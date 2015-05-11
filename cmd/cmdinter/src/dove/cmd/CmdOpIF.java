package dove.cmd;

import dove.cmd.interpreter.CommandLineLayerModel;
import dove.cmd.ui.CommandLineUI;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * CommandLine Operations Interface
 * <p>
 * The commandline holds an instance of this interface
 * that is handed to all commandline calls
 * <p>
 * this interface provides methods for writing/reading from/to
 * the commandline, switching colors and mode
 */
public class CmdOpIF {
    private static final Color DEFAULT_TEXT = Color.black;
    private static final Color DEFAULT_ERROR = Color.red;
    private CommandLineLayerModel model;
    private CommandLineUI ui;
    private Color color;

    public void textColor(Color color) {
        ui.setDefaultForeground(color);
    }

    public void backgroundColor(Color color) {
        ui.setBackground(color);
    }

    public void switchModel(CommandLineLayerModel nmodel) {
        ui.setModel(nmodel);
    }

    public InputStream getInputStream() {
        return new CmdInputStream();
    }

    public PrintStream getOutputStream() {
        return new CmdPrintStream(new CmdOutputStream());
    }

    public PrintStream getErrorStream() {
        return new CmdPrintStream(new CmdErrorStream());
    }

    private class CmdPrintStream
            extends PrintStream {
        public CmdPrintStream(OutputStream cmdos) {
            super(cmdos);
        }
    }

    private class CmdOutputStream
            extends java.io.OutputStream {
        @Override
        public void write(int b) throws IOException {
            write((char) b);
        }
    }

    private class CmdErrorStream
            extends java.io.OutputStream {
        @Override
        public void write(int b) throws IOException {
            textColor(DEFAULT_ERROR);
            write((char) b);
            textColor(DEFAULT_TEXT);
        }
    }

    private class CmdInputStream
            extends InputStream {
        @Override
        public int read() throws IOException {
            return ui.getBuffer().get(ui.getCmdCursor().getPosition());
        }
    }
}