package xyz.mcex.website.jade.handler;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import xyz.mcex.website.internals.Nullable;

import java.util.Map;

public abstract class JadeFormHandler extends JadeHandler
{
  public JadeFormHandler(String file)
  {
    super(file);
  }

  public JadeFormHandler(String file, @Nullable Map<String, Object> model)
  {
    super(file, model);
  }

  @Override
  public void handle(RoutingContext context)
  {
    context.request().setExpectMultipart(true);
    context.request().endHandler(new Handler<Void> () {
      @Override
      public void handle(Void event)
      {
        JadeFormHandler.this.processFormData(context.request().formAttributes());
        JadeFormHandler.super.handle(context);
      }
    });
  }

  public abstract void processFormData(MultiMap formData);
}
