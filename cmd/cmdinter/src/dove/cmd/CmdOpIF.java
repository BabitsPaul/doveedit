package dove.cmd;

import dove.cmd.interpreter.CommandLineLayerModel;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
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
    private CommandLineLayerModel model;

    public void textColor(Color color) {

    }

    public void backgroundColor(Color color) {

    }

    public void switchModel(CommandLineLayerModel nmodel) {

    }

    public InputStream getInputStream() {
        return null;
    }

    public PrintStream getOutputStream() {
        return null;
    }

    public PrintStream getErrorStream() {
        return null;
    }

    private class CmdPrintStream
            extends PrintStream {
        public CmdPrintStream() {
            super(new CmdOutputStream());
        }


    }

    private class CmdOutputStream
            extends java.io.OutputStream {
        @Override
        public void write(int b) throws IOException {

        }
    }
}