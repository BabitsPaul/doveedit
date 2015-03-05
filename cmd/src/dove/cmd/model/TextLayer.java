package dove.cmd.model;

import java.awt.event.KeyListener;

public class TextLayer
        extends AbstractCommandLayer
        implements KeyListener {
    public TextLayer() {
        super(null);
        //set the keyredirect to redirect event to this instance
        redirectTo(this);
    }

    @Override
    public void enableLayer() {

    }


}