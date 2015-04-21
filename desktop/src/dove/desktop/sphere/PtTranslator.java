package dove.desktop.sphere;

import java.awt.*;

import static java.lang.Math.atan;
import static java.lang.Math.tan;

public class PtTranslator {
    public static Point translate(double longitude, double latitude,
                                  double longitudeCenter, double latitudeCenter, double radius, Dimension screen) {
        return new Point((int) (screen.width / 2 + atan(longitude - longitudeCenter) * radius),
                (int) (screen.height / 2 + atan(latitude - latitudeCenter) * radius));
    }

    public static Coordinate translate(Point pt, double longitudeCenter, double latitudeCenter,
                                       double radius, Dimension screen) {
        return new Coordinate(tan((pt.getX() - screen.getWidth() / 2) / radius) - longitudeCenter,
                tan((pt.getY() - screen.getHeight() / 2) / radius) - latitudeCenter);
    }
}
