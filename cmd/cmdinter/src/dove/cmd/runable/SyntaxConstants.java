package dove.cmd.runable;

import dove.cmd.model.operator.OperatorStub;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyntaxConstants {
    public static final String VALID_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + /* alphabet */
                    "0123456789" + /* numbers */
                    "+-/*^%|" + /* arithmetic operators */
                    "_$#[](){}><=\\?!\"§&^" + /* other signs */
                    " \t" + System.getProperty("line.separator"); /* special signs */

    public static final String SPACE =
            " \t" + System.getProperty("line.separator");

    public static final String NON_SPACE =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + /* alphabet */
                    "0123456789" + /* numbers */
                    "+-/*^%|" + /* arithmetic operators */
                    "_$#[](){}><=\\?!\"§"; /* other signs */

    public static final String[] KEYWORDS = new String[]{
            "define",
            "for",
            "if",
            "do",
            "while"
    };

    public static final String CONCAT_LINES = "\\";

    public static final char[] OPENING_BRACKETS = new char[]{'{', '[', '('};

    public static final char[] CLOSING_BRACKETS = new char[]{'}', ']', ')'};

    public static final int BLOCK_BRACKET = 0;
    public static final int ARRAY_BRACKET = 1;
    public static final int ROUND_BRACKET = 2;

    public static final char STRING_DELIMITER = '"';

    public static final Map<OperatorStub, Integer> PRIORITY_MAP = generatePriorityMap();

    private static final Map<OperatorStub, Integer> generatePriorityMap() {
        HashMap<OperatorStub, Integer> result = new HashMap<>();

        result.put(new OperatorStub("(", true, true), 0);
        result.put(new OperatorStub("[", true, true), 0);
        result.put(new OperatorStub("->", false, false), 0);
        result.put(new OperatorStub("++", true, false), 0);
        result.put(new OperatorStub("--", true, false), 0);

        result.put(new OperatorStub("++", true, true), 1);
        result.put(new OperatorStub("--", true, true), 1);
        result.put(new OperatorStub("+", true, true), 1);
        result.put(new OperatorStub("-", true, true), 1);
        result.put(new OperatorStub("!", true, true), 1);
        result.put(new OperatorStub("~", true, true), 1);

        result.put(new OperatorStub("*", false, false), 2);
        result.put(new OperatorStub("/", false, false), 2);
        result.put(new OperatorStub("%", false, false), 2);

        result.put(new OperatorStub("+", false, false), 3);
        result.put(new OperatorStub("-", false, false), 3);

        result.put(new OperatorStub(">>", false, false), 4);
        result.put(new OperatorStub("<<", false, false), 4);
        result.put(new OperatorStub(">>>", false, false), 4);

        result.put(new OperatorStub("<", false, false), 5);
        result.put(new OperatorStub(">", false, false), 5);
        result.put(new OperatorStub(">=", false, false), 5);
        result.put(new OperatorStub("<=", false, false), 5);

        result.put(new OperatorStub("==", false, false), 6);
        result.put(new OperatorStub("!=", false, false), 6);

        result.put(new OperatorStub("&", false, false), 7);

        result.put(new OperatorStub("^", false, false), 8);

        result.put(new OperatorStub("|", false, false), 9);

        result.put(new OperatorStub("&&", false, false), 10);

        result.put(new OperatorStub("||", false, false), 11);

        result.put(new OperatorStub("?:", false, false), 12);

        result.put(new OperatorStub("=", false, false), 13);
        result.put(new OperatorStub("+=", false, false), 13);
        result.put(new OperatorStub("-=", false, false), 13);
        result.put(new OperatorStub("*=", false, false), 13);
        result.put(new OperatorStub("/=", false, false), 13);
        result.put(new OperatorStub("%=", false, false), 13);
        result.put(new OperatorStub("&=", false, false), 13);
        result.put(new OperatorStub("^=", false, false), 13);
        result.put(new OperatorStub("|=", false, false), 13);
        result.put(new OperatorStub("<<=", false, false), 13);
        result.put(new OperatorStub(">>=", false, false), 13);
        result.put(new OperatorStub(">>>=", false, false), 13);

        return Collections.unmodifiableMap(result);
    }
}