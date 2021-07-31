import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Main {

  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(String [] args) throws IOException, ExecutionException, InterruptedException {
    long ini = System.currentTimeMillis();
    var params = Params.create(args);

    Case1Java.create(params).exec();

    long end = System.currentTimeMillis();
    log.info(String.format("Fin: %sms", end-ini));
  }

}
