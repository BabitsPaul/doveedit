package dove.cmd.ui.paint;

import dove.cmd.ui.model.AbstractCommandLayer;
import dove.cmd.ui.model.TextLayer;

import java.awt.*;

public class TextLayerRenderer
        extends AbstractLayerRenderer {
    public TextLayerRenderer(TextLayer layer) {
        super(layer);
    }

    @Override
    public TextLayer getLayer() {
        return (TextLayer) super.getLayer();
    }

    @Override
    public void renderLayer(Graphics g, LayerRendererMetrics metrics) {
        int descent = metrics.lineHeight - metrics.signMaxAscent;

        //read buffers
        char[][] buffer = getLayer().getBuffer().getContent();
        Color[][] colors = getLayer().getBuffer().getColors();

        //position of the next char on the screen
        int x = metrics.textSpaceLeft;
        int y = metrics.textSpaceTop + metrics.lineHeight;

        //paint the chars of each line
        for (int line = 0; line < buffer.length; line++) {
            for (int col = 0; col < buffer[line].length; col++) {
                if (buffer[line][col] == AbstractCommandLayer.NO_CHAR)
                    continue;

                g.setColor(colors[line][col]);
                g.drawChars(new char[]{buffer[line][col]}, 0, 1, x, y);

                x += metrics.signWidth + metrics.signSpace;
            }

            x = metrics.textSpaceLeft;

            y += metrics.lineSpace;
            y += metrics.lineHeight;
        }

        //paint the cursor if visible
        if (metrics.showCursor) {
            int cursorX = getLayer().getCursor().getX();
            int cursorY = getLayer().getCursor().getY();
            Color cursorBackground = colors[cursorY][cursorX];
            Color cursorForeground = metrics.background;
            char[] cursorContent = new char[]{buffer[cursorY][cursorX]};

            g.setColor(cursorBackground);
            g.fillRect(cursorX * metrics.signWidth + metrics.textSpaceLeft,
                    cursorY * metrics.lineHeight + Math.max(0, cursorY - 1) * metrics.lineSpace + metrics.textSpaceTop + descent,
                    metrics.signWidth, metrics.lineHeight);

            g.setColor(cursorForeground);
            g.drawChars(cursorContent, 0, 1,
                    cursorX * metrics.signWidth + metrics.textSpaceLeft,
                    metrics.textSpaceTop + (cursorY + 1) * metrics.lineHeight + Math.max(0, cursorY - 1) * metrics.lineSpace);
        }
    }

    @Override
    public Dimension getSize(LayerRendererMetrics metrics) {
        int bufferHeight = getLayer().getBuffer().getHeight();
        int bufferWidth = getLayer().getBuffer().getWidth();

        int width = metrics.signWidth * bufferWidth + metrics.textSpaceLeft +
                metrics.textSpaceRight;
        int height = metrics.lineHeight * bufferHeight + metrics.textSpaceBottom +
                metrics.textSpaceTop + metrics.lineSpace * (bufferHeight - 1);

        return new Dimension(width, height);
    }
}
