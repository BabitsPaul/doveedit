package dove.cmd.runable;

import dove.cmd.CommandLineData;
import dove.util.treelib.Tree;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunnableParser {
    public static final String DEFINE = "define";

    private String txt;
    private CommandLineData data;
    private List<Pair<Integer, Integer>> indentions;
    private Tree<String> codeStruct;
    private List<String> lines;

    public void parse(String txt, CommandLineData data)
            throws ParserException, IOException {
        this.txt = txt;
        this.data = data;

        listLines();

        listIndentions();

        codeStruct();
    }

    /**
     * list all lines in the specified text
     *
     * @throws IOException
     */
    private void listLines()
            throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(txt.getBytes())));

        lines = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null)
            lines.add(line);

        br.close();
    }

    /**
     * list indention changes in the given input text
     * the output list contains pairs of (line , indention)
     * where indention is the number of '\t' characters at the
     * start of the line
     */
    private void listIndentions() {
        indentions = new ArrayList<>();

        int currentIndention = 0;

        Iterator<String> iter = lines.iterator();

        int atLine = 0;
        while (iter.hasNext()) {
            String line = iter.next();

            boolean isEmpty = true;

            for (char c : line.toCharArray()) {

                isEmpty = !SyntaxConstants.NON_SPACE.contains(Character.toString(c));

                if (!isEmpty)
                    break;
            }

            if (!isEmpty) {
                int indention = 0;
                for (; indention < line.length() && line.charAt(indention) == '\t'; indention++) ;

                if (indention != currentIndention)
                    indentions.add(new Pair<>(atLine, indention));
            }

            ++atLine;
        }
    }

    private void codeStruct()
            throws ParserException {
        codeStruct = new Tree<>(String.class);

        Tree<String> currentNode = codeStruct;

        Pair<Integer, Integer> prevIndention = new Pair<>(0, indentions.get(0).getValue() - 1);
        for (int i = 0; i < indentions.size(); i++) {
            Pair<Integer, Integer> ind = indentions.get(i);

            int deltaInd = ind.getValue() - prevIndention.getValue();

            if (deltaInd == 0 || deltaInd > 1) {
                //valid indentions may only differ by one (to right) and mustn't differ by 0
                throw new ParserException("Invalid indention", lines.get(ind.getKey()),
                        0, lines.get(ind.getKey()).length());
            }


        }
    }

    public static class ParserException
            extends Exception {
        private String line;
        private int startAt, endAt;

        public ParserException(String msg, String line, int startAt, int endAt) {
            super(msg);

            this.line = line;
            this.startAt = startAt;
            this.endAt = endAt;
        }

        public String getLine() {
            return line;
        }

        public int getStartAt() {
            return startAt;
        }

        public int getEndAt() {
            return endAt;
        }
    }
}