package xyz.mcex.website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.mcex.website.account.SignupHandler;
import xyz.mcex.website.jade.handler.JadeHandler;

public class MainVerticle extends AbstractVerticle
{
  private void attachRoutes(Router router, Logger logger)
  {
    logger.info("Attaching handlers for endpoints");
    router.route("/").handler(new JadeHandler("assets/index.jade"));
    router.route("/signup").handler(new SignupHandler());
  }

  @Override
  public void start()
  {
    Logger logger = LogManager.getLogger("website");
    HttpServer server = this.vertx.createHttpServer();

    Router router = Router.router(this.vertx);
    this.attachRoutes(router, logger);

    Integer port = this.config().getInteger("port");
    if (port == null)
    {
      logger.error("Couldn't read port from config file!");
      throw new RuntimeException("Couldn't read port from config file!");
    }

    server.requestHandler(router::accept).listen(port.intValue());
    logger.info("Finished starting up server");
  }
}
