package dove.util.sequence;

public class SeqPosPtr {
    private SequenceElement elem;

    private int at;

    public SeqPosPtr(SequenceElement elem, int at) {
        this.elem = elem;

        this.at = at;
    }

    public SequenceElement getElem() {
        return elem;
    }

    public int getAt() {
        return at;
    }
}
