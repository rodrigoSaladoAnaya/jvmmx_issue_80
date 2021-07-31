import java.awt.*;

class Point {

  final int x;
  final int y;
  int rgb;
  Color color;
  Object pixelData;

  private Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Point create(int x, int y) {
    return new Point(x, y);
  }

  @Override
  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}
