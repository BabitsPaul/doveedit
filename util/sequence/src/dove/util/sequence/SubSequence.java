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
class SubSequence
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
    public SubSequence(String original) {
        super(new ArrayList<>(), 0, original.length());

        this.original = original;

        isString = new ArrayList<>();
    }

    /**
     * @param original
     * @param offset
     * @param length
     */
    protected SubSequence(String original, int offset, int length) {
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
     * replaces a given subsequence of this sequence with
     * a new sequence
     *
     * @param offset   start of the subsequence to replace
     * @param length   length of the subsequence to replace
     * @param sequence the sequence to insert
     */
    public void replace(int offset, int length, SubSequence sequence) {

    }

    /**
     * generates a shallow copy of this sequence
     * any manipulation on elements of this sequence
     * will aswell affect the copy
     *
     * @return a shallow copy of this sequence
     */
    public SubSequence shallowClone() {
        SubSequence result = new SubSequence(original, getOffset(), getLength());

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
    public SubSequence deepClone() {
        SubSequence result = shallowClone();

        List<SubSequence> todo = new ArrayList<>();
        todo.add(result);

        while (!todo.isEmpty()) {
            SubSequence seq = todo.remove(0);

            seq.getContent().replaceAll(sub -> {
                if (sub instanceof SubSequence) {
                    SubSequence cast = seq;
                    SubSequence tmp = new SubSequence(cast.original.toString(), cast.getOffset(), cast.getLength());

                    tmp.isString.addAll(cast.isString);
                    tmp.getContent().addAll(cast.getContent());

                    return tmp;
                }
                else
                    return new StringElement(((StringElement) sub).getContent(), sub.getOffset(), sub.getOffset());
            });

            seq.getContent().stream().
                    filter(sub -> sub instanceof SubSequence).
                    forEach(sub -> todo.add((SubSequence) sub));
        }

        return result;
    }

    public String getOriginal() {
        return original;
    }
}