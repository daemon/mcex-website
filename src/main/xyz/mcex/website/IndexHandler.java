package xyz.mcex.website;

import de.neuland.jade4j.Jade4J;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

public class IndexHandler implements Handler<RoutingContext>
{
  @Override
  public void handle(RoutingContext context)
  {
    try
    {
      context.response().setChunked(true).write(Jade4J.render("assets/index.jade", null)).end();
    } catch (IOException e)
    {
      LogManager.getLogger("website").error("Rendering index page failed");
    }
  }
}
