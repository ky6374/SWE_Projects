import java.util.Arrays;
import java.util.Comparator;

public class Term implements Comparable<Term> {

    private String query; // query
    private long weight; // weight

    // Initializes a term with the given query string and weight.
    public Term(String query, long weight) {
        if (query == null || weight < 0)
            throw new IllegalArgumentException("query cannot be null");
        this.query = query;
        this.weight = weight;
    }

    private static class WeightComparator implements Comparator<Term> {
        public int compare(Term term1, Term term2) {
            return Long.compare(term2.weight, term1.weight);
        }
    }

    // Compares the two terms in descending order by weight.
    public static Comparator<Term> byReverseWeightOrder() {
        return new WeightComparator();
    }

    private static class LexComparator implements Comparator<Term> {
        private int r; // first r letters

        // constructor for nested class
        public LexComparator(int r) {
            if (r < 0) throw new IllegalArgumentException("r must be non-negative");
            this.r = r;
        }

        // compare the first r letters of term1 and term2, letter by letter
        public int compare(Term term1, Term term2) {
            String first = term1.query;
            String second = term2.query;
            int num = 0;
            for (int i = 0; i < r; i++) {
                if (i == first.length() || i == second.length())
                    return first.compareTo(second);
                num = Character.compare(first.charAt(i), second.charAt(i));
                if (num != 0)
                    return num;
            }
            return num;
        }
    }

    // Compares the two terms in lexicographic order,
    // but using only the first r characters of each query.
    public static Comparator<Term> byPrefixOrder(int r) {
        return new LexComparator(r);
    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(Term that) {
        return this.query.compareTo(that.query);
    }

    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString() {
        return (weight + "\t" + query);
    }

    // unit testing (required)
    public static void main(String[] args) {
        Term term = new Term("aaa", 3);
        Term term1 = new Term("bbb", 2);
        Term term2 = new Term("abcz", 5);
        Term term3 = new Term("abczganja", 5);
        StdOut.println(term.toString()); // 3   aaa
        StdOut.println(term.compareTo(term1)); // -1

        Term[] arr = { term, term1, term2, term3 };

        Arrays.sort(arr);
        StdOut.println("Sorted by default");
        for (Term word : arr) {
            StdOut.println(word);
        }

        Arrays.sort(arr, byPrefixOrder(7));
        StdOut.println("Sorted by first 7 letters");
        for (Term word : arr) {
            StdOut.println(word);
        }

        Arrays.sort(arr, byReverseWeightOrder());
        StdOut.println("Sorted by weight");
        for (Term word : arr) {
            StdOut.println(word);
        }

        LexComparator comp = new LexComparator(8);
        StdOut.println(comp.compare(term2, term3)); // -5
        StdOut.println(comp.compare(term3, term2)); // 5
    }

}
