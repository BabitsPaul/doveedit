package dove.codec.sha1;

import java.io.IOException;

public class SHA1 {
    public native byte[] sha1(String fileName) throws IOException;
}
