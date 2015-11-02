package xyz.mcex.website.account;

import io.vertx.core.json.JsonObject;

public class CreateAccountRequest
{
  public final String username;
  public final String password;
  public final String email;

  public CreateAccountRequest(String username, String password, String email)
  {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  public JsonObject toJsonObject()
  {
    JsonObject object = new JsonObject();
    object.put("username", this.username);
    object.put("password", this.password);
    object.put("email", this.email);

    return object;
  }
}
