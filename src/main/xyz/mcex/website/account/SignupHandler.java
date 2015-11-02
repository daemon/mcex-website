package xyz.mcex.website.account;

import io.vertx.core.MultiMap;
import xyz.mcex.website.internals.ClockReplacementCache;
import xyz.mcex.website.jade.handler.JadeFormHandler;

public class SignupHandler extends JadeFormHandler
{
  public SignupHandler()
  {
    super("assets/signup.jade");
  }

  @Override
  public void processFormData(MultiMap formData)
  {
  }
}
