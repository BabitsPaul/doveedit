package dove.cmd.ui.model;

import dove.cmd.ui.paint.AbstractLayerRenderer;
import dove.cmd.ui.paint.CharLayerRenderer;

/**
 * provides an interface for ascii-art / consolegames
 * if this charlayer is active, the buffer will be cliped to
 * restrict access to the part of the buffer, which is visible,
 * keyevents will be redirected to a listener
 * <p>
 * this layer cannot move the viewport
 */
public class CharLayer
        extends AbstractCommandLayer {
    /**
     * the model of this layer
     */
    private AbstractCharLayerModel model;

    public CharLayer(AbstractCharLayerModel model, CharBuffer buffer, Cursor cursor) {
        super(model, cursor, buffer);

        redirectTo(model);
    }

    //////////////////////////////////////////////////////////
    // model
    //////////////////////////////////////////////////////////

    public AbstractCharLayerModel getModel() {
        return model;
    }

    ///////////////////////////////////////////////////////////
    // painting
    ///////////////////////////////////////////////////////////

    @Override
    public AbstractLayerRenderer createRenderer() {
        return new CharLayerRenderer(this);
    }
}