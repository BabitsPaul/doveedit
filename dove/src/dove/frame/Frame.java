package dove.frame;

import dove.api.ComponentApi;
import dove.api.FrameApi;
import dove.config.Configuration;
import dove.document.DocumentContext;
import dove.event.EventListener;
import dove.event.EventRep;

import javax.swing.*;
import java.awt.*;

public class Frame
        implements FrameApi {
    private MainPane contentPane;

    private Configuration config;

    private JFrame frame;

    public Frame(DocumentContext context) {
        context.frame = this;

        config = context.config.getConfiguration("dove/frame");

        contentPane = new MainPane(context);

        this.frame = context.resources.requestFrame("mainframe");

        frame.setTitle("Dove");

        frame.setContentPane(contentPane);

        frame.setTitle("DoveEdit");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        //new FrameMenu(context);

        frame.setJMenuBar(new JMenuBar());
    }

    @Override
    public void add(ComponentApi component) {
        contentPane.add(component);

        frame.repaint();
    }

    @Override
    public void remove(ComponentApi component) {
        contentPane.remove(component);

        frame.repaint();
    }

    @Override
    public void process(EventRep e) {
        contentPane.process(e);
    }

    @Override
    public void setupListener(EventListener listener) {
        frame.addComponentListener(listener);
        frame.addKeyListener(listener);
        frame.addMouseListener(listener);
        frame.addMouseMotionListener(listener);
        frame.addMouseWheelListener(listener);
        frame.addWindowListener(listener);

        frame.setFocusable(true);
        frame.requestFocus();
    }

    @Override
    public void documentChanged() {
        frame.repaint();
    }

    @Override
    public Dimension getSize() {
        return contentPane.getSize();
    }

    @Override
    public Component getComponent() {
        return frame;
    }

    @Override
    public JMenuBar getJMenubar() {
        return frame.getJMenuBar();
    }

    @Override
    public void update(Runnable toUpdate) {
        SwingUtilities.invokeLater(() -> {
            frame.invalidate();

            toUpdate.run();

            frame.validate();
            frame.repaint();
        });
    }
}
