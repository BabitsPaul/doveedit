package dove.cmd.ui.paint;

import dove.cmd.ui.model.CharLayer;

import java.awt.*;

public class CharLayerRenderer
        extends AbstractLayerRenderer {
    public CharLayerRenderer(CharLayer layer, LayerRendererMetrics metrics) {
        super(layer, metrics);
    }

    @Override
    protected CharLayer getLayer() {
        return (CharLayer) super.getLayer();
    }

    @Override
    public void renderLayer(Graphics g) {

    }

    @Override
    public Dimension getSize() {
        int layerWidth = getLayer().getWidth();
        int layerHeight = getLayer().getHeight();

        int width = layerWidth * getMetrics().signWidth + getMetrics().textSpaceLeft + getMetrics().textSpaceRight;
        int height = layerHeight * getMetrics().lineHeight + (layerHeight - 1) * getMetrics().lineSpace +
                getMetrics().textSpaceBottom + getMetrics().textSpaceTop;

        return new Dimension(width, height);
    }
}
