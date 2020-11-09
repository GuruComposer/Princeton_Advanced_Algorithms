import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

import java.util.HashMap;

public class WordNet {
    // the digraph
    private final Digraph wordNet;
    // HashMap to store synsets
    private final HashMap<Integer, String> synsetsHashMap;
    // HashMap to store nouns
    private final HashMap<String, Bag<Integer>> nounsHashMap;

    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("Arguments to WordNet constructor cannot be null.");
        synsetsHashMap = new HashMap<>();
        nounsHashMap = new HashMap<>();

        // read Synsets
        int count = this.readSynsets(synsets);
        wordNet = new Digraph(count);

        // read Hypernyms
        this.readHypernyms(hypernyms);

        // Check that the input to the constructor is a rooted DAG.
        checkRootedDAG();

        sap = new SAP(wordNet);
    }

    private int readSynsets(String synsets) {
        if (synsets == null) throw new IllegalArgumentException("Argument to readSynsets(String) cannot be null.");
        In in = new In(synsets);
        int count = 0;
        while (in.hasNextLine()) {
            count++;
            String line = in.readLine();
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            synsetsHashMap.put(id, parts[1]);
            String[] nouns = parts[1].split(" ");
            for (String n : nouns) {
                if (nounsHashMap.get(n) != null) {
                    Bag<Integer> bag = nounsHashMap.get(n);
                    bag.add(id);
                }
                else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(id);
                    nounsHashMap.put(n, bag);
                }
            }
        }
        return count;
    }

    private void readHypernyms(String hypernyms) {
        if (hypernyms == null) throw new IllegalArgumentException("Argument to hypernyms(String) cannot be null.");
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int hypernymID = Integer.parseInt(parts[i]);
                wordNet.addEdge(id, hypernymID);
            }
        }
    }

    private void checkRootedDAG() {
        // Make sure graph is a DAG
        Topological topSort = new Topological(wordNet);
        if (!topSort.hasOrder()) {
            throw new IllegalArgumentException("Cycle detected");
        }

        // Check that the input to the constructor is a rooted DAG.
        Integer root = null;
        int rootCount = 0;
        for (int v = 0; v < wordNet.V(); v++) {
            if (wordNet.outdegree(v) == 0) {
                root = v;
                rootCount++;
            }
        }
        if (root == null)           { throw new IllegalArgumentException("NO ROOT DETECTED."); }
        else if (rootCount != 1)    { throw new IllegalArgumentException("Digraph must contain 1 root. DIGRAPH NOT ROOTED."); }

        // Ensure reachability from all other vertices to R

        BreadthFirstDirectedPaths bfdPaths = new BreadthFirstDirectedPaths(wordNet.reverse(), root);
        for (int v = 0; v < wordNet.V(); v++) {
            if (!bfdPaths.hasPathTo(v)) {
                throw new IllegalArgumentException("Not all vertices lead back to the root.");
            }
        }
        // StdOut.println("VERIFIED: ROOTED DAG");
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsHashMap.keySet();
    }

    // is the word a WordNet noun?
    // should run in time logarithmic or better in the number of nouns.
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Argument to isNoun(String) method cannot be null.");
        return nounsHashMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    // Should run in time linear in the size of the WordNet digraph.
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Arguments to distance(String, String) method cannot be null.");

        // Check if nounA and nounB are WordNet nouns.

        int distance = sap.length(nounsHashMap.get(nounA), nounsHashMap.get(nounB));
        return distance;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    // Should run in time linear in the size of the WordNet digraph.
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Arguments to sap(String, String) method cannot be null.");

        // Check if nounA and nounB are WordNet nouns.

        int closestID = sap.ancestor(nounsHashMap.get(nounA), nounsHashMap.get(nounB));
        String closestAncestorNoun = synsetsHashMap.get(closestID);
        return closestAncestorNoun;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("tests/synsets.txt", "tests/hypernyms.txt");
        StdOut.println("wordNet has: " + wordNet.wordNet.V() + " vertices.");
        StdOut.println("wordNet has: " + wordNet.wordNet.E() + " edges.");
    }
}