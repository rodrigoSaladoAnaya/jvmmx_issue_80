import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

  public static List<int[]> splitYs(final int pool, final int height) {
    var list = new ArrayList<int[]>();
    final int partSize = height / pool;
    for(int i = 0; i < pool; i++) {
      int y0 = i * partSize;
      int yn = y0 + partSize;
      list.add(new int[] {y0, yn});
    }
    list.add(new int[] {partSize * pool, height});
    return list;
  }

  public static void sleep(TimeUnit t, int timeout) {
    try {
      t.sleep(timeout);
    } catch (InterruptedException ignore) {}
  }

  public static void sleep(int seconds) {
    sleep(TimeUnit.SECONDS, seconds);
  }
}
