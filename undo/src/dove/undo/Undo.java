package dove.undo;

import dove.document.DocumentContext;

public abstract class Undo {
    public Undo(DocumentContext context) {
        context.undo.actionDone(this);
    }

    public abstract void redo();

    public abstract void undo();

    public abstract String getDiscription();
}
