package xyz.mcex.website.account;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import xyz.mcex.website.jade.handler.JadeFormHandler;
import xyz.mcex.website.jade.handler.JadeHandler;

import java.util.HashMap;
import java.util.Map;

public class SignupHandler extends JadeFormHandler
{
  public SignupHandler()
  {
    super("assets/signup.jade");
  }

  @Override
  public void processFormData(MultiMap formData)
  {
    System.out.println(formData.toString());
    return;
  }
}
