package dove.util.collections;

import java.util.ArrayList;
import java.util.Comparator;

public class SortedList<T>
        extends ArrayList<T> {
    private Comparator<T> comparator;

    public SortedList(Comparator<T> comparator) {
        super();

        this.comparator = comparator;
    }

    public boolean add(T t) {
        int min = 0;
        int max = size();
        int mid = (max + min) / 2;

        //binary search
        while (min < max) {
            int comp = comparator.compare(t, get(mid));

            if (comp > 0) {
                min = mid + 1;
            }
            else {
                max = mid - 1;
            }

            mid = (min + max) / 2;
        }

        super.add(mid, t);

        return true;
    }

    public T get(T t) {
        return null;
    }

    /**
     * performs a binarysearch upon the list to find the specified
     * content matching helper
     *
     * @param helper an comparator to check if the value matches an element
     * @param value  the value to check
     * @param <V>    the type of value
     * @return the first matching element or null
     */
    public <V> T searchBinary(SearchHelper<T, V> helper, V value) {
        if (isEmpty())
            return null;

        int low = 0;
        int high = size();
        int compare = 0;

        do {
            int position = (low + high) / 2;

            compare = helper.compare(get(position), value);

            if (compare < 0)
                low = position;
            else if (compare > 0)
                high = position;
            else
                return get(position);
        }
        while (compare != 0 && low != high);

        return null;
    }

    @FunctionalInterface
    public static interface SearchHelper<T, V> {
        public int compare(T t, V v);
    }
}