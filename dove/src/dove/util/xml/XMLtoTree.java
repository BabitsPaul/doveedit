package dove.util.xml;

import dove.util.treelib.Tree;

import java.util.ArrayList;

public class XMLtoTree {
    public Tree<Object> parseTree(String xml) {
        return null;
    }

    public Tree<String> parseStruct(String in) {
        String xml = in.toString();

        Tree<String> result = new Tree<>("");

        ArrayList<String> bracketOpenStack = new ArrayList<>();

        do {
            String bracketContent;
            String name;

            boolean isPlainText;
            boolean isSelfEnclosing;

            int nextBracket = xml.indexOf('<');
            int nextBracketClose = xml.indexOf('>');

            if (nextBracket > 0) {
                bracketContent = xml.substring(0, nextBracket);

                isPlainText = true;

                xml = xml.substring(nextBracket);
            }
            else {
                bracketContent = xml.substring(nextBracket, nextBracketClose + 1);

                isPlainText = false;

                xml = xml.substring(nextBracketClose + 1);
            }
        }
        while (xml.length() > 0);

        return result;
    }
}