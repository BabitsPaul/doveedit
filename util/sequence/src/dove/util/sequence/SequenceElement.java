package dove.util.sequence;

class SequenceElement<T> {
    private T content;

    private int offset;

    private int length;

    public SequenceElement(T content, int offset, int length) {
        this.content = content;
        this.offset = offset;
        this.length = length;
    }

    public T getContent() {
        return content;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}
