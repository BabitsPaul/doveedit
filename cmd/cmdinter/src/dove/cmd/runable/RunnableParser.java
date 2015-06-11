package dove.cmd.runable;

import dove.cmd.CommandLineData;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunnableParser {
    public static String DEFINE = "define";

    public void parse(String txt, CommandLineData data) {

    }

    private HashMap<String, Integer> keywords(String txt) {
        HashMap<String, Integer> indices = new HashMap<>();



        return null;
    }

    private List<Pair<Integer, Integer>> listIndentions(String txt)
            throws IOException {
        List<Pair<Integer, Integer>> result = new ArrayList<>();

        String currentIndention = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(txt.getBytes())));

        String line;
        while ((line = br.readLine()) != null) {
            boolean isEmpty = true;

            for (char c : line.toCharArray()) {

            }
        }

        return result;
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