package dove.util.ui.magnifier;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * provides a simple magnifier to show
 * a specific part of the screen with increased size
 */

public class Magnifier
        extends JFrame
        implements MouseMotionListener,
        MouseListener,
        ComponentListener,
        MouseWheelListener {
    private double magnificationFactor;

    private BufferedImage displayInMagnifier;

    private Component owner;

    private int s_x, s_y, s_width, s_height;

    private JPopupMenu popupMenu;
    private ArrayList<MagnifierUpdateListener> listeners = new ArrayList<>();

    public Magnifier(int width, int height, double magnificationFactor, Component owner) {
        super("Magnifier");

        if (magnificationFactor == 0)
            throw new IllegalArgumentException("magnificationfactor must not be 0");

        //set local variables
        this.magnificationFactor = magnificationFactor;

        this.owner = owner;

        s_x = 0;
        s_y = 0;
        s_width = 0;
        s_height = 0;

        //create magnifier
        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(displayInMagnifier, 0, 0, null);
            }
        };
        add(panel);

        //place the magnifier over the mouse
        Point p = MouseInfo.getPointerInfo().getLocation();
        setBounds(p.x - width / 2, p.y - height / 2, width, height);

        //add listeners
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);

        //add popupmenu
        popupMenu = new JPopupMenu();

        JMenuItem resize = new JMenuItem("Resize");
        resize.addActionListener(e ->
        {
            popupMenu.setVisible(false);

            JTextField tf_w = new JFormattedTextField(new NumberFormatter());
            JTextField tf_h = new JFormattedTextField(new NumberFormatter());
            tf_w.setPreferredSize(new Dimension(50, (int) tf_w.getPreferredSize().getHeight()));
            tf_h.setPreferredSize(new Dimension(50, (int) tf_w.getPreferredSize().getHeight()));

            JPanel resizePanel = new JPanel();
            resizePanel.add(new JLabel("width: "));
            resizePanel.add(tf_w);
            resizePanel.add(new JLabel(" x height: "));
            resizePanel.add(tf_h);

            if (JOptionPane.showConfirmDialog(this, resizePanel, "Resize Magnifier", JOptionPane.OK_CANCEL_OPTION)
                    != JOptionPane.OK_OPTION)
                return;

            Integer _w = Integer.parseInt(tf_w.getText());
            Integer _h = Integer.parseInt(tf_h.getText());

            setSize(_w, _h);
            refresh();
        });
        popupMenu.add(resize);

        JMenuItem zoom = new JMenuItem("Change Magnificationrate");
        zoom.addActionListener(e -> {
            popupMenu.setVisible(false);

            JTextField tf_r = new JFormattedTextField(new NumberFormatter(NumberFormat.getNumberInstance()));
            tf_r.setPreferredSize(new Dimension(50, tf_r.getPreferredSize().height));

            JPanel rate = new JPanel();
            rate.add(new JLabel("Magnification rate"));
            rate.add(tf_r);

            if (JOptionPane.showConfirmDialog(this, rate, "Change Magnificationrate", JOptionPane.OK_CANCEL_OPTION)
                    != JOptionPane.YES_OPTION)
                return;

            setMagnificationFactor(Double.parseDouble(tf_r.getText()));
        });
        popupMenu.add(zoom);


        JMenuItem quit = new JMenuItem("Close Magnifier");
        quit.addActionListener(e ->
        {
            setVisible(false);
            dispose();
            popupMenu.setVisible(false);
        });
        popupMenu.add(quit);

        //create frame
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
    }

    public Magnifier(int width, int height, Component owner) {
        this(width, height, 2.0, owner);
    }

    public Magnifier(double magnificationFactor, Component owner) {
        this(300, 200, magnificationFactor, owner);
    }

    ////////////////////////////////////////////////////////////////
    // getter / setter
    ////////////////////////////////////////////////////////////////

    public Magnifier(Component owner) {
        this(2.0, owner);
    }

    public double getMagnificationFactor() {
        return magnificationFactor;
    }

    public void setMagnificationFactor(double magnificationFactor) {
        this.magnificationFactor = magnificationFactor;
    }

    /////////////////////////////////////////////////////////////////
    // ui
    /////////////////////////////////////////////////////////////////

    public void setOwner(Component owner) {
        this.owner = owner;
    }

    public Rectangle getDisplayedRectangle() {
        return new Rectangle(s_x, s_y, s_width, s_height);
    }

    /////////////////////////////////////////////////////////////////
    // magnifierupdatelistener
    /////////////////////////////////////////////////////////////////

    private void updateMagnifiedImg() {
        //screen capture of the frame that invokes this magnifier
        BufferedImage screen = new BufferedImage(owner.getWidth(), owner.getHeight(), BufferedImage.TYPE_INT_ARGB);
        owner.paint(screen.getGraphics());

        //width of the screen to capture
        s_width = (int) (getWidth() / magnificationFactor);
        s_height = (int) (getHeight() / magnificationFactor);

        //center of the screen to capture
        s_x = getLocationOnScreen().x - owner.getLocationOnScreen().x + getWidth() / 2;
        s_y = getLocationOnScreen().y - owner.getLocationOnScreen().y + getWidth() / 2;

        //position of the screen to capture
        s_x -= s_width / 2;
        s_y -= s_height / 2;

        //expand to the size of this component
        BufferedImage result = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) result.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(screen, 0, 0, getWidth(), getHeight(), s_x, s_y, s_x + s_width, s_y + s_height, null);
        g.dispose();

        displayInMagnifier = result;
    }

    public void addMagnifierUpdateListener(MagnifierUpdateListener listener) {
        listeners.add(listener);
    }

    protected void fireMagnifierUpdated() {
        listeners.forEach(MagnifierUpdateListener::magnifierUpdated);
    }

    /////////////////////////////////////////////////////////////////
    // event handling
    /////////////////////////////////////////////////////////////////

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPopup(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updatePos(e.getXOnScreen(), e.getYOnScreen());

        refresh();
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        refresh();
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            double rotation = e.getWheelRotation();
            boolean shrink = (rotation < 0);
            double resizeBy;

            if (shrink)
                resizeBy = (1 / (-rotation / 10));
            else
                resizeBy = rotation / 10;

            if (resizeBy == 0)
                return;

            int width = getWidth();
            int height = getHeight();

            if ((width <= 20 || height <= 20) && resizeBy < 1)
                return;

            width *= resizeBy;
            height *= resizeBy;

            setSize(width, height);
        }
        else {
            double changeZoomBy = e.getWheelRotation();

            if (changeZoomBy == 0)
                return;

            magnificationFactor *= 1 + changeZoomBy / 10;

            if (magnificationFactor == 0)
                magnificationFactor *= 1 + changeZoomBy / 10;
        }

        refresh();
    }

    ////////////////////////////////////////////////////////////////////
    // helper methods
    ////////////////////////////////////////////////////////////////////

    private void updatePos(int e_x, int e_y) {
        int widht = getWidth();
        int height = getHeight();

        int x = e_x - widht / 2;
        int y = e_y - height / 2;

        setBounds(x, y, widht, height);
    }

    public void refresh() {
        Point mouse = MouseInfo.getPointerInfo().getLocation().getLocation();

        updatePos(mouse.x, mouse.y);
        updateMagnifiedImg();
        repaint();

        fireMagnifierUpdated();
    }

    private void doPopup(MouseEvent e) {
        popupMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());
        popupMenu.setVisible(true);
    }
}