package dove.cmd.runable;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;
import dove.cmd.model.operator.OperatorStub;
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
    private Tree<String> codeStruct;
    private List<String> lines;
    private Tree<Tree<Data>> executable;
    private List<ParserException> exceptions;

    public void parse(String txt, CommandLineData data)
            throws ParserException, IOException {
        this.txt = txt;
        this.data = data;
        exceptions = new ArrayList<>();

        listLines();

        listIndentions();

        codeStruct();

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
     *
     * this list aswell contains the first line with indention = 0
     * and the last line with indention = 0
     */
    private void listIndentions() {
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
    private void codeStruct()
            throws ParserException
    {
        codeStruct = new Tree<>();

        Tree<String> currentNode = codeStruct;

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

    ///////////////////////////////////////////////////////////////
    // logical parsing
    //
    // this part contains the main-parsing
    // this includes the parsing of codeblocks into executable
    // statements
    ///////////////////////////////////////////////////////////////

    private void toExec()
            throws ParserException {
        try {
            executable = codeStruct.<Tree<Data>>transform(s ->
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

    private Tree<Tree<Data>> toExecTree(String code)
            throws ParserException {
        Tree<Tree<Data>> result = new Tree<>();

        return result;
    }

    private Tree<Tree<String>> parseBrackets(String code) {
        List<Integer>[] openingIndices = new List[SyntaxConstants.OPENING_BRACKETS.length];

        for (int i = 0; i < openingIndices.length; i++)
            openingIndices[i] = new ArrayList<>();

        for (char b : SyntaxConstants.OPENING_BRACKETS) {

        }

        return null;
    }

    private Map<Integer, OperatorStub> listOperators(String code) {
        Map<Integer, OperatorStub> result = new HashMap<>();


        return result;
    }
}