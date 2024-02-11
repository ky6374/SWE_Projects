import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class BinarySearchDeluxe {

    // Returns the index of the first key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int firstIndexOf(Key[] a, Key key, Comparator<Key> c) {
        if (a == null || key == null || c == null)
            throw new IllegalArgumentException("no arguments can be null");
        int low = 0;
        int high = a.length - 1;
        int num = -1;

        while (low <= high) {
            int mid = high + low >>> 1;
            int compare = c.compare(key, a[mid]);

            if (compare > 0)
                low = mid + 1;
            else if (compare < 0)
                high = mid - 1;

            else {
                high = mid - 1;
                num = mid;
            }
        }
        return num;
    }

    // Returns the index of the last key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int lastIndexOf(Key[] a, Key key, Comparator<Key> c) {
        if (a == null || key == null || c == null)
            throw new IllegalArgumentException("no arguments can be null");
        int low = 0;
        int high = a.length - 1;
        int num = -1;

        while (low <= high) {
            int mid = high + low >>> 1;
            int compare = c.compare(key, a[mid]);

            if (compare > 0)
                low = mid + 1;
            else if (compare < 0)
                high = mid - 1;

            else {
                low = mid + 1;
                num = mid;
            }
        }
        return num;

    }

    // unit testing (required)
    public static void main(String[] args) {
        Term a = new Term("a", 1);
        Term b = new Term("b", 1);

        Term[] arr = { a, a, a, b, b, b };

        Comparator<Term> comparator = Term.byPrefixOrder(1);
        Term a1 = new Term("a", 1);
        Term b1 = new Term("b", 1);

        StdOut.println(BinarySearchDeluxe.firstIndexOf(arr, a1, comparator)); // 0
        StdOut.println(BinarySearchDeluxe.lastIndexOf(arr, a1, comparator)); // 1
        StdOut.println(BinarySearchDeluxe.firstIndexOf(arr, b1, comparator)); // 2
        StdOut.println(BinarySearchDeluxe.lastIndexOf(arr, b1, comparator)); // 4
    }
}
