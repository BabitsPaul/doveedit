package dove.clipboard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class InternalClipboardHelper {
    //TODO:other dataflavors
    //TODO: provide dataflavorinfo

    public static String getClipboardContent() {
        String result = "";

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable t = clipboard.getContents(null);

        boolean available = ((t != null) && (t.isDataFlavorSupported(DataFlavor.stringFlavor)));

        if (available) {
            try {
                result = (String) t.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException | IOException e) {
                result = null;
            }
        }

        return result;
    }

    public static void setClipboardContent(String nContent) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection text = new StringSelection(nContent);

        clipboard.setContents(text, text);
    }

}
