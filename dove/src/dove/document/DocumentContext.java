package dove.document;

import dove.Resources;
import dove.api.FrameApi;
import dove.config.ConfigLoader;
import dove.error.ErrorHandler;
import dove.event.EventListener;
import dove.frame.menubar.MenubarTodo;
import dove.undo.UndoUtil;

public class DocumentContext {
    public IDGiver       idGiver;
    public FrameApi      frame;
    public EventListener event;
    public ConfigLoader  config;
    public UndoUtil      undo;
    public ErrorHandler  error;
    public Resources     resources;
    public MenubarTodo   menuTodo;
}