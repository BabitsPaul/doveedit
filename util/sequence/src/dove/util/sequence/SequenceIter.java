package dove.util.sequence;

public class SequenceIter<T> {
    public static final int MAX_DEPTH = -1;

    private Sequence<T> seq;

    private SubSequence<T> current;

    private int at = 0;

    public SequenceIter(Sequence<T> seq) {
        this.seq = seq;

        current = seq;
    }

    public boolean hasNext() {
        return at < seq.getLength();
    }

    public T next() {
        return null;
    }

    public SubSequence<T> getSmallestMark() {
        return null;
    }

    public SubSequence<T> getNextSubSeq() {
        return null;
    }

    public SubSequence<T> getTotalSeq() {
        return seq;
    }
}
