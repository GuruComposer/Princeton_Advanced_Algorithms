import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet theWordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.theWordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDistance = -1;
        String outcast = null;
        for (String s : nouns) {
            int distanceSum = 0;
            for (String t : nouns) {
                distanceSum += theWordNet.distance(s, t);
            }
            if (distanceSum > maxDistance) {
                maxDistance = distanceSum;
                outcast = s;
            }
        }
        return outcast;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}