import java.util.Arrays;

/**
 * Created by Babits on 21/04/2015.
 */
public class SO {
    public static void main(String[] args) {
        int[][] blocks = {
                {2, 1, 0, 0}, {2, 2, 0, 1}, {2, 1, 0, 3}, {2, 1, 2, 0}, {2, 1, 2, 3},
                {1, 2, 2, 1}, {1, 1, 3, 1}, {1, 1, 3, 2}, {1, 1, 4, 0}, {1, 1, 4, 3}
        };

        final char CORNER = '+';
        final char VERTICAL = '|';
        final char HORIZONTAL = '-';

        int cellsPerLine = 0;
        int cellsPerColumn = 0;

        //calculate the size of the tray in cells (can be left out, if the size is fixed)
        for (int[] block : blocks) {
            int blockXRight = block[1] + block[2];
            int blockYBottom = block[0] + block[3];

            if (cellsPerLine < blockXRight)
                cellsPerLine = blockXRight;
            if (cellsPerColumn < blockYBottom)
                cellsPerColumn = blockYBottom;
        }

        //calculate the required space for chars
        int width = cellsPerLine * 4 + 1;//3 chars per cell, 1 char each to separate cells
        int height = cellsPerColumn * 2 + 1;//1 char height per cell, 1 char each to separate cells

        //use this later to create a string
        //to get the first index of line n use width * n;
        char[][] tmp = new char[width][height];
        for (char[] ln : tmp)
            Arrays.fill(ln, ' ');

        for (int[] block : blocks) {
            int x = block[2] * 4;//left upper corner of the cell in the chararray
            int y = block[3] * 2;

            int blockwidth = block[1] * 4;
            int blockheight = block[0] * 2;

            //insert all "+" tokens
            for (int i = x; i < x + blockwidth; ) {
                tmp[i][y] = CORNER;
                tmp[i][y + blockheight] = CORNER;
                i += 4;
            }
            for (int i = y; i < y + blockheight; ) {
                tmp[x][i] = CORNER;
                tmp[x + blockwidth][i] = CORNER;
                i += 2;
            }

            //insert all "-"
            for (int i = x; i < x + blockwidth; i++) {
                if (i % 4 == 0)//inserted a plus here
                    continue;

                tmp[i][y] = VERTICAL;
                tmp[i][y + blockheight] = VERTICAL;
            }

            //insert all "|"
            for (int i = y + 1; i < y + blockheight; ) {
                tmp[x][i] = HORIZONTAL;
                tmp[x + blockwidth][i] = HORIZONTAL;

                i += 2;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (char[] line : tmp) {
            builder.append(line);
            builder.append('\n');
        }

        System.out.println(builder.toString());
    }
}
