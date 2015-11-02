package xyz.mcex.website.account;

import xyz.mcex.website.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDatabase
{
  private static Pattern userPattern = Pattern.compile("^[A-z0-9]+$");
  public static boolean isUsernameValid(String username)
  {
    if (username.length() <= 2 || username.length() > 26)
      return false;

    Matcher m = userPattern.matcher(username);
    return m.matches();
  }

  public static boolean isPasswordValid(String password)
  {
    if (password.length() <= 8 || password.length() > 64)
      return false;

    boolean hasCapital = false;
    boolean hasLowercase = false;
    for (int i = 0; i < password.length(); ++i)
    {
      char character = password.charAt(i);
      if (character <= 'Z' && character >= 'A')
        hasCapital = true;
      else if (character <= 'z' && character >= 'a')
        hasLowercase = true;
    }

    return hasCapital && hasLowercase;
  }

  public static CreateAccountResponse createUser(CreateAccountRequest request) throws SQLException
  {
    Connection connection = null;
    PreparedStatement nameCheckStmt = null;
    ResultSet set = null;
    PreparedStatement emailCheckStmt = null;
    PreparedStatement insertUserStmt = null;
    CreateAccountResponse response = new CreateAccountResponse();

    try
    {
      connection = DatabaseManager.getInstance().getSqlConnection();

      nameCheckStmt = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
      nameCheckStmt.setString(0, request.username);

      set = nameCheckStmt.executeQuery();
      if (set.next())
        response.setUserTaken();

      set.close();

      emailCheckStmt = connection.prepareStatement("SELECT id FROM users WHERE email = ?");
      emailCheckStmt.setString(0, request.email);
      set = emailCheckStmt.executeQuery();
      if (set.next())
        response.setEmailTaken();

      response.userUnsatisfied = !isUsernameValid(request.username);
      response.passwordUnsatisfied = !isPasswordValid(request.password);

      if (response.emailTaken || response.userTaken || response.userUnsatisfied || response.passwordUnsatisfied)
        return response;

      String salt = AuthenticationStore.getInstance().createSalt();
      String hash = AuthenticationStore.getInstance().hash(request.password, salt);

      insertUserStmt = connection.prepareStatement("INSERT INTO users (username, email, password, salt) VALUES (?, ?, ?, ?)");
      insertUserStmt.setString(0, request.username);
      insertUserStmt.setString(1, request.email);
      insertUserStmt.setString(2, hash);
      insertUserStmt.setString(3, salt);
      insertUserStmt.execute();
      response.success = true;
      return response;
    } finally {
      if (set != null)
        set.close();
      if (nameCheckStmt != null)
        nameCheckStmt.close();
      if (emailCheckStmt != null)
        emailCheckStmt.close();
      if (insertUserStmt != null)
        insertUserStmt.close();
      if (connection != null)
        connection.close();
    }
  }
}
