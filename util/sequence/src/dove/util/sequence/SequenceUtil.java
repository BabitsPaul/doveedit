package dove.util.sequence;

import java.util.ArrayList;
import java.util.function.Function;

public class SequenceUtil {
    public static <U, T> Sequence<U> transform(Function<T[], U[]> f, Sequence<T> seq) {
        Sequence<U> root = new Sequence<>(f.apply(seq.getT()), seq.getLength());

        ArrayList<SubSequence<T>> todo = new ArrayList<>();
        todo.addAll(seq.listChildren());

        while (!todo.isEmpty()) {
            SubSequence<T> nseq = todo.remove(0);

            root.insert(f.apply(nseq.getT()), nseq.getOffset(), nseq.getLength());

            todo.addAll(nseq.listChildren());
        }

        return root;
    }
}
