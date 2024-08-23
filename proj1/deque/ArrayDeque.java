package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private final int INITIALSIZE = 8;

    public ArrayDeque() {
        items = (T[]) new Object[INITIALSIZE];
        size = 0;
        nextFirst = INITIALSIZE / 2;
        nextLast = INITIALSIZE / 2 + 1;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        if (capacity < items.length) {
            System.arraycopy(items, (nextFirst + 1) % items.length, newItems, 0, size);
        } else {
            if (nextFirst + 1 == nextLast) {
                System.arraycopy(items, nextLast, newItems, 0, size - nextLast);
                System.arraycopy(items, 0, newItems, size - nextLast, nextLast);
            } else {
                System.arraycopy(items, nextLast, newItems, 0, size);
            }
        }

        items = newItems;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int i = 0;
        while (i < size - 1) {
            System.out.print(items[i] + " ");
            i += 1;
        }

        System.out.println(items[i]);
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        nextFirst = (nextFirst + 1) % items.length;
        T rmItem = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;

        if ((size < items.length / 4) && (items.length > INITIALSIZE)) {
            resize(items.length / 2);
        }

        return rmItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        nextLast = (nextLast - 1 + items.length) % items.length;
        T rmItem = items[nextLast];
        items[nextLast] = null;
        size -= 1;

        if ((size < items.length / 4) && (items.length > INITIALSIZE)) {
            resize(items.length / 2);
        }

        return rmItem;
    }

    public T get(int index) {
        if (isEmpty() || index < 0 || index >= size) {
            return null;
        }

        return items[(nextFirst + index + 1) % items.length];
    }

    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {
        private int wizPos;
        private ArrayIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T item = get(wizPos);
            wizPos += 1;
            return item;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> O = (Deque<T>) o;
        if (O.size() != size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!O.get(i).equals(get(i))) {
                return false;
            }
        }

        return true;
    }

}
