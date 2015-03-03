package dove.error;

import dove.document.DocumentContext;
import dove.util.ui.dialogs.Dialogs;

import java.awt.*;

public class ErrorHandler {
    private DocumentContext doc;

    public ErrorHandler(DocumentContext context) {
        this.doc = context;
    }

    public void handleException(String additional, Exception e) {
        Component c = null;
        if (doc != null && doc.frame != null)
            c = doc.frame.getComponent();

        Dialogs.showDialog(additional, e.getMessage(), Dialogs.ERROR_MESSAGE,
                new String[]{"OK"}, new String[]{}, c, 0);
    }
}