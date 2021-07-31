import io.reactivex.rxjava3.core.Observable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Case4Rx {

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private final Params params;

  private Case4Rx(Params params) {
    this.params = params;
  }

  private Point getRGB(Point p) {
    p.rgb = params.inputImage.getRGB(p.x, p.y);
    return p;
  }

  private Point getColor(Point p) {
    p.color = new Color(p.rgb, true);
    return p;
  }

  private Point changeRGB(Point p) {
    if(
      Math.abs(p.color.getRed() - params.targetColor.getRed()) <= params.tolerance &&
      Math.abs(p.color.getGreen() - params.targetColor.getGreen()) <= params.tolerance &&
      Math.abs(p.color.getBlue() - params.targetColor.getBlue()) <= params.tolerance
    ) {
      p.rgb = params.newColor.getRGB();
    }
    return p;
  }

  private Point darkerRGB(Point p) {
    Color c = new Color(p.rgb, true);
    for (int i = 0; i < params.tolerance; i++) {
      c = c.darker();
    }
    p.rgb = c.getRGB();
    return p;
  }

  private Point brighterRGB(Point p) {
    Color c = new Color(p.rgb, true);
    for (int i = 0; i < params.tolerance; i++) {
      c = c.brighter();
    }
    p.rgb = c.getRGB();
    return p;
  }

  private Point setRGB(Point p) {
    params.outputImage.setRGB(p.x, p.y, p.rgb);
    return p;
  }


  private void writeOutputImage() throws IOException {
    ImageIO.write(params.outputImage, "jpg", params.outputImageFile);
  }

  private Observable<Point> getXYs() {
    return Observable.create(emitter -> {
      for(int x = 0; x < params.inputImage.getWidth() && !emitter.isDisposed(); x++) {
        for (int y = 0; y < params.inputImage.getHeight() && !emitter.isDisposed(); y++) {
          emitter.onNext(Point.create(x, y));
        }
      }
      emitter.onComplete();
    });
  }

  public Case4Rx execChangeImageColor() {
    Observable.just(this)
      .flatMap(Case4Rx::getXYs)
      .map(this::getRGB)
      .map(this::getColor)
      .map(this::changeRGB)
      .map(this::setRGB)
      .subscribe(
        n -> {},
        e -> e.printStackTrace(),
        () -> {
          writeOutputImage();
          log.info("Completed...");
        }
      );
    return this;
  }

  public Case4Rx execDarkerImageColor() {
    Observable.just(this)
      .flatMap(Case4Rx::getXYs)
      .map(this::getRGB)
      .map(this::getColor)
      .map(this::darkerRGB)
      .map(this::setRGB)
      .subscribe(
        n -> {},
        e -> e.printStackTrace(),
        () -> {
          writeOutputImage();
          log.info("Completed...");
        }
      );
    return this;
  }

  public Case4Rx execBrighterImageColor() {
    Observable.just(this)
      .flatMap(Case4Rx::getXYs)
      .map(this::getRGB)
      .map(this::getColor)
      .map(this::brighterRGB)
      .map(this::setRGB)
      .subscribe(
        n -> {},
        e -> e.printStackTrace(),
        () -> {
          writeOutputImage();
          log.info("Completed...");
        }
      );
    return this;
  }

  public static Case4Rx create(Params params) {
    return new Case4Rx(params);
  }
}
