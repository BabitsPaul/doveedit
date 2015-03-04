package dove.cmd.ui;

import java.util.EventObject;

public class CommandLineEvent
        extends EventObject {
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
        TEXT_LAYER_TYPE
    }
}
