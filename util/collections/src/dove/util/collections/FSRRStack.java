package dove.util.collections;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * FixedSizeRandomReadStack
 * <p>
 * this stack implementation allows the user
 * to hold a specific number of values.
 * <p>
 * If the stack is filled and a new value is added, the oldest value will
 * be discarded
 *
 * @param <T>
 */
public class FSRRStack<T> {
    private int maxSize;

    private int currentSize;

    private T[] content;

    private int currentStart = 0;

    public FSRRStack(int size, Class<T> clazz) {
        maxSize = size;

        content = (T[]) Array.newInstance(clazz, size);

        currentSize = 0;
    }

    public FSRRStack(T[] t) {
        maxSize = t.length;

        content = t;

        currentSize = t.length;
    }

    public int size() {
        return currentSize;
    }

    public T push(T t) {
        int temp = (currentStart - 1 + maxSize) % maxSize;

        T rem = content[temp];

        content[temp] = t;

        if (currentSize < maxSize)
            ++currentSize;

        currentStart = temp;

        return rem;
    }

    public T get(int i) {
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
