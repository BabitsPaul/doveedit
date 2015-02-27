package dove.codec;

public interface Codec {
    public CodecElement getCodecElement(Element e);

    public void checkFormat(String format);
}
