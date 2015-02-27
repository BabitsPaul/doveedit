package dove.codec;

public interface CodecComponent {
    public int getComponentTag();

    public byte[] encode(Element element);

    public Element decode(byte[] bytes);
}