package dove.util.ui.progress;

import javax.swing.*;
import java.awt.*;

/**
 * provides a component
 * that displays the default circular
 * loading sign
 */
public class CirculateLoading
        extends JPanel
        implements Runnable {
    /**
     * the user speed this speed can be defined by
     * the user here (@see dove.util.ui.progress.CirculateLoading$Speed)
     */
    public static int     user    = 500;
    /**
     * if true, the dots are moving
     * can be set false to kill the
     * moving thread
     */
    public        boolean runLoad = true;
    /**
     * the speed at which the dots are
     * moving (@see dove.util.ui.progress.CirculateLoading$Speed)
     */
    public SPEED   speed;
    /**
     * the number of visible dots
     */
    public int     visibleDots;
    /**
     * the number of total dots
     * for example totalDots == 4 means
     * the dots move a quarter circle every clocktick
     */
    public int     totalDots;
    /**
     * determines wether the dots move
     * clockwise or the revers way
     */
    public boolean clockwise;
    /**
     * the position of the first dot
     * start with the first dot upside
     * value in rad
     */
    private double radPos = Math.PI * 0.5;
    /**
     * the percentage of the whole panel
     * the circle upon which the dots move uses
     * (take the size of the dots in calculation
     * they will be only displayed half if
     * radSize == 1)
     */
    private double radSize;

    /**
     * the real radSize
     * if this is -1, radSize is used
     */
    private int radSizeReal;

    /**
     * the size at which dots will step
     * calculated in the constructor
     */
    private double stepSize;

    /**
     * the color of the dots that are displayed
     * the further the dot is away from the first
     * dot, the brighter its color is
     */
    private Color[] ptColors;

    /**
     * the radius of the dots
     */
    private int ptSize;

    /**
     * creates a default loading
     * token
     */
    public CirculateLoading() {
        this(8, 4, 6, 0.8, true, SPEED.MEDIUM);
    }

    /**
     * sets up a circulate loading token
     * only difference to the next constructor:
     * the radSize is absolute (in pixel)
     *
     * @param totalDots
     * @param visibleDots
     * @param dotSize
     * @param radSize
     * @param clockwise
     * @param speed
     */
    public CirculateLoading(int totalDots, int visibleDots, int dotSize, int radSize
            , boolean clockwise, SPEED speed) {
        this(totalDots, visibleDots, dotSize, (double) radSize, clockwise, speed);

        radSizeReal = radSize;
    }

    /**
     * creates a circulateloading sign from the
     * give arguments and sets up all constants
     * needed to display
     *
     * @param totalDots   number of dots the circle consists off
     * @param visibleDots number of visible dots
     * @param dotSize     radius of a dot
     * @param radSize     percentage of how much of the space of the
     *                    component is used by the circle
     * @param clockwise   true if the circle spins clockwise
     * @param speed       the speed the clock spins with
     */
    public CirculateLoading(int totalDots, int visibleDots, int dotSize, double radSize
            , boolean clockwise, SPEED speed) {
        this.totalDots = totalDots;
        this.visibleDots = visibleDots;
        this.clockwise = clockwise;
        this.speed = speed;
        this.ptSize = dotSize;
        this.radSize = radSize;
        this.radSizeReal = -1;

        stepSize = Math.PI * 2 / totalDots;
        if (!clockwise)
            stepSize *= -1;

        createDotColors();

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("loading circle dove.util");
        thread.start();
    }

    /**
     * the foreground is set for this component
     * and all dotcolors are updated
     * (@see createDotColors())
     *
     * @param color the new foreground color
     */
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);

        createDotColors();
    }

    /**
     * creates the dotColors for each dot
     * by the given foreground color
     * <p>
     * every dot-color is a bit more brighter
     * than the previous one
     * <p>
     * so color[visibledot colors + 1] would be white (255 , 255 , 255)
     * if existent
     */
    private void createDotColors() {
        if (visibleDots == 0)
            return;

        if (visibleDots == 1) {
            ptColors = new Color[]{getForeground()};
            return;
        }

        ptColors = new Color[visibleDots];
        int red = getForeground().getRed();
        int blue = getForeground().getBlue();
        int green = getForeground().getGreen();

        int redStep = (255 - red) / visibleDots;
        int blueStep = (255 - blue) / visibleDots;
        int greenStep = (255 - green) / visibleDots;

        for (int i = 0; i < visibleDots; i++) {
            ptColors[i] =
                    new Color(red + redStep * i,
                            green + greenStep * i,
                            blue + blueStep * i);
        }
    }

    /**
     * displays every dot
     * <p>
     * dots are calculated from the dotpos via
     * unit circle
     *
     * @param g a graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());

        g.fillRect(0, 0, getWidth(), getHeight());

        double curDot = radPos;

        double halfWidth = getWidth() / 2;
        double halfHeight = getHeight() / 2;

        for (int i = 0; i < visibleDots; i++) {
            g.setColor(ptColors[i]);

            int x;
            int y;

            if (radSizeReal == -1) {
                x = (int) (Math.cos(curDot) * halfWidth * radSize + halfWidth) - ptSize / 2;
                y = (int) (Math.sin(curDot) * halfHeight * radSize + halfHeight) - ptSize / 2;
            }
            else {
                x = (int) (Math.cos(curDot) * radSizeReal + halfWidth) - ptSize / 2;
                y = (int) (Math.sin(curDot) * radSizeReal + halfHeight) - ptSize / 2;
            }

            g.fillOval(x, y, ptSize, ptSize);

            curDot -= stepSize;
        }
    }

    /**
     * moves the position of the first dot
     * the specified stepSize
     * <p>
     * and repaints
     *
     * @see CirculateLoading#radPos
     * @see CirculateLoading#stepSize
     */
    @Override
    public void run() {
        while (runLoad) {
            radPos += stepSize;

            if (radPos == Math.PI * 2)
                radPos = 0;

            repaint();

            try {
                Thread.sleep(speed.waitTime);
            }
            catch (InterruptedException ignored) {

            }
        }
    }

    /**
     * defines the speed of the dots
     * <p>
     * the higher the value, the longer the time
     * between repaints/the slower the loading icon
     */
    public enum SPEED {
        USER(user),
        FAST(100),
        MEDIUM(200),
        SLOW(500);

        private int waitTime;

        private SPEED(int wait) {
            waitTime = wait;
        }
    }
}