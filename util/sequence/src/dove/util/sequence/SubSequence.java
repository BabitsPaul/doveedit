package dove.util.sequence;

import dove.util.collections.SortedList;

import java.util.ArrayList;

public class SubSequence<T> {
    private ArrayList<SubSequence<T>> subseq;

    private T[] t;

    private int offset;

    private int length;

    public SubSequence(T[] t, int offset, int length) {
        this.t = t;

        subseq = new SortedList<>((a, b) -> Integer.compare(a.getOffset(), b.getOffset()));
    }

    public T[] getT() {
        return t;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public SubSequence<T> get(int childNum) {
        return subseq.get(childNum);
    }

    public void insert(T[] t, int offset, int length) {
        if (offset < this.offset || offset + length > this.offset + this.length)
            throw new IllegalArgumentException("offset or length not in range");

        subseq.add(new SubSequence<>(t, offset, length));
    }

    public void remove(T[] t, int offset, int length) {

    }

    public boolean isLeaf() {
        return subseq.isEmpty();
    }
}