package dove;

import dove.config.ConfigLoader;
import dove.document.DocumentContext;
import dove.document.IDGiver;
import dove.error.ErrorHandler;
import dove.event.EventListener;
import dove.frame.Frame;
import dove.frame.menubar.MenubarTodo;
import dove.undo.UndoUtil;

public class Setup {
    public static DocumentContext setup() {
        DocumentContext context = new DocumentContext();

        context.error = new ErrorHandler(context);
        context.resources = new Resources(context);

        context.config = new ConfigLoader();
        context.config.load(context);

        context.idGiver = new IDGiver(context);
        context.menuTodo = new MenubarTodo(context);
        context.undo = new UndoUtil(context);

        context.frame = new Frame(context);
        context.event = new EventListener(context);

        context.menuTodo.apply();

        return context;
    }

    public static void tearDown(DocumentContext context) {
        context.config.save(context);

        context.resources.closeAll("shutdown");
    }
}