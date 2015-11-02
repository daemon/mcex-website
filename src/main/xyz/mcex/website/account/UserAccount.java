package xyz.mcex.website.account;

import io.vertx.core.json.JsonObject;

public class UserAccount
{
  public final String username;
  public final Long userId;
  public final String token;
  public final String email;

  public UserAccount(String username, Long userId, String token, String email)
  {
    this.username = username;
    this.userId = userId;
    this.token = token;
    this.email = email;
  }

  public static UserAccount from(JsonObject jsonObject)
  {
    String username = jsonObject.getString("username");
    Long userId = jsonObject.getLong("userId");
    String email = jsonObject.getString("email");
    String token = jsonObject.getString("token");

    return new UserAccount(username, userId, token, email);
  }

  public JsonObject toJsonObject()
  {
    JsonObject json = new JsonObject();
    json.put("username", this.username);
    json.put("token", this.token);
    json.put("userId", this.userId);
    json.put("email", this.email);
    return json;
  }
}
