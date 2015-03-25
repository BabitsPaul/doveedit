package dove.cmd.interpreter;

import dove.cmd.ui.model.AbstractTextLayerModel;
import dove.cmd.ui.model.CharBuffer;
import dove.cmd.ui.model.ClipObject;
import dove.cmd.ui.model.Cursor;

import java.util.List;

public class CommandLineLayerModel
        extends AbstractTextLayerModel {
    public CommandLineLayerModel(CharBuffer buffer, Cursor cursor, ClipObject clip) {
        super(buffer, cursor, clip);
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

    @Override
    public String getLastLine() {
        return null;
    }

    @Override
    public List<String> listLines() {
        return null;
    }

    @Override
    public void write(String text) {

    }

    @Override
    public void writeln(String text) {

    }
}
