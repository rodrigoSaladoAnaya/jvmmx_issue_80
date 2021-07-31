import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Case5Rx {

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private final Params params;

  private Case5Rx(Params params) {
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

  private Point setRGB(Point p) {
    params.outputImage.setRGB(p.x, p.y, p.rgb);
    return p;
  }

  private void writeOutputImage() throws IOException {
    ImageIO.write(params.outputImage, "jpg", params.outputImageFile);
  }

  private Observable<Point> getXYs(final int[] ys) {
    return Observable.create(emitter -> {
      for(int x = 0; x < params.inputImage.getWidth() && !emitter.isDisposed(); x++) {
        for(int y = ys[0]; y < ys[1] && !emitter.isDisposed(); y++) {
          emitter.onNext(Point.create(x, y));
        }
      }
      emitter.onComplete();
    });
  }

  public CompletableFuture<Case5Rx> execChangeImageColor() {
    var promise = new CompletableFuture<Case5Rx>();

    var tasks = Utils.splitYs(4, params.inputImage.getHeight()).stream()
      .map(ys -> Observable.just(this)
        .subscribeOn(Schedulers.newThread())
        .flatMap(n -> getXYs(ys))
        .map(this::getRGB)
        .map(this::getColor)
        .map(this::changeRGB)
        .map(this::setRGB)
        //.doOnNext(n -> log.info("-> " + n))
        .map(n -> this)
      )
      .collect(Collectors.toList());

    Observable.concat(
      sendMail(),
      Observable.merge(tasks),
      Observable.zip(callExternalAPI(), saveInDatabase(), (api, db) -> {
        log.info(String.format("api: %s, db: %s", api, db));
        return this;
      })
    )
      .subscribe(
        n -> {},
        e -> e.printStackTrace(),
        () -> {
          writeOutputImage();
          log.info(String.format("[%s] Completed...", Thread.currentThread().getName()));
          promise.complete(this);
        }
      );

    return promise;
  }

  private Observable<Case5Rx> sendMail() {
    return Observable.create(emitter -> {
      log.info("[START] >> sendMail");
      CompletableFuture.runAsync(() -> {
        Utils.sleep(2);
        log.info("[DONE] << sendMail");
      });
      emitter.onNext(this);
      emitter.onComplete();
    });
  }


  private Observable<String> callExternalAPI() {
    return Observable.create(emitter -> {
      log.info("[START] >> callExternalAPI");
      CompletableFuture.runAsync(() -> {
        log.info("[DONE] << callExternalAPI");
        emitter.onNext(UUID.randomUUID().toString());
        emitter.onComplete();
      });
    });
  }

  private Observable<Integer> saveInDatabase() {
    return Observable.create(emitter -> {
      log.info("[START] >> saveInDatabase");
      Utils.sleep(2);
      log.info("[DONE] << saveInDatase");
      emitter.onNext(ThreadLocalRandom.current().nextInt());
      emitter.onComplete();
    });
  }

  public static Case5Rx create(Params params) {
    return new Case5Rx(params);
  }
}
