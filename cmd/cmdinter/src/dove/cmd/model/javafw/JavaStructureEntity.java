package dove.cmd.model.javafw;

import dove.cmd.model.FieldEntity;
import dove.cmd.model.MethodEntity;
import dove.cmd.model.StructureEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavaStructureEntity
        extends StructureEntity {
    private MethodEntity[] methods;

    private FieldEntity[] fields;

    public JavaStructureEntity(String className)
            throws ClassNotFoundException {
        Class clazz = Class.forName(className);

        methods = new MethodEntity[clazz.getDeclaredMethods().length];
        int i = 0;
        for (Method m : clazz.getDeclaredMethods()) {
            methods[i] = new MethodEntity(null, null, null);
        }

        for (Field f : clazz.getDeclaredFields()) {

        }

        for (Constructor c : clazz.getDeclaredConstructors()) {

        }
    }
}
