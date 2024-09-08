package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private Node root;

    private class Node {
        K key;
        V value;
        Node left;
        Node right;

        public Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    public BSTMap() {
        size = 0;
        root = null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    private boolean containsKey(K key, Node node) {
        if (node == null) {
            return false;
        } else if (node.key.equals(key)) {
            return true;
        } else if (node.key.compareTo(key) < 0) {
            return containsKey(key, node.right);
        } else {
            return containsKey(key, node.left);
        }
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    private V get(K key, Node node) {
        if (node == null) {
            return null;
        } else if (node.key.equals(key)) {
            return node.value;
        } else if (node.key.compareTo(key) < 0) {
            return get(key, node.right);
        } else {
            return get(key, node.left);
        }
    }

    @Override
    public int size() { return size; }

    @Override
    public void put(K key, V value) {
        root = put(key, value, root);
    }

    private Node put(K key, V value, Node node) {
        if (node == null) {
            size += 1;
            return new Node(key, value);
        } else {
            if (node.key.equals(key)) {
                node.value = value;
            } else if (node.key.compareTo(key) < 0) {
                node.right = put(key, value, node.right);
            } else {
                node.left = put(key, value, node.left);
            }
        }
        return node;
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) {
            return;
        } else {
            printInOrder(node.left);
            System.out.println(node);
            printInOrder(node.right);
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet(root);
    }

    private Set<K> keySet(Node node) {
        Set<K> set = new TreeSet<>();
        keySet(node, set);
        return set;
    }

    private void keySet(Node node, Set<K> set) {
        if (node == null) {
            return;
        } else {
            keySet(node.left, set);
            set.add(node.key);
            keySet(node.right, set);
        }
    }

    @Override
    public V remove(K key) {
        Node parent = null;
        Node current = root;
        while (current != null) {
            if (current.key.equals(key)) {
                V result = current.value;
                remove(current, parent);
                size -= 1;
                return result;
            } else if (current.key.compareTo(key) < 0) {
                parent = current;
                current = current.right;
            } else {
                parent = current;
                current = current.left;
            }
        }

        return null;
    }

    @Override
    public V remove(K key, V value) {
        Node parent = null;
        Node current = root;
        while (current != null) {
            if (current.key.equals(key)) {
                if (current.value.equals(value)) {
                    V result = current.value;
                    remove(current, parent);
                    size -= 1;
                    return result;
                } else {
                    break;
                }
            } else if (current.key.compareTo(key) < 0) {
                parent = current;
                current = current.right;
            } else {
                parent = current;
                current = current.left;
            }
        }

        return null;
    }

    private void remove(Node node, Node parent) {
        if (node.left == null && node.right == null) {
            if (node == root) {
                root = null;
            } else {
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            }
        } else if (node.right == null) {
            if (node == root) {
                root = node.left;
            } else {
                if (parent.left == node) {
                    parent.left = node.left;
                } else {
                    parent.right = node.left;
                }
            }
        } else if (node.left == null) {
            if (node == root) {
                root = node.right;
            } else {
                if (parent.left == node) {
                    parent.left = node.right;
                } else {
                    parent.right = node.right;
                }
            }
        } else {
            Node par = node;
            Node cur = node.right;
            while (cur.left != null) {
                par = cur;
                cur = cur.left;
            }

            node.key = cur.key;
            node.value = cur.value;
            remove(cur, par);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIter();
    }

    private class BSTMapIter implements Iterator<K> {
        private Stack<Node> stack;

        public BSTMapIter() {
            stack = new Stack<>();
            Node current = root;
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = stack.pop();
            K result = node.key;
            Node current = node.right;
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            return result;
        }
    }

}
