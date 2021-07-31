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

public class Case6Rx {

  private final Logger log = Logger.getLogger(this.getClass().getName());
  private final Params params;

  private Case6Rx(Params params) {
    this.params = params;
  }
  
  private void writeOutputImage() throws IOException {
    ImageIO.write(params.outputImage, "jpg", params.outputImageFile);
  }

  public CompletableFuture<Case6Rx> execChangeImageColor() {
    var promise = new CompletableFuture<Case6Rx>();

    var tasks = Utils.splitYs(4, params.inputImage.getHeight()).stream()
      .map(ys -> Observable.just(this)
        .subscribeOn(Schedulers.computation())
        .flatMap(n -> this.writeImageFromYs(ys)))
      .collect(Collectors.toList());

    Observable.concat(
      sendMail(),
      Observable.merge(tasks),
      Observable.zip(callExternalAPI(), saveInDatabase(), (api, db ) -> {
        log.info(String.format("api: %s, db: %s", api, db));
        return this;
      }))
      .subscribe(
        n -> {},
        e -> e.printStackTrace(),
        () -> {
          writeOutputImage();
          log.info("Completed...");
          promise.complete(this);
        }
      );
    return promise;
  }

  private Observable<Case6Rx> writeImageFromYs(final int[] ys) {
    return Observable.create(emitter -> {
      for(int x = 0; x < params.inputImage.getWidth() && !emitter.isDisposed(); x++) {
        for(int y = ys[0]; y < ys[1] && !emitter.isDisposed(); y++) {
          int rgb = params.inputImage.getRGB(x, y);
          var color = new Color(rgb, true);
          if(
            Math.abs(color.getRed() - params.targetColor.getRed()) <= params.tolerance &&
            Math.abs(color.getGreen() - params.targetColor.getGreen()) <= params.tolerance &&
            Math.abs(color.getBlue() - params.targetColor.getBlue()) <= params.tolerance
          ) {
            rgb = params.newColor.getRGB();
          }
          params.outputImage.setRGB(x, y, rgb);
          emitter.onNext(this);
        }
      }
      emitter.onComplete();
    });
  }

  private Observable<Case6Rx> sendMail() {
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
    });
  }
  public static Case6Rx create(Params params) {
    return new Case6Rx(params);
  }
}
