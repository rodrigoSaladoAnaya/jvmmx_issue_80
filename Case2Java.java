import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Case2Java {

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private final Params params;
  private final int pool = 4;
  private final ExecutorService service = Executors.newFixedThreadPool(pool);

  private Case2Java(Params params) {
    this.params = params;
  }

  public void exec() throws IOException, InterruptedException, ExecutionException {
    Function<int[], Callable<Void>> callableFunction = ys -> () -> writeImageFromYs(ys[0], ys[1]);

    var tasks = Utils.splitYs(pool, params.inputImage.getHeight()).stream()
    .map(callableFunction)
    .collect(Collectors.toList());

    var futures = service.invokeAll(tasks);
    for(var f : futures) {
      f.get();
    }

    ImageIO.write(params.outputImage, "jpg", params.outputImageFile);
    service.shutdown();
  }

  private Void writeImageFromYs(final int y0, final int yn) {
    for(int x = 0; x < params.inputImage.getWidth(); x++) {
      for(int y = y0; y < yn; y++) {
        int rgb = params.inputImage.getRGB(x, y);
        Color color = new Color(rgb, true);
        if(
          Math.abs(color.getRed() - params.targetColor.getRed()) <= params.tolerance &&
          Math.abs(color.getGreen() - params.targetColor.getGreen()) <= params.tolerance &&
          Math.abs(color.getBlue() - params.targetColor.getBlue()) <= params.tolerance
        ) {
          rgb = params.newColor.getRGB();
          //log.info(String.format("[%s] (%s,%s) #%s", Thread.currentThread().getName(), x, y, Integer.toHexString(rgb)));
        }
        params.outputImage.setRGB(x, y, rgb);
      }
    }
    return null;
  }

  public static Case2Java create(Params params) {
    return new Case2Java(params);
  }
}

