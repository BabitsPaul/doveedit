package dove.codec.base;

import java.io.InputStream;
import java.io.OutputStream;

public class Base {
    public native OutputStream base64Encode(InputStream is);

    public native OutputStream base64Decode(InputStream is);

    public native OutputStream base32Encode(InputStream is);

    public native OutputStream base32Decode(InputStream is);

    public native OutputStream base16Encode(InputStream is);

    public native OutputStream base16Decode(InputStream is);
}
