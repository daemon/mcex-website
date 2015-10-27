package xyz.mcex.website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle
{
  public void start()
  {
    Logger logger = LogManager.getLogger("website.core");
    HttpServer server = this.vertx.createHttpServer();

    Router router = Router.router(this.vertx);
    Route route = router.route().path("/");

    route.handler(routingContext -> {

    });

    Integer port = this.config().getInteger("port");
    if (port == null)
    {
      logger.error("Couldn't read port from config file!");
      return;
    }

    server.listen(port);
    logger.info("Finished starting up server");
  }
}
