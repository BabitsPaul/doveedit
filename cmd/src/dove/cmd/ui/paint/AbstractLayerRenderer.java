package dove.cmd.ui.paint;

import dove.cmd.ui.model.AbstractCommandLayer;

import java.awt.*;

/**
 * the baseclass for rendering abstractcommandlayers
 * <p>
 * this class aswell specifies the space required
 * to render the specified layer
 * <p>
 * the text will always be rendered monospaced
 */
public abstract class AbstractLayerRenderer {
    /**
     * the layer rendered by this renderer
     */
    private AbstractCommandLayer layer;

    /**
     * the metrics for rendering text
     */
    private LayerRendererMetrics metrics;

    /**
     * creates a new layerrenderer for the
     * specified layer
     *
     * @param layer the layer to render
     */
    public AbstractLayerRenderer(AbstractCommandLayer layer, LayerRendererMetrics metrics) {
        this.layer = layer;
        this.metrics = metrics;
    }

    /**
     * getter for the layer this
     * renderer is related to
     *
     * @return the layer this renderer renders
     */
    protected AbstractCommandLayer getLayer() {
        return layer;
    }

    /**
     * the metrics used by this renderer
     * to paint the specified layer
     *
     * @return the metrics this renderer uses
     */
    protected LayerRendererMetrics getMetrics() {
        return metrics;
    }

    /**
     * paints the layer this renderer is
     * supposed to paint with the specified graphics
     *
     * @param g the graphics to paint with
     */
    public abstract void renderLayer(Graphics g);

    /**
     * the area required to paint the
     * given layer
     *
     * @return the area required by this renderer
     */
    public abstract Dimension getSize();
}