package dove.cmd.runable;

import dove.cmd.CommandLineData;
import dove.cmd.model.datatypes.Data;
import dove.util.treelib.Tree;
import dove.util.treelib.TreeBuildException;

import java.io.IOException;
import java.util.*;

public class RunnableParser {
    private static final int STATE_PARSER_UNINITIALIZED = -1;
    private static final int STATE_PARSER_INIITED = 0;
    private static final int STATE_STRINGS_RESOLVED = 1;
    private static final int STATE_PREPROCESSOR_DONE = 2;
    private static final int STATE_RESOLVED_LINES = 3;
    private static final int STATE_RESOLVED_BRACKETS = 4;

    private int state = STATE_PARSER_UNINITIALIZED;

    private String txt;
    private CommandLineData data;

    private TreeSet<Integer> lines;

    private Map<Integer, Integer> bracketPeersCloseToOpen;
    private Map<Integer, Integer> bracketPeersOpenToClose;
    private Tree<BracketRep> bracketStruct;

    private Map<String, String> preprocessor;

    private Map<String, String> textExtract;

    private Tree<Data> executable;
    private List<ParserException> exceptions;

    public static void main(String[] args)
            throws ParserException {
        RunnableParser parser = new RunnableParser();
        parser.txt = "ab{dfe[erear(e)]sdf}[] dfsle (efse)";
        parser.parseBrackets();
        System.out.println(parser.bracketStruct);
    }

    //////////////////////////////////////////////////////////////////////
    // line parsing
    //
    // generate data representing the general structure of the
    // code (lines, etc.)
    //////////////////////////////////////////////////////////////////////

    public void parse(String txt, CommandLineData data)
            throws ParserException {
        this.txt = txt;
        this.data = data;
        state = STATE_PARSER_INIITED;

        /* string extraction */
        extractStrings();
        state = STATE_STRINGS_RESOLVED;

        /* preprocessor */
        parsePreprocessor();
        state = STATE_PREPROCESSOR_DONE;

        /* generate general data */
        listLines();
        state = STATE_RESOLVED_LINES;

        /* bracket parsing */
        checkBrackets();

        parseBrackets();
        state = STATE_RESOLVED_BRACKETS;
    }

    ///////////////////////////////////////////////////////////////////////
    // bracket parsing
    //
    // this part parses the code structure from
    // the brackets contained in the code
    ///////////////////////////////////////////////////////////////////////

    /**
     * list all lines in the specified text
     *
     * @throws IOException
     */
    public void listLines()
            throws ParserException {
        checkState(STATE_STRINGS_RESOLVED);

        lines = new TreeSet<>();

        int nline = txt.indexOf('\n');

        while (nline != -1) {
            lines.add(nline + 1);

            nline = txt.indexOf('\n');
        }
    }

    /**
     * transforms the tree built from the indention-structure
     * into a tree of trees containing the bracket-structure of
     * the code
     * <p>
     * constraints: the code has a valid bracket-structure
     *
     * @throws ParserException
     */
    public void parseBrackets()
            throws ParserException {
        checkState(STATE_RESOLVED_LINES);

        //the mapping of each bracket to its peer
        bracketPeersOpenToClose = new HashMap<>();
        bracketPeersCloseToOpen = new HashMap<>();

        //the final tree containing the result for this piece of code
        bracketStruct = new Tree<>();

        //all brackettoken compiled into strings (b_o for opening , b_c for closing brackets)
        String brackets_open = new String(SyntaxConstants.OPENING_BRACKETS);
        String brackets_close = new String(SyntaxConstants.CLOSING_BRACKETS);

        //list of all currently opened brackets with their respective type
        //(aka index of the char in b_o)
        ArrayList<Integer> openingIndices = new ArrayList<>();
        ArrayList<Integer> openingType = new ArrayList<>();

        //the parent of the next completed brackets
        Tree<BracketRep> current_node = bracketStruct;
        //index of the last bracket
        int last_bracket = 0;

        //loop over all characters
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);

            //check whether this character is a opening/closing bracket or none
            int type_open = brackets_open.indexOf(c);
            int type_close = (type_open == -1 ? brackets_close.indexOf(c) : -1);

