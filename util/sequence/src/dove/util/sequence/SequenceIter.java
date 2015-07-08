package dove.util.sequence;

public class SequenceIter<T> {
    private Sequence<T> seq;

    public SequenceIter(Sequence<T> seq) {
        this.seq = seq;
    }

    public boolean hasNext() {
        return false;
    }

    public T next() {
        return null;
    }

    public SubSequence<T> getSmallestMark() {
        return null;
    }

    public SubSequence<T> getTotalSeq() {
        return seq;
    }
}
