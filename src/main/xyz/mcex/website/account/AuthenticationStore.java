package xyz.mcex.website.account;

import org.apache.logging.log4j.LogManager;
import redis.clients.jedis.Jedis;
import xyz.mcex.website.db.DatabaseManager;
import xyz.mcex.website.internals.ClockReplacementCache;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class AuthenticationStore
{
  private static AuthenticationStore _instance = new AuthenticationStore();
  private final ClockReplacementCache<String, String> _tokenToUserCache;

  /** Used for token */
  private static SecureRandom secRandom = new SecureRandom();

  /** Used for salt */
  private static Random random = new Random(System.currentTimeMillis());

  private static final int TOKEN_TTL = 172800;

  private AuthenticationStore()
  {
    this._tokenToUserCache = new ClockReplacementCache<>(128);
  }

  private String createToken()
  {
    return new BigInteger(130, secRandom).toString(32);
  }

  public String hash(String password, String salt)
  {
    try
    {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.update(password.getBytes());
      digest.update(salt.getBytes());
      return String.format("%064x", new BigInteger(1, digest.digest()));
    } catch (NoSuchAlgorithmException e)
    {
      LogManager.getLogger("website").error("Couldn't initialize hash digest");
    }

    return null;
  }

  public String createSalt()
  {
    return new BigInteger(64, random).toString(32);
  }

  public boolean isAuthenticated(String username, String token)
  {
    if (this._tokenToUserCache.get(token) != null && this._tokenToUserCache.get(token).equals(username))
      return true;

    Jedis conn = DatabaseManager.getInstance().getJedisResource();
    String tok = conn.get(username);
    if (tok != null)
      if (tok.equals(token))
      {
        this._tokenToUserCache.put(username, tok);
        conn.close();
        return true;
      }

    conn.close();
    return false;
  }

  private String querySalt(String username) throws SQLException
  {
    Connection conn = DatabaseManager.getInstance().getSqlConnection();
    if (conn == null)
      throw new SQLException();

    ResultSet set = null;
    PreparedStatement stmt = null;

    try
    {
      stmt = conn.prepareStatement("SELECT salt FROM users WHERE username = ?");
      set = stmt.executeQuery();
      if (set.next())
      {
        String salt = set.getString(1);
        return salt;
      }

      throw new SQLException("Couldn't find username");
    } finally {
      if (set != null)
        set.close();
      if (stmt != null)
        stmt.close();
      DatabaseManager.getInstance().close(conn);
    }
  }

  public void renew(String token)
  {
    Jedis jedis = DatabaseManager.getInstance().getJedisResource();
    jedis.expire(token, TOKEN_TTL);
    String username = jedis.get(token);
    jedis.close();

    if (!this._tokenToUserCache.renew(token) && username != null)
      this._tokenToUserCache.put(token, username);
  }

  public UserAccount authenticate(String username, String password) throws SQLException
  {
    Connection conn = DatabaseManager.getInstance().getSqlConnection();
    if (conn == null)
      throw new SQLException("Couldn't get connection");

    PreparedStatement authUserStmt = null;
    ResultSet set = null;
    Jedis jedis = null;

    try
    {
      authUserStmt = conn.prepareStatement("SELECT id, email FROM users WHERE username = ? AND password = ?");
      String hash = this.hash(password, this.querySalt(username));
      authUserStmt.setString(0, username);
      authUserStmt.setString(1, hash);

      set = authUserStmt.executeQuery();
      if (!set.next())
        return null;

      Long userId = set.getLong("id");
      String email = set.getString("email");
      String token = this.createToken();

      jedis = DatabaseManager.getInstance().getJedisResource();
      jedis.set(token, username);

      return new UserAccount(username, userId, token, email);
    } finally {
      if (authUserStmt != null)
        authUserStmt.close();
      if (set != null)
        set.close();
      if (jedis != null)
        jedis.close();
    }
  }

  public static AuthenticationStore getInstance()
  {
    return _instance;
  }
}
