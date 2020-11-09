import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Argument to constructor cannot be null.");
        digraph = new Digraph(G);
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        BreadthFirstDirectedPaths bfsDigraphA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDigraphB = new BreadthFirstDirectedPaths(digraph, w);
        int distance;
        int minDistance = Integer.MAX_VALUE;
        boolean minFound = false;

        for (int x = 0; x < digraph.V(); x++) {
            if (bfsDigraphA.hasPathTo(x) && bfsDigraphB.hasPathTo(x)) {
                distance = bfsDigraphA.distTo(x) + bfsDigraphB.distTo(x);
                if (distance < minDistance) {
                    minDistance = distance;
                    minFound = true;
                }
            }
        }
        return minFound ? minDistance : -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        BreadthFirstDirectedPaths bfsDigraphA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDigraphB = new BreadthFirstDirectedPaths(digraph, w);
        int distance;
        int minDistance = Integer.MAX_VALUE;
        int closestAncestor = -1;

        for (int x = 0; x < digraph.V(); x++) {
            if (bfsDigraphA.hasPathTo(x) && bfsDigraphB.hasPathTo(x)) {
                distance = bfsDigraphA.distTo(x) + bfsDigraphB.distTo(x);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestAncestor = x;
                }
            }
        }
        return closestAncestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Arguments to length(Iterable<Integer>, Iterable<Integer> cannot be null.");

        BreadthFirstDirectedPaths bfsDigraphA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDigraphB = new BreadthFirstDirectedPaths(digraph, w);
        int distance;
        int minDistance = Integer.MAX_VALUE;
        boolean minFound = false;

        for (int x = 0; x < digraph.V(); x++) {
            if (bfsDigraphA.hasPathTo(x) && bfsDigraphB.hasPathTo(x)) {
                distance = bfsDigraphA.distTo(x) + bfsDigraphB.distTo(x);
                if (distance < minDistance) {
                    minDistance = distance;
                    minFound = true;
                }
            }
        }
        return minFound ? minDistance : -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Arguments to length(Iterable<Integer>, Iterable<Integer> cannot be null.");

        BreadthFirstDirectedPaths bfsDigraphA = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDigraphB = new BreadthFirstDirectedPaths(digraph, w);
        int distance;
        int minDistance = Integer.MAX_VALUE;
        int closestAncestor = -1;

        for (int x = 0; x < digraph.V(); x++) {
            if (bfsDigraphA.hasPathTo(x) && bfsDigraphB.hasPathTo(x)) {
                distance = bfsDigraphA.distTo(x) + bfsDigraphB.distTo(x);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestAncestor = x;
                }
            }
        }
        return closestAncestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}