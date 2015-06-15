package dove.cmd.runable;

public class SyntaxConstants {
    public static final String VALID_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + /* alphabet */
                    "0123456789" + /* numbers */
                    "+-/*^%|" + /* arithmetic operators */
                    "_$#[](){}><=\\?!\"�" + /* other signs */
                    " \t" + System.getProperty("line.separator"); /* special signs */

    public static final String SPACE =
            " \t" + System.getProperty("line.separator");

    public static final String NON_SPACE =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + /* alphabet */
                    "0123456789" + /* numbers */
                    "+-/*^%|" + /* arithmetic operators */
                    "_$#[](){}><=\\?!\"�"; /* other signs */

    public static final String[] KEYWORDS = new String[]{
            "define",
            "for",
            "if",
            "do",
            "while"
    };
}