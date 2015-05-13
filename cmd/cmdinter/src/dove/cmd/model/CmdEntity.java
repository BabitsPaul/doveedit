package dove.cmd.model;

/**
 * represents an entity
 * <p>
 * Entitys can be: Methods, Structures or Variables
 * <p>
 * The Entity only holds the code representing the
 * structure/method. And can be used to execute calls
 * referencing the specified entityw
 */
public abstract class CmdEntity {
    private int storedAt;

    public CmdEntity(Memory memory) {
        storedAt = memory.store(this);
    }

    public int getMemPos() {
        return storedAt;
    }

    public abstract DataType getType();
}
