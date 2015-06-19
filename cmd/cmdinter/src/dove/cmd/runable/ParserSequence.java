package dove.cmd.runable;

import java.util.ArrayList;
import java.util.List;

public class ParserSequence
        extends SequenceElement<List<SequenceElement>> {
    private String original;

    private List<Boolean> isString;

    public ParserSequence(String original) {
        super(new ArrayList<>(), 0, original.length());

        this.original = original;

        isString = new ArrayList<>();
    }

    public void remove(Object o) {
        int index = getContent().indexOf(o);

        if (index != -1) {
            getContent().remove(index);

            isString.remove(index);
        }
    }

    public void remove(int i) {
        getContent().remove(i);

        isString.remove(i);
    }

    public SequenceElement get(int i) {
        return getContent().get(i);
    }

    public boolean isStringElement(int i) {
        return isString.get(i);
    }

    public ParserSequence subsequence(int offset, int length) {
        ParserSequence result = new ParserSequence(original.substring(offset, offset + length));


        return result;
    }

    public void replace(int offset, int length, ParserSequence sequence) {

    }
}