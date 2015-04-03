package dove.cmd.interpreter;

import dove.cmd.ui.model.AbstractTextLayerModel;
import dove.cmd.ui.model.CharBuffer;
import dove.cmd.ui.model.ClipObject;
import dove.cmd.ui.model.Cursor;
import dove.util.collections.FixedSizeRAStack;

public class CommandLineLayerModel
        extends AbstractTextLayerModel {
    private int currentLineStartY;

    private FixedSizeRAStack<String> prevCmds;

    public CommandLineLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip) {
        super(buffer, cursor, clip);

        currentLineStartY = cursor.getY();

        prevCmds = new FixedSizeRAStack<>(50, String.class);
    }

    @Override
    public void removeChar() {

    }

    @Override
    public void addChar(char c) {

    }

    @Override
    public void cursorUp() {

    }

    @Override
    public void cursorDown() {

    }

    @Override
    public void cursorRight() {

    }

    @Override
    public void cursorLeft() {

    }

    @Override
    public void nextLine() {

    }

    public void write(String txt) {

    }

    public void writeLine(String txt) {

    }
}