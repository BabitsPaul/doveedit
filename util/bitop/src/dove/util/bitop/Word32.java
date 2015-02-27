package dove.util.bitop;

public class Word32 {
    private int word;

    private byte[] bytes;

    public Word32(int val) {
        word = val;

        bytes = toBytes(val);
    }

    public Word32(byte[] val) {
        bytes = val;

        word = toInt(val);
    }

    public static Word32 add(Word32... w) {
        int sum = 0;

        for (Word32 word : w)
            sum += word.getWord();

        return new Word32(sum);
    }

    public static byte[] toBytes(int val) {
        byte[] result = new byte[4];

        for (int i = 0; i < 4; i++)
            result[i] = (byte) (0xFF & (val >> (8 * (3 - i))));

        return result;
    }

    public static int toInt(byte[] bytes) {
        int result = 0;

        for (int i = 0; i < 4; i++)
            result |= (bytes[i] << (i * 8));

        return result;
    }

    public int getWord() {
        return word;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
