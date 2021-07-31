import io.reactivex.rxjava3.core.Observable;

import java.util.logging.Logger;

public class Case3Rx {

  private final Logger log = Logger.getLogger(this.getClass().getName());

  private Case3Rx() {
  }

  public Case3Rx testObservable() {
    Observable.just("video1", "video2", "video3")
      .subscribe(
        n -> log.info("watching: " + n)
      );
    return this;
  }

  public Case3Rx testMap() {
    Observable.range(0, 16)
      .map(Integer::toHexString)
      .subscribe(log::info);
    return this;
  }

  public Case3Rx testFlatMap() {
    Observable.range(0, 10)
      .flatMap(x -> Observable.range(0, 10)
        .map(y -> String.format("(%s,%s)", x, y))
      )
      .subscribe(log::info);
    return this;
  }

  public static Case3Rx create() {
    return new Case3Rx();
  }
}
