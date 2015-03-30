package dove.cmd.ui.model;

import java.awt.event.KeyListener;

public abstract class AbstractCharLayerModel
        extends AbstractLayerModel
        implements KeyListener {
        private CharBuffer buffer;

        private Cursor cursor;
}
