package dove.util.ui.progress;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

//TODO look and feel

public class LoadingBar
        extends JPanel {
    private String unit;

    private double maxVal;

    private double curVal;

    private String title;

    private DecimalFormat format;

    private SpeedUtil speedUtil;

    public LoadingBar() {
        this("loading", "%", 100);
    }

    public LoadingBar(String title, String unit, double maxVal) {
        this.title = title;
        this.unit = unit;
        this.maxVal = maxVal;

        this.curVal = 0;

        speedUtil = new SpeedUtil();
        speedUtil.start();

        int nums = (int) Math.nextUp(Math.log10(maxVal));
        String formatString = "";
        for (int i = 0; i < nums; i++) {
            formatString += "#";
        }
        formatString += ".00";
        this.format = new DecimalFormat(formatString);
    }

    public void setMaxVal(double val) {
        maxVal = val;

        repaint();
    }

    public void setCurVal(double val) {
        curVal = val;

        speedUtil.valUpdated();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics arg) {
        Graphics2D g = (Graphics2D) arg;

        g.setColor(Color.black);

        g.drawString(title, 20, 20);

        g.setColor(getForeground());

        g.fillRect(20, 30, (int) ((getWidth() - 40) * (curVal / maxVal)), 20);

        g.drawRect(20, 30, getWidth() - 40, 20);

        g.setColor(Color.black);

        String speedInfo = format.format(curVal) + "/" + format.format(maxVal) + unit +
                " @speed: " + format.format(speedUtil.getSpeed()) + " " + unit + "/s";
        int infoWidth = g.getFontMetrics().stringWidth(speedInfo);

        g.drawString(speedInfo
                , getWidth() - infoWidth - 20, 70);
    }

    private class SpeedUtil
            implements Runnable {
        private double startVal;

        private long recentUpdate;

        private double speed = 0;

        public void start() {
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.setName("load bar speed dove.util");
            thread.start();
        }

        public synchronized void run() {
            while (true) {
                speed = (curVal - startVal) / (System.currentTimeMillis() - recentUpdate) * 1000;

                recentUpdate = System.currentTimeMillis();

                startVal = curVal;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        repaint();
                    }
                });

                try {
                    wait();
                }
                catch (InterruptedException e) {

                }
            }
        }

        public synchronized void valUpdated() {
            notifyAll();
        }

        public double getSpeed() {
            return speed;
        }
    }
}