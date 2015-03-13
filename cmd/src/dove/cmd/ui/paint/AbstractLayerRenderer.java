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
 *
 * NOTE: this renderer is NOT supposed to/will NOT
 * check the validness of fontmetrics or paint the
 * background
 * It will paint the text and cursor with the info
 * provided by the buffer and cursor
 */
public abstract class AbstractLayerRenderer {
    /**
     * the layer rendered by this renderer
     */
    private AbstractCommandLayer layer;

    /**
     * creates a new layerrenderer for the
     * specified layer
     *
     * @param layer the layer to render
     */
    public AbstractLayerRenderer(AbstractCommandLayer layer) {
        this.layer = layer;
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
     * paints the layer this renderer is
     * supposed to paint with the specified graphics
     *
     * @param g the graphics to paint with
     * @param metrics
     */
    public abstract void renderLayer(Graphics g, LayerRendererMetrics metrics);

    /**
     * the area required to paint the
     * given layer
     *
     * @return the area required by this renderer
     * @param metrics
     */
    public abstract Dimension getSize(LayerRendererMetrics metrics);
}