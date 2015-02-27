package dove;

import dove.document.DocumentContext;
import dove.util.misc.MapHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;

public class Resources {
    private WindowHelper windowHelper;

    /**
     * this map holds all currently opened outputstream
     * together with their openend files (path)
     */
    private HashMap<String, OutputStream> outMap;

    /**
     * this map holds all currently opened inputstreams
     * together with their opened files (by path)
     */
    private HashMap<String, InputStream> inMap;

    /**
     * lists all currently opened frames
     * together with their name
     */
    private HashMap<String, Window> frameMap;

    /**
     * creates a new resourcehandler
     * for this documentcontext if not
     * yet existent
     * <p>
     * if a resourcehanler already exists,
     * an illegalstateexception will be thrown
     *
     * @param context the documentcontext, for which a resourcehandle will be created
     * @throws IllegalStateException if a resourcehandle already exists
     */
    public Resources(DocumentContext context) {
        if (context.resources != null) {
            throw new IllegalStateException(
                    "An instance of Resourcehandle already exists for this Runtime");
        }

        context.resources = this;

        outMap = new HashMap<>();
        inMap = new HashMap<>();
        frameMap = new HashMap<>();

        windowHelper = new WindowHelper();
    }

    public JFrame requestFrame(String id) {
        JFrame result = new JFrame();

        result.addWindowListener(windowHelper);

        frameMap.put(id, result);

        return result;
    }

    /**
     * opens the streams correlated to this file
     * and openmode
     *
     * @param file the file to be opened
     * @param mode the mode in which this file is opened
     * @throws java.io.IOException if any exceptions occure during opening the streams
     */
    public void open(String file, OpenMode mode)
            throws IOException {
        InputStream in = null;
        OutputStream out = null;

        switch (mode) {
            case IN_ONLY:
                in = new FileInputStream(file);
                break;
            case BI_DIR:
                in = new FileInputStream(file);
            case OUT_ONLY:
                out = new FileOutputStream(file);
                break;
        }

        inMap.put(file, in);
        outMap.put(file, out);
    }

    /**
     * if an inputstream that is correlated
     * to the specified file is opened it
     * will be returned (else null will be returned)
     *
     * @param file the file for which an inputstream is searched
     * @return the inputstream correlated to
     * the specified file
     */
    public InputStream getInputStream(String file) {
        return inMap.get(file);
    }

    /**
     * if an outputstream that is correlated
     * to this file opened, it will be returned
     *
     * @param file the file for which an outputstream is searched
     * @return the outputstream correlated to this file
     */
    public OutputStream getOutputStream(String file) {
        return outMap.get(file);
    }

    /**
     * cloases all open streams and
     * frames to end the program
     *
     * @param cause the cause why this action
     *              is performed (shutdown, severe error, etc.)
     */
    public void closeAll(String cause) {
        inMap.values().forEach(i -> {
            if (i == null)
                return;

            try {
                i.close();
            }
            catch (IOException e) {
            }
        });

        outMap.values().forEach(o -> {
            if (o == null)
                return;

            try {
                o.flush();
                o.close();
            }
            catch (IOException e) {
            }
        });

        SwingUtilities.invokeLater(() ->
                frameMap.values().forEach(f -> {
                    f.setVisible(false);
                    f.dispose();
                }));
    }

    /**
     * OpenMode defines the way in which a
     * file is opened
     */
    public enum OpenMode {
        /**
         * only an inputstream is opened
         */
        IN_ONLY,
        /**
         * only an outputstream is opened
         */
        OUT_ONLY,
        /**
         * both in- and outputstream are opened
         */
        BI_DIR
    }

    private class WindowHelper
            extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            frameMap.remove(MapHelper.getFirstFor(frameMap, (Window) e.getSource()));
        }
    }
}