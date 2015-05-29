package dove.cmd.runable;

import dove.cmd.CommandLineData;

import java.util.HashMap;

public class RunnableParser {
    public void parse(String txt, CommandLineData data) {

    }

    private HashMap<String, Integer> keywords(String txt) {
        HashMap<String, Integer> indices = new HashMap<>();


        return null;
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