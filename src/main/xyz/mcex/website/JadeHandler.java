package xyz.mcex.website;

import com.sun.istack.internal.Nullable;
import de.neuland.jade4j.Jade4J;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Map;

public class JadeHandler implements Handler<RoutingContext>
{
  private final String _file;
  private final Map<String, Object> _model;

  public JadeHandler(String file)
  {
    this(file, null);
  }

  public JadeHandler(String file, @Nullable Map<String, Object> model)
  {
    this._file = file;
    this._model = model;
  }

  @Override
  public void handle(RoutingContext context)
  {
    try
    {
      context.response().setChunked(true).write(Jade4J.render(this._file, this._model)).end();
    } catch (IOException e)
    {
      LogManager.getLogger("website").error("Rendering" + this._file + " failed");
    }
  }
}
