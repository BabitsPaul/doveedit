package dove.cmd.model;

import dove.cmd.model.datatypes.Data;

/**
 * represents an entity
 * <p>
 * Entitys can be: Methods, Structures or Variables
 * <p>
 * The Entity only holds the code representing the
 * structure/method. And can be used to execute calls
 * referencing the specified entityw
 */
public abstract class SyntaxEntity
        extends Data {
    public abstract String getName();

    public abstract DataType getType();
}
