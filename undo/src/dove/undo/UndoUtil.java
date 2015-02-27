package dove.undo;

import dove.document.DocumentContext;

import java.util.Stack;

public class UndoUtil {
    private Stack<Undo> undoStack;
    private Stack<Undo> redoStack;

    public UndoUtil(DocumentContext doc) {
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        doc.undo = this;
    }

    void actionDone(Undo undo) {
        undoStack.push(undo);

        //TODO store redo-tasks for later
        redoStack.clear();
    }

    public void undoNext() {
        Undo undo = undoStack.pop();

        undo.undo();

        redoStack.push(undo);
    }

    public void redoNext() {
        Undo undo = redoStack.pop();

        undo.redo();

        undoStack.push(undo);
    }
}
