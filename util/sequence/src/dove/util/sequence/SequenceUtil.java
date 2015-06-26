package dove.util.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Utility-class for processing Sequences
 * <p>
 * This class provides methods for extracting
 * specific information from sequences/subsequences
 */
public class SequenceUtil {
    /**
     * creates a new sequence with the given string as
     * content. This sequence doesn't contain any subsequences
     *
     * @param txt the content of the sequence
     * @return a sequence containing txt
     */
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
     * point in the main-sequence. this list is ordered from
     * toplevel to bottom
     *
     * @param seq a subsequence
     * @param at  the point in the string that is searched
     * @return a list of subsequences
     */
    public static List<SequenceElement> listElements(SubSequence seq, int at) {
        List<SequenceElement> result = new ArrayList<>();

        int totalOffset = 0;

        SequenceElement e = seq;

        while (e instanceof SubSequence) {
            result.add(e);

            SubSequence tmp = (SubSequence) e;
            e = tmp.getContent().get(searchPt(tmp, 0, at - totalOffset));
            totalOffset += e.getOffset();
        }

        result.add(e);

        return result;
    }

    /**
     * searches for the subsequence of this sequence that contains
     * the point pt in the string held by this subsequence. This search is
     * implemented as binarysearch
     * <p>
     * any element before startAt will be ignored in the search. Note that this
     * means that the subsequence matching the above specified constraints might
     * not be part of the searchscope!!!
     *
     * @param startAt the starting-point of the search
     * @param pt      the point to search for
     * @return the index of the subsequence containing pt
     */
    public static int searchPt(SubSequence seq, int startAt, int pt) {
        List<SequenceElement> content = seq.getContent();
        int size = content.size();

        boolean found = false;
        int elem_ind = startAt + (size - startAt) / 2;
        int span_size = (size - startAt) / 2;

        while (!found) {
            SequenceElement sub = content.get(elem_ind);

            if (sub.getOffset() <= pt && sub.getOffset() + sub.getLength() >= pt)
                return elem_ind;
            else if (sub.getOffset() < pt)
                elem_ind -= span_size;
            else
                elem_ind += span_size;

            span_size /= 2;
        }

        return -1;
    }

    /**
     * removes all characters from start (inclusive) to end (exclusive)
     * from the given sequence. This also includes subsequences marked in seq
     *
     * @param seq   the sequence from which the characters are removed
     * @param start the start of the subsequence to remove
     * @param end   end of the subsequence to remove
     */
    public static void remove(SubSequence seq, int start, int end) {

    }

    public static SubSequence subSequence(SubSequence seq, int start, int end) {
        SubSequence result = new SubSequence(seq.getOriginal().substring(start, end),
                seq.getOffset() + start, seq.getOffset() + end);

        seq.getContent().stream().
                filter(sub ->
                        (sub.getOffset() >= start && sub.getOffset() < end) ||
                                (sub.getOffset() + sub.getLength() < end && sub.getOffset() + sub.getLength() >= start)).
                forEachOrdered(sub -> result.getContent().add(sub));

        /* copy the first element of each subsequence recursively until the leaf of
         * the tree-structure is reached. this is used to ensure that all subsequences
          * are in range of (start , end) */
        int totalOffsetFirst = 0;
        boolean copyFirstDone = seq.getContent().isEmpty();
        SequenceElement first = (copyFirstDone ? null : seq.getContent().get(0));
        SubSequence prnt = result;
        while (!copyFirstDone) {
            if (first instanceof SubSequence) {

            } else {
                StringElement str = (StringElement) first;

                int offset = start - totalOffsetFirst;
                int length = end - start < str.getLength() ? end - start : str.getLength();

                prnt.getContent().add(new StringElement(
                        str.getContent().substring(offset, length), start, length));

                copyFirstDone = true;
            }

            totalOffsetFirst += first.getOffset();
            copyFirstDone |= totalOffsetFirst >= start;
        }

        return result;
    }

    /**
     * @param seq the sequence to copy
     * @return a clone
     * @see SubSequence#deepClone()
     * <p>
     * creates a deepclone of the given subsequence. This means the complete
     * content of seq is cloned. Changes on content of seq won't affect the copy
     * and vice versa.
     */
    public static SubSequence deepClone(SubSequence seq) {
        return seq.deepClone();
    }

    /**
     * @param sub a sequence to copy
     * @return a shallow copy
     * @see SubSequence#shallowClone()
     * <p>
     * creates a shallow clone of the given subsequence. This means the copy
     * contains references to the same objects that are used in sub. Thus
     * changes in seq will also affect the copy and vice versa.
     */
    public static SubSequence shallowClone(SubSequence sub) {
        return sub.shallowClone();
    }

    public static <T> SubSequence transform(Function<String, T> f) {
        //TODO
        return null;
    }
}