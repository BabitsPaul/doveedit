package dove.frame.menubar;

import dove.document.DocumentContext;

import java.util.ArrayList;

public class MenubarTodo {
    private ArrayList<MenubarComponent> todo;

    private DocumentContext doc;

    public MenubarTodo(DocumentContext doc) {
        if (doc.menuTodo != null)
            throw new IllegalStateException("already instancised for this runtime");

        doc.menuTodo = this;

        this.doc = doc;

        todo = new ArrayList<>();
    }

    public void register(MenubarComponent comp) {
        todo.add(comp);
    }

    public void apply() {
        //todo.forEach(t -> doc.menu.addAll(t.getStructure()));
    }
}
