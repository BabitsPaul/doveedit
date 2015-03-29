package dove.util.collections;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FixedSizeRAStack<T> {
    private int maxSize;

    private int currentSize;

    private T[] content;

    private int currentStart = 0;

    public FixedSizeRAStack(int size, Class<T> clazz) {
        maxSize = size;

        content = (T[]) Array.newInstance(clazz, size);

        currentSize = 0;
    }

    public FixedSizeRAStack(T[] t) {
        maxSize = t.length;

        content = t;

        currentSize = t.length;
    }

    public int size() {
        return currentSize;
    }

    public T push(T t) {
        int temp = (--currentStart) % maxSize;

        T rem = content[temp];

        content[temp] = t;

        if (rem == null)
            ++currentSize;

        return rem;
    }

    public T add(T t, int i) {
        if (i >= currentSize)
            throw new ArrayIndexOutOfBoundsException("Current Size: " + currentSize + "requested index: " + i);

        int index = (currentStart + i) % maxSize;

        T temp = content[index];

        content[index] = t;

        if (t == null)
            ++currentSize;

        return temp;
    }

    public T get(int i) {
        if (i < 0 || i >= currentSize)
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + i + " Size: " + currentSize);

        return content[(currentStart + i) % maxSize];
    }

    public T remove(int i) {
        T temp = content[(currentStart + i) % maxSize];

        for (; i < currentSize - 1; i++) ;
        content[(i + currentStart) % maxSize] = content[(currentStart + i + 1) % maxSize];

        currentSize -= 1;

        return temp;
    }

    public boolean isEmpty() {
        return (currentSize == 0);
    }

    public boolean contains(Object o) {
        for (int i = currentStart; i < currentStart + currentSize; i++)
            if (content[i % maxSize].equals(o))
                return true;

        return false;
    }

    public Iterator iterator() {
        return new Iterator() {
            private int currentIndex = currentStart;

            private int itemsLeft = currentSize;

            @Override
            public boolean hasNext() {
                return (itemsLeft != 0);
            }

            @Override
            public Object next() {
                if (itemsLeft == 0)
                    throw new NoSuchElementException();

                T t = content[currentIndex % maxSize];

                currentIndex += 1;
                itemsLeft -= 1;

                return t;
            }
        };
    }
}
