package dove.cmd.ui.model;

import java.awt.event.KeyListener;

public abstract class AbstractCharLayerModel
        extends AbstractLayerModel
        implements KeyListener {
        public abstract int getWidth();

        public abstract int getHeight();
}
