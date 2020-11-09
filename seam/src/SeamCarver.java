import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {

    private int[][]colorMatrix;
    private int width, height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Input to constructor cannot be null.");
        width = picture.width();
        height = picture.height();
        colorMatrix = new int[width][height];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                colorMatrix[x][y] = picture.get(x, y).getRGB();
            }
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width, height);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                pic.set(x, y, new Color(colorMatrix[x][y]));
            }
        return pic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);

        // if pixel is on border, return 1000
        if (onBorder(x, y)) return 1000;
        double xGradientSq = xGradient(x, y);
        double yGradientSq = yGradient(x, y);
        double energy = Math.sqrt(xGradientSq + yGradientSq);

        return energy;
    }

    private boolean onBorder(int x, int y) {
        if (x == 0 || x == this.width() -1 || y == 0
                || y == this.height() - 1)                  return true;
        else                                                return false;
    }

    private double xGradient(int x, int y) {
        Color color1 = new Color(colorMatrix[x-1][y]);
        Color color2 = new Color(colorMatrix[x+1][y]);
        int r = Math.abs(color1.getRed()    -   color2.getRed());
        int g = Math.abs(color1.getGreen()  -   color2.getGreen());
        int b = Math.abs(color1.getBlue()   -   color2.getBlue());
        int xGradSquared = (r*r + g*g + b*b);
        return xGradSquared;
    }

    private double yGradient(int x, int y) {
        Color color1 = new Color(colorMatrix[x][y-1]);
        Color color2 = new Color(colorMatrix[x][y+1]);
        int r = Math.abs(color1.getRed()    -   color2.getRed());
        int g = Math.abs(color1.getGreen()  -   color2.getGreen());
        int b = Math.abs(color1.getBlue()   -   color2.getBlue());
        int yGradSquared = (r*r + g*g + b*b);
        return yGradSquared;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // construct energy matrix by H x W
        double[][] energyMatrix = toEnergyMatrix(height, width, true);
        return findSeam(energyMatrix);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // construct energy matrix by W x H
        double[][] energyMatrix = toEnergyMatrix(width, height, false);
        return findSeam(energyMatrix);
    }

    private int[] findSeam(double[][] eMatrix) {
        int W = eMatrix.length;
        int H = eMatrix[0].length;

        // construst energyTo matrix
        double[][] energyTo = new double[W][H];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) {
                if (y == 0) energyTo[x][y] = 195075.0;
                else energyTo[x][y] = Double.POSITIVE_INFINITY;
            }

        int[] seam = new int[H];
        int[][] edgeTo = new int[W][H];
        IndexMinPQ pq = new IndexMinPQ(W);

        // calculate energyTo by relax pixels
        for (int y = 0; y < H - 1; y++)
            for (int x = 0; x < W; x++)
                for (int k = x-1; k <= x+1; k++)
                    if (k >= 0 && k < W)
                        if (energyTo[k][y+1] > energyTo[x][y] + eMatrix[k][y+1]) {
                            energyTo[k][y+1] = energyTo[x][y] + eMatrix[k][y+1];
                            edgeTo[k][y+1] = xyTo1D(x, y, eMatrix);
                        }

        // find the minimum index in last row
        for (int x = 0; x < W; x++)
            pq.insert(x, energyTo[x][H-1]);
        seam[H-1] = pq.minIndex();

        // back-track
        for (int y = H-1; y > 0; y--)
            seam[y-1] = edgeTo[seam[y]][y] % W;
        return seam;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != width || height <= 1)
            throw new java.lang.IllegalArgumentException();
        //checkSeam(a);

        int[][] copy = new int[width][height-1];

        for (int x = 0; x < width; x++) {
            System.arraycopy(colorMatrix[x], 0, copy[x], 0, seam[x]);
            System.arraycopy(colorMatrix[x], seam[x]+1, copy[x], seam[x], height-seam[x]-1);
        }

        height--;
        colorMatrix = copy;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length != height || width <= 1)
            throw new java.lang.IllegalArgumentException();
        //checkSeam(a);

        int[][] copy = new int[width-1][height];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (x < seam[y]) copy[x][y] = colorMatrix[x][y];
                else if (x > seam[y]) copy[x-1][y] = colorMatrix[x][y];
            }
        }

        width--;
        colorMatrix = copy;

    }

    private double[][] toEnergyMatrix(int W, int H, boolean transposed) {
        double[][] result = new double[W][H];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) {
                if (transposed) result[x][y] = energy(y, x);
                else result[x][y] = energy(x, y);
            }

        return result;
    }

    private int xyTo1D(int x, int y, double[][] matrix) {
        return y * matrix.length + x;
    }

    private void validateRowIndex(int row) {
        if (row < 0 || row >= height())
            throw new IllegalArgumentException("row index must be between 0 and " + (height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (col < 0 || col >= width())
            throw new IllegalArgumentException("column index must be between 0 and " + (width() - 1) + ": " + col);
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

}