package dove.util.version;

public class Version
        implements Comparable<Version> {
    public static DefaultVersionComparator comparator = new DefaultVersionComparator();

    private int[]  verNum;
    private String stringRep;

    public Version(int... verNum) {
        this.verNum = verNum;

        stringRep = stringRep();
    }

    public Version(String version) {
        String[] verNums = version.split(".");

        verNum = new int[verNums.length];

        for (int i = 0; i < verNums.length; i++) {
            verNum[i] = Integer.parseInt(verNums[i]);
        }

        stringRep = version;
    }

    public String stringRep() {
        String result = "";

        for (int ver : verNum) {
            result += ver;
            result += ".";
        }

        result = result.substring(0, result.length() - 1);

        return result;
    }

    @Override
    public int compareTo(Version version) {
        return comparator.compare(this, version);
    }

    public int[] getVerNum() {
        return verNum;
    }
}