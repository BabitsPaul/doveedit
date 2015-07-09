package dove.util.sequence;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sequence<T>
        extends SubSequence<T> {
    public Sequence(T[] t, int length) {
        super(t, 0, length);
    }

    public void mark(T[] t, int offset, int length) {
        //TODO transfer subsequence to lower sequence
        findLowestContainer(offset, length).insert(t, offset, length);
    }

    public void removeAt(int offset, int length) {
        findLowestContainer(offset, length).remove(null, offset, length);
    }

    public SequenceIter<T> iterator() {
        return new SequenceIter<>(this);
    }

    private SubSequence<T> findLowestContainer(int offset, int length) {
        SubSequence<T> seq = this;
        Predicate<SubSequence<T>> containsRange =
                (cseq -> cseq.getOffset() <= offset && cseq.getOffset() + cseq.getLength() >= offset + length);


        while (!seq.isLeaf()) {
            Optional<SubSequence<T>> sub = seq.listChildren().stream().
                    filter(containsRange).findAny();

            if (sub.isPresent())
                seq = sub.get();
            else
                return seq;
        }

        return seq;
    }

    private List<SubSequence<T>> listSequencesInside(int offset, int length) {
        return findLowestContainer(offset, length).listChildren().stream().
                filter(seq -> seq.getOffset() >= offset && seq.getOffset() + seq.getLength() <= offset + length).
                collect(Collectors.toList());
    }
}