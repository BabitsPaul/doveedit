package dove.util.misc;

public class StringHelper {
    public static Character[] castToChar(char[] small) {
        Character[] result = new Character[small.length];
        for (int i = 0; i < result.length; i++)
            result[i] = small[i];

        return result;
    }
}
