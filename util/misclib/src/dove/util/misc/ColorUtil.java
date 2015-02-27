package dove.util.misc;

import java.awt.*;

public class ColorUtil {
    public static Color avgComplementaryColor(Color... colors) {
        int red = 0;
        int green = 0;
        int blue = 0;

        int colorCount = colors.length;

        for (Color color : colors) {
            if (color == null) {
                colorCount--;
                continue;
            }

            red += color.getRed();
            blue += color.getBlue();
            green += color.getGreen();
        }

        red /= colorCount;
        blue /= colorCount;
        green /= colorCount;

        red ^= 0xFF;
        blue ^= 0xFF;
        green ^= 0xFF;

        return new Color(red, green, blue);
    }
}
