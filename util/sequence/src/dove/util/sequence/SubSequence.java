package dove.util.sequence;

import dove.util.collections.SortedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public List<SubSequence<T>> listChildren() {
        return Collections.unmodifiableList(subseq);
    }

    public void insert(T[] t, int offset, int length) {
        if (offset < this.offset || offset + length > this.offset + this.length)
            throw new IllegalArgumentException("offset or length not in range");

        subseq.add(new SubSequence<>(t, offset, length));
    }

    public void remove(T[] t, int offset, int length) {
        int i = 0;
        while (i < subseq.size() && subseq.get(i).getOffset() < offset)
            ++i;

        if (subseq.get(i).offset != 0 || (t != null && Arrays.equals(t, subseq.get(i).getT())))
            throw new IllegalStateException("No subsequence is marked with offset=" + offset + " and content=" + t);
        else
            subseq.remove(i);
    }

    public boolean isLeaf() {
        return subseq.isEmpty();
    }
}