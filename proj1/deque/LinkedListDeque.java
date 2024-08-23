package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private final Node sentinel;
    private int size;

    private class Node {
        private Node prev;
        private T item;
        private Node next;

        private Node(T i, Node n) {
            item = i;
            next = n;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node(null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        newNode.prev = sentinel;
        size += 1;
    }

    public void addLast(T item) {
        Node newNode = new Node(item, sentinel);
        sentinel.prev.next = newNode;
        newNode.prev = sentinel.prev;
        sentinel.prev = newNode;
        size += 1;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while (p.next != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }

        System.out.println(p.item);
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        Node rmNode = sentinel.next;
        T rmItem = rmNode.item;
        sentinel.next = rmNode.next;
        rmNode.next.prev = sentinel;
        rmNode.prev = null;
        rmNode.item = null;
        rmNode.next = null;
        size -= 1;
        return rmItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        Node rmNode = sentinel.prev;
        T rmItem = rmNode.item;
        sentinel.prev = rmNode.prev;
        rmNode.prev.next = sentinel;
        rmNode.prev = null;
        rmNode.item = null;
        rmNode.next = null;
        size -= 1;
        return rmItem;
    }

    public T get(int index) {
        if (isEmpty() || index < 0 || index >= size) {
            return null;
        }

        Node p;
        for (p = sentinel.next; index != 0; p = p.next) {
            index -= 1;
        }

        return p.item;
    }

    public T getRecursive(int index) {
        if (isEmpty() || index < 0 || index >= size) {
            return null;
        }

        Node p = sentinel.next;
        return getRecursiveHelper(p, index);
    }

    public T getRecursiveHelper(Node p, int index) {
        if (index == 0) {
            return p.item;
        }

        return getRecursiveHelper(p.next, index - 1);
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private int wizPos;
        public LinkedListIterator() {
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
            if (O.get(i) != get(i)) {
                return false;
            }
        }

        return true;
    }

}
