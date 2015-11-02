package xyz.mcex.website.jade.handler;

import de.neuland.jade4j.Jade4J;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import xyz.mcex.website.internals.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JadeHandler implements Handler<RoutingContext>
{
  private final String _file;
  private final Map<String, Object> _model;
  private boolean _redirect = false;
  private String _redirectUrl = "";

  public JadeHandler(String file)
  {
    this(file, null);
  }

  public JadeHandler(String file, @Nullable Map<String, Object> model)
  {
    this._file = file;
    if (model == null)
      this._model = new HashMap<>();
    else
      this._model = model;
  }

  public Map<String, Object> model()
  {
    return this._model;
  }

  public void setRedirect(String url)
  {
    this._redirect = true;
    this._redirectUrl = url;
  }

  @Override
  public void handle(RoutingContext context)
  {
    try
    {
      this.processRequest(context.request());
      if (!_redirect)
        context.response().setChunked(true).write(Jade4J.render(this._file, this._model)).end();
      else
        context.response().setChunked(true).setStatusCode(301).putHeader("Location", this._redirectUrl);
    } catch (IOException e)
    {
      LogManager.getLogger("website").error("Opening" + this._file + " failed");
    }
  }

  public void processRequest(HttpServerRequest request)  { return; }
}
