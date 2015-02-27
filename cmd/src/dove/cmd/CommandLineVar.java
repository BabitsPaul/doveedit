package dove.cmd;

import dove.util.concurrent.access.ThreadSafeVar;

public class CommandLineVar
        extends ThreadSafeVar {
    private boolean isUpdateable = false;

    private boolean typesafe = true;

    private Class clazz;

    public CommandLineVar() {
        super.val = null;
        clazz = Object.class;
        typesafe = false;
        isUpdateable = true;
    }

    public CommandLineVar(Object val) {
        this.val = val;

        if (val == null) {
            clazz = Object.class;
            typesafe = false;
            isUpdateable = true;
        }
        else {
            clazz = val.getClass();
            typesafe = true;
            isUpdateable = true;
        }
    }

    public void makeTypesafe(boolean typesafe, Class... type) {
        this.typesafe = typesafe;

        if (typesafe)
            this.clazz = type[0];
    }

    public void setVal(Object val) {
        if (!isUpdateable)
            throw new IllegalStateException("final vars can't be updated");

        if (typesafe && !clazz.isInstance(val))
            throw new IllegalArgumentException("Invalid type - must be instance of " + clazz.getName());

        this.val = val;
    }

    public void makeFinal() {
        isUpdateable = false;
    }

    public void makeEditable() {
        isUpdateable = true;
    }
}