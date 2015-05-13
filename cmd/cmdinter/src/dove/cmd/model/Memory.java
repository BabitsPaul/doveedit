package dove.cmd.model;

public class Memory {
    private static final int DEFAULT_MEM_SIZE = 5242880;
    private byte[] bytes;

    public Memory() {
        bytes = new byte[DEFAULT_MEM_SIZE];
    }

    public Memory(int size) {
        bytes = new byte[size];
    }

    public int store(CmdEntity entity) {


        return 0;
    }

    public CmdEntity get(int entity) {
        return null;
    }

    public boolean remove(CmdEntity entity) {
        return false;
    }
}
