package dove.cmd.ui.paint;

import dove.cmd.ui.model.CharLayer;

import java.awt.*;

public class CharLayerRenderer
        extends AbstractLayerRenderer {
    public CharLayerRenderer(CharLayer layer) {
        super(layer);
    }

    @Override
    protected CharLayer getLayer() {
        return (CharLayer) super.getLayer();
    }

    @Override
    public void renderLayer(Graphics g, LayerRendererMetrics metrics) {

    }

    @Override
    public Dimension getSize(LayerRendererMetrics metrics) {
        int layerWidth = getLayer().getWidth();
        int layerHeight = getLayer().getHeight();

        int width = layerWidth * metrics.signWidth + metrics.textSpaceLeft + metrics.textSpaceRight;
        int height = layerHeight * metrics.lineHeight + (layerHeight - 1) * metrics.lineSpace +
                metrics.textSpaceBottom + metrics.textSpaceTop;

        return new Dimension(width, height);
    }
}
