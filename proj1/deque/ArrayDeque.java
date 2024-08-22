package deque;

public class ArrayDeque<T> {
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

    public boolean isEmpty() {
        return size == 0;
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
}
