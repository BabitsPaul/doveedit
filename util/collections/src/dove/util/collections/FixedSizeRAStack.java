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
        int temp = (currentStart - 1) % maxSize;

        T rem = content[temp];

        content[temp] = t;

        if (currentSize < maxSize)
            ++currentSize;

        currentStart = temp;

        return rem;
    }

    public T get(int i) {
        if (i < 0 || i >= currentSize)
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + i + " Size: " + currentSize);

        return content[(currentStart + i) % maxSize];
    }

    public T pop() {
        if (currentSize < 1)
            throw new NoSuchElementException("Stack is empty");

        T temp = content[currentStart];

        currentStart += 1;
        currentStart %= maxSize;

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
