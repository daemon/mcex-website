package xyz.mcex.website.account;

import io.vertx.core.json.JsonObject;
import xyz.mcex.website.internals.Nullable;

public class CreateAccountResponse
{
  public boolean passwordUnsatisfied = false;
  public boolean userTaken = false;
  public boolean emailTaken = false;
  public boolean userUnsatisfied = false;
  public boolean success = false;

  public CreateAccountResponse setPasswordUnsatisfied()
  {
    this.passwordUnsatisfied = true;
    return this;
  }

  public CreateAccountResponse setUserTaken()
  {
    this.userTaken = true;
    return this;
  }

  public CreateAccountResponse setEmailTaken()
  {
    this.emailTaken = true;
    return this;
  }

  public CreateAccountResponse setUserUnsatisfied()
  {
    this.userUnsatisfied = true;
    return this;
  }

  public JsonObject toJsonObject()
  {
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("success", !this.emailTaken && !this.userTaken && !this.passwordUnsatisfied);
    jsonObject.put("password_unsatisfied", this.passwordUnsatisfied);
    jsonObject.put("email_taken", this.emailTaken);
    jsonObject.put("user_taken", this.userTaken);
    jsonObject.put("user_unsatisfied", this.userUnsatisfied);
    return jsonObject;
  }
}
