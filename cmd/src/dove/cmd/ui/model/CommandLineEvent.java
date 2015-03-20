package dove.cmd.ui.model;

import java.util.EventObject;

public class CommandLineEvent
        extends EventObject {
    public static final int SUPPRESS_EVENT_RELATED_REPAINT = 0;
    public static final int ENABLE_EVENT_RELATED_REPAINT   = 1;

    public static final Object PAINTING_DUMMY = new Object();

    private SOURCE_TYPE source_type;
    private int         modifier;

    public CommandLineEvent(Object source, SOURCE_TYPE source_type, int modifier) {
        super(source);

        this.source_type = source_type;

        this.modifier = modifier;
    }

    public SOURCE_TYPE getSourceType() {
        return source_type;
    }

    public int getModifier() {
        return modifier;
    }

    public static enum SOURCE_TYPE {
        BUFFER_TYPE,
        CURSOR_TYPE,
        CHAR_LAYER_TYPE,
        TEXT_LAYER_TYPE,
        CLIPPING,
        PAINTING
    }
}
