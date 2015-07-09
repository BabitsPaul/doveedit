package dove.cmd.runable;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.operator.OperatorStub;
import dove.util.misc.StringHelper;
import dove.util.sequence.Sequence;
import dove.util.treelib.Tree;
import dove.util.treelib.TreeBuildException;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RunnableParser {
    private String txt;
    private CommandLineData data;
    private List<Pair<Integer, Integer>> indentions;
    private Tree<String> indentionStruct;
    private List<String> lines;
    private Tree<Tree<String>> bracketStruct;
    private Tree<Tree<Data>> executable;
    private List<ParserException> exceptions;

    public void parse(String txt, CommandLineData data)
            throws ParserException, IOException {
        this.txt = txt;
        this.data = data;
        exceptions = new ArrayList<>();

        listLines();

        listIndentions();

        indentionStruct();

        parseBrackets();

        toExec();
    }

    ///////////////////////////////////////////////////////////////////////////
    // code structure
    //
    // this block contains method relevant for parsing the basic structure of
    // the code. this is mainly specified by the indention-structure of the code
    ///////////////////////////////////////////////////////////////////////////

    /**
     * list all lines in the specified text
     *
     * @throws IOException
     */
    public void listLines()
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
     *
     * this list aswell contains the first line with indention = 0
     * and the last line with indention = 0
     */
    public void listIndentions() {
        indentions = new ArrayList<>();
        indentions.add(new Pair<>(0, 0));

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

        indentions.add(new Pair<>(atLine, 0));
    }

    /**
     * parses the code into a tree-like structure.
     * This way, the code can be easily traversed by order.
     *
     * @throws ParserException
     */
    public void indentionStruct()
            throws ParserException
    {
        indentionStruct = new Tree<>();

        Tree<String> currentNode = indentionStruct;

        Pair<Integer, Integer> prevIndention = indentions.get(0);
        for (int i = 1; i < indentions.size(); i++) {
            Pair<Integer, Integer> ind = indentions.get(i);

            int deltaInd = ind.getValue() - prevIndention.getValue();

            //simplify all lines by removing leading \t
            for (int l = prevIndention.getKey(); l < ind.getKey(); l++)
                try {
                    currentNode.add(lines.get(l).substring(prevIndention.getValue()));
                } catch (TreeBuildException ignored) {
                }//never thrown

            if (deltaInd > 0) {
                Tree[] tmp = currentNode.getChildren().toArray(new Tree[0]);
                currentNode = (Tree<String>) tmp[tmp.length - 1];
            } else {
                for (int l = 0; l < -deltaInd; l++)
                    currentNode = currentNode.getParent();
            }

            prevIndention = ind;
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // bracket parsing
    //
    // this part parses the code structure from
    // the brackets contained in the code
    ///////////////////////////////////////////////////////////////////////

    public void parseBrackets()
            throws ParserException {
        try {
            bracketStruct = indentionStruct.<Tree<String>>transform(t -> {
                String code = t;

                Character[] chars = StringHelper.castToChar(code.toCharArray());
                Sequence<Character> seq = new Sequence<>(chars, code.length());

                //list all opening indices
                List<Integer>[] openingIndices = new List[SyntaxConstants.OPENING_BRACKETS.length];

                for (int i = 0; i < openingIndices.length; i++) {
                    openingIndices[i] = new ArrayList<>();

                    char o_b = SyntaxConstants.OPENING_BRACKETS[i];
                    char c_b = SyntaxConstants.CLOSING_BRACKETS[i];

                    int ind_o = code.indexOf(o_b);
                    int ind_c = code.indexOf(c_b);
                    int ci = 0;

                    while (ci < code.length()) {
                        if (ind_o > ind_c) {
                            int offset = openingIndices[i].remove(openingIndices[i].size() - 1);
                            int length = ind_c - offset;

                            Character[] c = new Character[length];
                            System.arraycopy(chars, offset, c, 0, length);
                            seq.mark(chars, offset, length);

                            ind_c = code.indexOf(c_b, ind_c);
                        } else {
                            openingIndices[i].add(ind_o);

                            ind_o = code.indexOf(o_b, ind_o);
                        }
                    }
                }

                Tree<String> result = new Tree<>();

                return new Tree<>(result);
            });
        } catch (TreeBuildException e) {
            throw new ParserException(ParserException.NOT_SPECIFIED, e.getMessage(), "unknown", -1, -1);
        }
    }

    ///////////////////////////////////////////////////////////////
    // logical parsing
    //
    // this part contains the main-parsing
    // this includes the parsing of codeblocks into executable
    // statements
    ///////////////////////////////////////////////////////////////

    public void toExec()
            throws ParserException {
        try {
            executable = indentionStruct.<Tree<Data>>transform(s ->
            {
                try {
                    return toExecTree(s);
                } catch (ParserException e) {
                    exceptions.add(e);

                    return null;
                }
            });
        } catch (TreeBuildException e) {
            throw new ParserException(ParserException.NOT_SPECIFIED, e.getMessage(), "unknown", -1, -1);
        }
    }

    public Tree<Tree<Data>> toExecTree(String code)
            throws ParserException {

        Tree<Tree<Data>> result = new Tree<>();

        return result;
    }

    private Map<Integer, OperatorStub> listOperators(String code) {
        Map<Integer, OperatorStub> result = new HashMap<>();


        return result;
    }
}