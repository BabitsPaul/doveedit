package dove.component.img;

import dove.api.ComponentApi;
import dove.document.DocumentContext;
import dove.event.EventRep;
import dove.frame.m2menu.M2Menu;
import dove.undo.Undo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image
        extends ComponentApi {
    private BufferedImage bi;

    private DocumentContext doc;

    private String imgName;

    public Image(String src, DocumentContext doc) {
        try {
            bi = ImageIO.read(new File(src));

            size = new Rectangle(bi.getWidth(), bi.getHeight());

            imgName = new File(src).getName();
        }
        catch (IOException e) {
            //TODO
        }

        this.doc = doc;
    }

    @Override
    public void moveComponentTo(int dx, int dy) {
        new Undo(doc) {
            private Point prevPt = new Point(size.x, size.y);
            private Point newPt = new Point(dx, dy);

            @Override
            public void redo() {
                moveComponentTo(newPt.x, newPt.y);
            }

            @Override
            public void undo() {
                moveComponentTo(prevPt.x, prevPt.y);
            }

            @Override
            public String getDiscription() {
                return "move " + imgName + "to x: " + prevPt.x + " y: " + prevPt.y;
            }
        };

        size.setLocation(dx, dy);
    }

    @Override
    public void processEvent(EventRep e) {
        switch ((String) e.get("type")) {
            case "java.awt.event.KeyEvent":
                if ((int) e.get("id") != KeyEvent.KEY_PRESSED)
                    return;

                switch ((int) e.get("keycode")) {
                    case KeyEvent.VK_UP:
                        moveComponentTo(size.x, size.y - 5);
                        break;

                    case KeyEvent.VK_DOWN:
                        moveComponentTo(size.x, size.y + 5);
                        break;

                    case KeyEvent.VK_LEFT:
                        moveComponentTo(size.x - 5, size.y);
                        break;

                    case KeyEvent.VK_RIGHT:
                        moveComponentTo(size.x + 5, size.y);
                        break;
                }
                break;
        }

        doc.frame.documentChanged();
    }

    @Override
    public void resizeComponent(Dimension nSize) {

    }

    @Override
    public M2Menu getM2Menu() {
        return null;
    }

    @Override
    public void optionSelected(String[] optionPath) {

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bi, size.x, size.y, null);
    }
}