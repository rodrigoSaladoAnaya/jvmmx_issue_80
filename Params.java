import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class  Params {

  final BufferedImage inputImage;
  final BufferedImage outputImage;
  final Color targetColor;
  final Color newColor;
  final int tolerance;
  final File outputImageFile;

  public Params(String args[]) throws IOException {
    String inputFileType = args[0].toLowerCase();
    String inputImagePath = args[1];
    String outputFileName = null;
    BufferedImage inputImageTmp = null;
    if(inputFileType.equals("local")) {
      File inputImageFile = new File(inputImagePath);
      inputImageTmp = ImageIO.read(inputImageFile);
      outputFileName = inputImageFile.getName();
    } else if(inputFileType.equals("url")) {
      var url = new URL(inputImagePath);
      inputImageTmp = ImageIO.read(url);
      String[] formatParts = url.getFile().split("\\.");
      outputFileName = String.format("url-temp.%s", formatParts[formatParts.length - 1]);
    }
    inputImage = inputImageTmp;
    outputImageFile = new File(String.format("./resources/out/out-%s", outputFileName));
    outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

    targetColor = new Color(Integer.decode("0x" + args[2]));
    newColor = new Color(Integer.decode("0x" + args[3]));
    tolerance = Integer.parseInt(args[4]);
  }

  public static Params create(String args[]) throws IOException {
    return new Params(args);
  }

}