            if (type_open != -1) {
                /* opening bracket */

                //store the index and type of the bracket in the stack
                openingIndices.add(0, i);
                openingType.add(0, type_open);

                //update the tree to match the structure of the brackets
                Tree<BracketRep> tmp = new Tree<>(new BracketRep(txt.substring(last_bracket, i), type_open));
                try {
                    current_node.add(tmp);
                } catch (TreeBuildException ignored) {
                }//guaranteed to never be thrown
                current_node = tmp;

                //update the index of the last bracket
                last_bracket = i;
            } else if (type_close != -1) {
                /* closing bracket */

                //retrieve the last opening bracket
                int peer_bracket_type = openingType.remove(0);
                int peer_bracket_index = openingIndices.remove(0);

                //generate the node and insert it into the tree
                Tree<BracketRep> n_t = new Tree<>(new BracketRep(txt.substring(last_bracket, i), peer_bracket_type));
                try {
                    current_node.add(n_t);
                } catch (TreeBuildException ignored) {
                }//never thrown -> ignore
                //step out
                current_node = current_node.getParent();

                //store this value and it's peer in the map
                bracketPeersCloseToOpen.put(i, peer_bracket_index);
                bracketPeersOpenToClose.put(peer_bracket_index, i);

                //update index of last bracket
                last_bracket = i + 1;
            }
        }
    }

    ///////////////////////////////////////////////////////////////
    // preprocessor
    //
    // prepare preprocessor macros and defines
    // to replace them in the resulting code
    ///////////////////////////////////////////////////////////////

    /**
     * check whether the bracket-structure of the code is valid
     * this only affects the general assembly of the brackets, not
     * whether they are in a valid place respecting keywords/syntax, but
     * whether for each opening bracket a matching closing bracket exists
     *
     * @throws ParserException if the code has an invalid bracket-structure
     */
    public void checkBrackets()
            throws ParserException {
        checkState(STATE_RESOLVED_LINES);

        final String bracketOpen = new String(SyntaxConstants.OPENING_BRACKETS);
        final String bracketClose = new String(SyntaxConstants.CLOSING_BRACKETS);

        ArrayList<Integer> bracketType = new ArrayList<>();

        char c;

        for (int i = 0; i < txt.length(); i++) {
            c = txt.charAt(i);

            int type_open = bracketOpen.indexOf(c);
            int type_close = bracketClose.indexOf(c);
            int merge = type_close ^ type_open;

            if (merge != 0) {
                if (type_close != -1) {
                    if (bracketType.isEmpty())
                        throw new ParserException(ParserException.BRACKET_MISSING, "invalid token: " + c, getLine(i),
                                translateToInline(i), translateToInline(i));
                    else {
                        int type_o = bracketType.remove(0);

                        if (type_o != type_close)
                            throw new ParserException(ParserException.BRACKET_MISSING,
                                    "invalid token: " + c + " replace with: " + bracketClose.charAt(type_o), getLine(i),
                                    translateToInline(i), translateToInline(i));
                    }
                } else {
                    bracketType.add(0, type_open);
                }
            }
        }

        if (!bracketType.isEmpty()) {
            int txtEnd = txt.length() - 1;

            throw new ParserException(ParserException.BRACKET_MISSING,
                    "bracket missing: " + bracketClose.charAt(bracketType.get(0)), getLine(txtEnd),
                    translateToInline(txtEnd), translateToInline(txtEnd));
        }
    }

    ///////////////////////////////////////////////////////////////
    // string extractor
    //
    // replace all literals with their id
    // and map the ids to their literals
    ///////////////////////////////////////////////////////////////

    public void parsePreprocessor()
            throws ParserException {

    }

    ///////////////////////////////////////////////////////////////
    // logical parsing
    //
    // this part contains the main-parsing
    // this includes the parsing of codeblocks into executable
    // statements
    ///////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////
    // helper methods
    ////////////////////////////////////////////////////////////////

    public void extractStrings()
            throws ParserException {
        checkState(STATE_PARSER_INIITED);

        int tmp;
    }

    public String getLine(int atChar)
            throws ParserException {
        checkState(STATE_RESOLVED_LINES);

        if (atChar < 0 || atChar >= txt.length())
            throw new IllegalArgumentException("Invalid index - must be in range");

        Integer start = lines.floor(atChar);
        Integer end = lines.ceiling(atChar);

        if (start == null)
            start = 0;

        if (end == null)
            end = txt.length() - 1;
        else
            --end;//exclude \n

        return txt.substring(start, end);
    }

    public int translateToInline(int atChar)
            throws ParserException {
        checkState(STATE_RESOLVED_LINES);

        if (atChar < 0 || atChar >= txt.length())
            throw new IllegalArgumentException("Invalid index - must be in range");

        Integer start = lines.floor(atChar);

        if (start == null)
            return atChar;
        else
            return atChar - start;
    }

    /**
     * returns the index of the peer bracket to the char
     * at atChar, if the char at atChar is a valid bracket
     *
     * @param atChar
     * @return
     * @throws ParserException
     */
    public int getBracketPeer(int atChar)
            throws ParserException {
        checkState(STATE_RESOLVED_BRACKETS);

        char c = txt.charAt(atChar);

        int type = (new String(SyntaxConstants.OPENING_BRACKETS).contains("" + c) ? 1 :
                new String(SyntaxConstants.CLOSING_BRACKETS).contains("" + c) ? 2 : 0);

        if (type == 0)
            throw new IllegalArgumentException("Invalid character - must be part of SyntaxConstants.OPENING_BRACKETS");
        else if (type == 1)
            return bracketPeersOpenToClose.get(atChar);
        else
            return bracketPeersCloseToOpen.get(atChar);
    }

    ////////////////////////////////////////////////////////////////
    // testing
    ////////////////////////////////////////////////////////////////

    public void checkState(int expected)
            throws ParserException {
        if (state < expected)
            throw new ParserException(ParserException.INTERNAL_ERROR, "Invalid state - hasn't loaded required data",
                    "unknown", -1, -1);
    }

    private static final class BracketRep {
        public int bracketType;
        public String content;

        public BracketRep(String s, int type) {
            this.content = s;
            bracketType = type;
        }
    }
}