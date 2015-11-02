package xyz.mcex.website.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager
{
  private static DatabaseManager _instance = new DatabaseManager();
  private final JedisPool _jedisPool;
  private final ComboPooledDataSource _cpds;

  private DatabaseManager()
  {
    JedisPoolConfig cfg = new JedisPoolConfig();
    this._jedisPool = new JedisPool(cfg, "localhost");
    this._cpds = new ComboPooledDataSource();
    try
    {
      this._cpds.setDriverClass("com.mysql.jdbc.Driver");
    } catch (PropertyVetoException e)
    {
      LogManager.getLogger("website").error("Couldn't initialize MySQL connection pool");
    }

    this._cpds.setMaxPoolSize(8);
    this._cpds.setMinPoolSize(2);
    this._cpds.setMaxStatements(64);
    this._cpds.setMaxStatementsPerConnection(8);
  }

  private void setSqlConfig(JsonObject sqlConfig)
  {
    // jdbc:mysql://localhost:3306/db
    this._cpds.setJdbcUrl(sqlConfig.getString("jdbc_url"));
    this._cpds.setUser(sqlConfig.getString("sql_user"));
    this._cpds.setPassword(sqlConfig.getString("sql_password"));
  }

  public static DatabaseManager getInstance()
  {
    return _instance;
  }

  public Jedis getJedisResource()
  {
    return this._jedisPool.getResource();
  }

  public Connection getSqlConnection()
  {
    try
    {
      return this._cpds.getConnection();
    } catch (SQLException e)
    {
      LogManager.getLogger("website").error("Couldn't get SQL connection");
      return null;
    }
  }

  public void close(Connection conn)
  {
    try
    {
      conn.close();
    } catch (SQLException e)
    {
      LogManager.getLogger("website").error("Couldn't close connection");
    }
  }
}
