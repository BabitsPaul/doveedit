package dove.util.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a String and allows the user to mark
 * subsequences and recursively subsequences of subsequences. This
 * structure is represented as multitree.
 * <p>
 * Most general string-processing methods like substring/subsequence, etc.
 * are implemented in this class aswell.
 */
class Sequence
        extends SequenceElement<List<SequenceElement>> {
    /**
     * The complete String represented by this class
     */
    private String original;

    /**
     * a list of all subsequences that specifies whether the given
     * subsequence is a String or a Sequence of Subsequences and Strings.
     */
    private List<Boolean> isString;

    /**
     * creates a new sequence from the given string
     *
     * @param original the original string
     */
    public Sequence(String original) {
        super(new ArrayList<>(), 0, original.length());

        this.original = original;

        isString = new ArrayList<>();
    }

    /**
     * @param original
     * @param offset
     * @param length
     */
    protected Sequence(String original, int offset, int length) {
        super(new ArrayList<>(), offset, length);

        this.original = original;

        isString = new ArrayList<>();
    }

    /**
     * @param o
     */
    public void remove(Object o) {
        int index = getContent().indexOf(o);

        if (index != -1) {
            getContent().remove(index);

            isString.remove(index);
        }
    }

    /**
     * @param i
     */
    public void remove(int i) {
        getContent().remove(i);

        isString.remove(i);
    }

    /**
     * @param i
     * @return
     */
    public SequenceElement get(int i) {
        return getContent().get(i);
    }

    /**
     * @param i
     * @return
     */
    public boolean isStringElement(int i) {
        return isString.get(i);
    }

    /**
     * generates the subsequence starting at offset with length length.
     * The resulting subsequence will hold all flags set in the original
     * sequence.
     *
     * @param offset start of the subsequence to copy
     * @param length length of the copy
     * @return a copy of the specified subsequence of this sequence
     */
    public Sequence subsequence(int offset, int length) {
        Sequence result = new Sequence(original.substring(offset, offset + length));

        int start = searchPt(0, offset);
        int end = searchPt(start, offset + length);

        List<SequenceElement> subseq = getContent().subList(start, end + 1);
        List<Boolean> isStr = isString.subList(start, end + 1);

        //since the sequenceelements at the start and the end of the subsequence
        //are not necassarily part of the resulting subsequence, we must reduce the
        //length of these elements to match the required length and offset
        //this recursively aswell affects elements of elements
        List<SequenceElement> todo = new ArrayList<>();

        SequenceElement tmp = subseq.remove(0);
        if (tmp instanceof Sequence) {
            Sequence seq = (Sequence) tmp;


            tmp = seq;
        } else {
            StringElement str = (StringElement) tmp;


            tmp = str;
        }
        subseq.add(0, tmp);

        result.isString = isStr;
        result.getContent().clear();
        result.getContent().addAll(subseq);

        return result;
    }

    /**
     * replaces a given subsequence of this sequence with
     * a new sequence
     *
     * @param offset   start of the subsequence to replace
     * @param length   length of the subsequence to replace
     * @param sequence the sequence to insert
     */
    public void replace(int offset, int length, Sequence sequence) {

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
    protected int searchPt(int startAt, int pt) {
        List<SequenceElement> content = getContent();
        int size = content.size();

        boolean found = false;
        int elem_ind = startAt + (size - startAt) / 2;
        int span_size = (size - startAt) / 2;

        while (!found) {
            SequenceElement seq = content.get(elem_ind);

            if (seq.getOffset() <= pt && seq.getOffset() + seq.getLength() >= pt)
                return elem_ind;
            else if (seq.getOffset() < pt)
                elem_ind -= span_size;
            else
                elem_ind += span_size;

            span_size /= 2;
        }

        return -1;
    }

    /**
     * generates a shallow copy of this sequence
     * any manipulation on elements of this sequence
     * will aswell affect the copy
     *
     * @return a shallow copy of this sequence
     */
    public Sequence shallowClone() {
        Sequence result = new Sequence(original, getOffset(), getLength());

        Collections.copy(result.isString, isString);
        Collections.copy(result.getContent(), getContent());

        return result;
    }

    /**
     * generates a deep copy of this sequence.
     * all elements of the sequence will aswell be
     * replaced (recursively aswell elements of elements, etc.).
     * This means a worse performance than shallowClone, but operations
     * on elements of this sequence won't affect the copy and vice versa.
     *
     * @return a deep copy of this sequence
     */
    public Sequence deepClone() {
        Sequence result = shallowClone();

        List<Sequence> todo = new ArrayList<>();
        todo.add(result);

        while (!todo.isEmpty()) {
            Sequence seq = todo.remove(0);

            seq.getContent().replaceAll(sub -> {
                if (sub instanceof Sequence)
                    return ((Sequence) sub).shallowClone();
                else
                    return new StringElement(((StringElement) sub).getContent(), sub.getOffset(), sub.getOffset());
            });

            seq.getContent().stream().
                    filter(sub -> sub instanceof Sequence).
                    forEach(sub -> todo.add((Sequence) sub));
        }

        return result;
    }
}