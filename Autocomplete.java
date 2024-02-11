import java.util.Arrays;
import java.util.Comparator;

public class Autocomplete {
    private Term[] sortedTerms; // array with all terms sorted

    // Initializes the data structure from the given array of terms.
    public Autocomplete(Term[] terms) {
        if (terms == null)
            throw new IllegalArgumentException("the array of terms cannot be null");

        for (int i = 0; i < terms.length; i++) {
            if (terms[i] == null)
                throw new IllegalArgumentException("no entry can be null");
        }

        sortedTerms = terms.clone();
        Arrays.sort(sortedTerms);

    }

    // Returns terms that start with the given prefix, in descending order of weight.
    public Term[] allMatches(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("prefix cannot be null");

        Term term = new Term(prefix, 1);
        Comparator<Term> comparator = Term.byPrefixOrder(prefix.length());
        int first = BinarySearchDeluxe.firstIndexOf(sortedTerms, term, comparator);
        int last = BinarySearchDeluxe.lastIndexOf(sortedTerms, term, comparator);

        if (first == -1 || last == -1) {
            Term[] empty = new Term[0];
            return empty;
        }

        Term[] arr = new Term[last - first + 1];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = sortedTerms[first + i];
        }

        Arrays.sort(arr, Term.byReverseWeightOrder());
        return arr;

    }

    // Returns the number of terms that start with the given prefix.
    public int numberOfMatches(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("prefix cannot be null");

        Term[] copy = sortedTerms.clone();
        Term term = new Term(prefix, 1);
        Comparator<Term> comparator = Term.byPrefixOrder(prefix.length());
        int first = BinarySearchDeluxe.firstIndexOf(copy, term, comparator);
        int last = BinarySearchDeluxe.lastIndexOf(copy, term, comparator);

        if (first == -1 && last == -1)
            return 0;
        return last - first + 1;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        int n = in.readInt();
        Term[] terms = new Term[n];
        for (int i = 0; i < n; i++) {
            long weight = in.readLong();           // read the next weight
            in.readChar();                         // scan past the tab
            String query = in.readLine();          // read the next query
            terms[i] = new Term(query, weight);    // construct the term
        }

        // read in queries from standard input and print the top k matching terms
        int k = Integer.parseInt(args[1]);
        Autocomplete autocomplete = new Autocomplete(terms);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            Term[] results = autocomplete.allMatches(prefix);
            StdOut.printf("%d matches\n", autocomplete.numberOfMatches(prefix));
            for (int i = 0; i < Math.min(k, results.length); i++)
                StdOut.println(results[i]);
        }
    }

}
