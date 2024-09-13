package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;
    private final int DEFAULTSIZE = 16;
    private int initSize = DEFAULTSIZE;
    private double loadFactor = 0.75;
    private HashSet<K> keySet;

    /** Constructors */
    public MyHashMap() {
        size = 0;
        buckets = createTable(initSize);
        keySet = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        size = 0;
        initSize = initialSize;
        buckets = createTable(initSize);
        keySet = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        initSize = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(initSize);
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }

        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        size = 0;
        initSize = DEFAULTSIZE;
        buckets = createTable(initSize);
        keySet = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        int index = Math.abs(key.hashCode() % initSize);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = Math.abs(key.hashCode() % initSize);
        if (containsKey(key)) {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    node.value = value;
                }
            }
        } else {
            Node newNode = createNode(key, value);
            buckets[index].add(newNode);
            size += 1;
            keySet.add(key);
        }

        if ((double) size / initSize >= loadFactor) {
            resize();
        }
    }

    private void resize() {
        initSize *= 2;
        Collection<Node>[] newTable= createTable(initSize);
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int index = Math.abs(node.key.hashCode() % initSize);
                newTable[index].add(node);
            }
        }

        buckets = newTable;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }

        V result = null;
        int index = Math.abs(key.hashCode() % initSize);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                result = node.value;
                buckets[index].remove(node);
                keySet.remove(node.key);
            }
        }

        return result;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }

        V result = null;
        int index = Math.abs(key.hashCode() % initSize);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                if (node.value.equals(value)) {
                    result = node.value;
                    buckets[index].remove(node);
                    keySet.remove(node.key);
                } else {
                    return null;
                }
            }
        }

        return result;
    }

    @Override
    public Iterator<K> iterator() {
        return new HSMapIter();
    }

    private class HSMapIter implements Iterator<K> {
        private Iterator<K> iter;

        public HSMapIter() {
            iter = keySet.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public K next() {
            return iter.next();
        }
    }

}
