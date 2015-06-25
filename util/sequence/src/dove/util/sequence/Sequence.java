package dove.util.sequence;

public class Sequence {
    private SubSequence total;

    public Sequence(String string) {
        total = SequenceUtil.createSequence(string);
    }

    public void mark(int offset, int length) {

    }

    public void removeMark(int offset, int length) {

    }
}