package dove.cmd.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This structural entity can be used to directly interfer with
 * javaclasses
 * <p>
 * by the means of
 */
public class JavaEntity
        extends StructureEntity {
    public JavaEntity(String className)
            throws ClassNotFoundException {
        Class clazz = Class.forName(className);

        for (Method m : clazz.getDeclaredMethods()) {

        }

        for (Field f : clazz.getDeclaredFields()) {

        }

        for (Constructor c : clazz.getDeclaredConstructors()) {

        }
    }
}
