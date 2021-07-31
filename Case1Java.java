import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

public class Case1Java {

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private final Params params;

  private Case1Java(Params params) {
    this.params = params;
  }

  public void exec() throws IOException {
    for(int x = 0; x < params.inputImage.getWidth(); x++) {
      for(int y = 0; y < params.inputImage.getHeight(); y++) {
        int rgb = params.inputImage.getRGB(x, y);
        var color = new Color(rgb, true);
        if(
          Math.abs(color.getRed() - params.targetColor.getRed()) <= params.tolerance &&
          Math.abs(color.getGreen() - params.targetColor.getGreen()) <= params.tolerance &&
          Math.abs(color.getBlue() - params.targetColor.getBlue()) <= params.tolerance
        ) {
          rgb = params.newColor.getRGB();
          //log.info(String.format("(%s,%s) #%s", x, y, Integer.toHexString(rgb)));
        }
        params.outputImage.setRGB(x, y, rgb);
      }
    }
    ImageIO.write(params.outputImage, "jpg", params.outputImageFile);
  }

  public static Case1Java create(Params params) {
    return new Case1Java(params);
  }
}

