package dove.util.sequence;

import java.util.ArrayList;
import java.util.List;

public class SequenceUtil {
    public static SubSequence createSequence(String txt) {
        return new SubSequence(txt);
    }

    /**
     * lists all subsequences that are marked in the specified
     * sequence. This list also includes the sequence itself
     *
     * @param seq lists all elements of this sequence
     * @return a list of subsequences
     */
    public static List<SequenceElement> listElements(SubSequence seq) {
        List<SequenceElement> result = new ArrayList<>();

        List<SequenceElement> todo = new ArrayList<>();
        todo.add(seq);

        while (!todo.isEmpty()) {
            SequenceElement sub = todo.remove(0);

            if (sub instanceof SubSequence) {
                SubSequence tmp = (SubSequence) sub;

                todo.addAll(tmp.getContent());
            }

            result.add(sub);
        }

        return result;
    }

    /**
     * lists all subsequences that contain the specified
     * point in the main-sequence
     *
     * @param seq a subsequence
     * @param at  the point in the string that is searched
     * @return a list of subsequences
     */
    public static List<SequenceElement> listElements(SubSequence seq, int at) {
        return null;
    }
}