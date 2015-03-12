package dove.cmd.ui.paint;

import dove.cmd.ui.model.TextLayer;

import java.awt.*;

public class TextLayerRenderer
        extends AbstractLayerRenderer {
    public TextLayerRenderer(TextLayer layer, LayerRendererMetrics metrics) {
        super(layer, metrics);
    }

    @Override
    public TextLayer getLayer() {
        return (TextLayer) super.getLayer();
    }

    @Override
    public void renderLayer(Graphics g) {

    }

    @Override
    public Dimension getSize() {
        int bufferHeight = getLayer().getBuffer().getHeight();
        int bufferWidth = getLayer().getBuffer().getWidth();

        int width = getMetrics().signWidth * bufferWidth + getMetrics().textSpaceLeft +
                getMetrics().textSpaceRight;
        int height = getMetrics().lineHeight * bufferHeight + getMetrics().textSpaceBottom +
                getMetrics().textSpaceTop + getMetrics().lineSpace * (bufferHeight - 1);

        return new Dimension(width, height);
    }
}
