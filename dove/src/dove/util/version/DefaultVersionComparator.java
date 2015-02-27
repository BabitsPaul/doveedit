package dove.util.version;

import java.util.Comparator;

public class DefaultVersionComparator
        implements Comparator<Version> {
    @Override
    public int compare(Version o1, Version o2) {
        int[] verNumA = o1.getVerNum();
        int[] verNumB = o2.getVerNum();

        for (int i = 0; i < verNumA.length && i < verNumB.length; i++) {
            if (verNumA[i] != verNumB[i]) {
                return (verNumA[i] - verNumB[i]);
            }
        }

        if (verNumA.length > verNumB.length) return 1;

        return (verNumA.length == verNumB.length ? 0 : -1);
    }
}