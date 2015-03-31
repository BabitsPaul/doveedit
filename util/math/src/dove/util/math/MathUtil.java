package dove.util.math;

public class MathUtil {
    /**
     * returns the sign of val
     * <p>
     * 0 if val == 0
     * -1 if val < 0
     * 1 if val > 0
     *
     * @param val sign of this number will be returned
     * @return see explanation
     */
    public static int getSign(int val) {
        if (val < 0)
            return -1;
        else if (val > 0)
            return 1;
        else
            return 0;
    }

    /**
     * this is a utilityclass providing
     * a interpreter for a multidimensional cube
     */
    private static class MultiCube {
        public Corner[] corners;

        public MultiCube(int dimension) {
            int corner_num = (int) Math.pow(2, dimension);

            //create corners
            corners = new Corner[corner_num];

            for (int d = 0; d < corner_num; d++) {
                corners[d] = new Corner(dimension, d);
            }

            //correlate corners
            for (int c = 0; c < corner_num; c++) {
                Corner corner = corners[c];

                int cBitCode = corner.bitID;

                //find correclate corners
                //and save them as peers
                for (int d = 0; d < dimension; d++)
                    corner.peers[d] = corners[(cBitCode | (1 << d))];

            }
        }
    }

    private static class Corner {
        public int bitID;

        public Corner[] peers;

        public Corner(int dimension, int bitID) {
            this.bitID = bitID;
            peers = new Corner[dimension];
        }
    }
}