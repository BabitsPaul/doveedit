package dove.frame.menubar;

import dove.document.DocumentContext;
import dove.util.treelib.StringMap;

import javax.swing.*;

public abstract class MenubarComponent {
    public MenubarComponent(DocumentContext doc) {
        doc.menuTodo.register(this);
    }

    protected abstract StringMap<JMenuItem> getStructure();
}