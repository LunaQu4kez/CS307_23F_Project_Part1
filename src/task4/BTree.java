package task4;

// The implementation is refer to Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne.
public class BTree<Key extends Comparable<Key>, Value>  {
    private static final int M = 6;
    private Node root;
    private int height;
    private int size;

    private static final class Node {
        private int m;
        private Entry[] children;

        private Node(int k) {
            m = k;
            children = new Entry[M];
        }
    }

    private static class Entry {
        private Comparable key;
        private Object val;
        private Node next;

        public Entry(Comparable key, Object val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    public BTree() {
        root = new Node(0);
    }

    public Value get(Key key) {
        return search(root, key, height);
    }

    private Value search(Node x, Key key, int ht) {
        Entry[] children = x.children;
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (equal(key, children[j].key)) return (Value) children[j].val;
            }
        } else {
            for (int j = 0; j < x.m; j++) {
                if (j + 1 == x.m || less(key, children[j + 1].key))
                    return search(children[j].next, key, ht - 1);
            }
        }
        return null;
    }

    public void put(Key key, Value val) {
        Node u = insert(root, key, val, height);
        size++;
        if (u == null) return;

        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
    }

    public int size() {
        return size;
    }

    private Node insert(Node h, Key key, Value val, int ht) {
        int j;
        Entry t = new Entry(key, val, null);

        if (ht == 0) {
            for (j = 0; j < h.m; j++) if (less(key, h.children[j].key)) break;
        } else {
            for (j = 0; j < h.m; j++) {
                if (j + 1 == h.m || less(key, h.children[j + 1].key)) {
                    Node u = insert(h.children[j++].next, key, val, ht - 1);
                    if (u == null) return null;
                    t.key = u.children[0].key;
                    t.val = null;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.m; i > j; i--) h.children[i] = h.children[i - 1];
        h.children[j] = t;
        h.m++;
        if (h.m < M) return null;
        else return split(h);
    }

    private Node split(Node h) {
        Node t = new Node(M / 2);
        h.m = M / 2;
        for (int j = 0; j < M / 2; j++) t.children[j] = h.children[M / 2 + j];
        return t;
    }

    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean equal(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }
}


