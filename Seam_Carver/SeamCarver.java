public class SeamCarver {

    private Picture pic; // current picture

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Picture argument cannot be null");

        pic = new Picture(picture);
    }

    // computes gradient squared value
    private double gradientSquared(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = (rgb2) & 0xFF;

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
    }

    // calculate the energy of a pixel
    private double getEnergy(int row, int col) {
        if (row < 0 || row >= pic.height() || col < 0 || col >= pic.width()) {
            throw new IllegalArgumentException("Invalid row or column index");
        }

        int left = col - 1;
        int right = col + 1;

        if (width() == 1) {
            left = 0;
            right = 0;
        }
        else if (col == 0)
            left = width() - 1;
        else if (col == pic.width() - 1)
            right = 0;

        int rgbLeft = pic.getRGB(left, row);
        int rgbRight = pic.getRGB(right, row);

        double xGradientSquared = gradientSquared(rgbLeft, rgbRight);

        int top = row - 1;
        int bot = row + 1;

        if (height() == 1) {
            top = 0;
            bot = 0;
        }
        else if (row == 0)
            top = height() - 1;
        else if (row == pic.height() - 1)
            bot = 0;

        int rgbTop = pic.getRGB(col, top);
        int rgbBot = pic.getRGB(col, bot);

        double yGradientSquared = gradientSquared(rgbTop, rgbBot);

        return Math.sqrt(xGradientSquared + yGradientSquared);
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (y < 0 || y >= height() || x < 0 || x >= width())
            throw new IllegalArgumentException("Invalid row or column index");
        return getEnergy(y, x);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] hSeam = findVerticalSeam();
        transpose();
        return hSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] distTo = new double[height()][width()];
        int[][] edgeTo = new int[height()][width()];

        // initialization of top row
        // all edgeTo values for top row are -1
        for (int col = 0; col < width(); col++) {
            distTo[0][col] = energy(col, 0);
            edgeTo[0][col] = -1;
        }

        // get shortest path to each pixel
        for (int row = 1; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                double minD = Double.POSITIVE_INFINITY;
                double energy = energy(col, row);
                int minIndex = -1;

                if (distTo[row - 1][col] < minD) {
                    minD = distTo[row - 1][col];
                    minIndex = col;
                }
                if (col != width() - 1 && minD > distTo[row - 1][col + 1]) {
                    minD = distTo[row - 1][col + 1];
                    minIndex = col + 1;
                }
                if (col != 0 && minD > distTo[row - 1][col - 1]) {
                    minD = distTo[row - 1][col - 1];
                    minIndex = col - 1;
                }
                edgeTo[row][col] = minIndex;
                distTo[row][col] = minD + energy;
            }
        }
        // find the minimum cumulative energy in the lowest row
        int minCol = 0;

        for (int col = 1; col < width(); col++) {
            if (distTo[height() - 1][col] < distTo[height() - 1][minCol])
                minCol = col;
        }

        // trace back path
        int[] vSeam = new int[height()];
        vSeam[height() - 1] = minCol;
        for (int row = height() - 2; row >= 0; row--) {
            vSeam[row] = edgeTo[row + 1][vSeam[row + 1]];
        }
        return vSeam;
    }

    // checks if a vertical seam is valid
    private boolean isValidVertical(int[] seam) {
        int prev = seam[0];
        for (int i = 0; i < seam.length; i++) {
            int num = Math.abs(seam[i] - prev);
            if (seam[i] > width() - 1 || seam[i] < 0 || num > 1)
                return false;
            prev = seam[i];
        }
        return true;
    }

    // checks if a horizontal seam is valid
    private boolean isValidHorizontal(int[] seam) {
        int prev = seam[0];
        for (int i = 0; i < seam.length; i++) {
            int num = Math.abs(seam[i] - prev);
            if (seam[i] > height() - 1 || seam[i] < 0 || num > 1)
                return false;
            prev = seam[i];
        }
        return true;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("null input");
        if (height() == 1)
            throw new IllegalArgumentException("Height is 1");
        if (seam.length != width() || !isValidHorizontal(seam))
            throw new IllegalArgumentException("Seam isn't valid");
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("null input");
        if (width() == 1)
            throw new IllegalArgumentException("Width is 1");
        if (seam.length != height() || !isValidVertical(seam))
            throw new IllegalArgumentException("Seam isn't valid");

        Picture tempPic = new Picture(width() - 1, height());

        // Copy pixels
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width() - 1; col++) {
                if (col < seam[row])
                    tempPic.setRGB(col, row, pic.getRGB(col, row));
                else
                    tempPic.setRGB(col, row, pic.getRGB(col + 1, row));

            }
        }

        pic = tempPic;
    }

    // switch rows to columns and vise versa
    private void transpose() {
        Picture tempPic = new Picture(height(), width());

        // Copy pixels
        for (int row = 0; row < tempPic.height(); row++) {
            for (int col = 0; col < tempPic.width(); col++) {
                tempPic.setRGB(col, row, pic.getRGB(row, col));
            }
        }
        pic = tempPic;
    }

    //  unit testing (required)
    public static void main(String[] args) {
        Picture pic = new Picture("10x10.png");
        SeamCarver carver = new SeamCarver(pic);

        for (int i = 0; i < carver.height(); i++) {
            for (int j = 0; j < carver.width(); j++) {
                StdOut.println(carver.energy(j, i));
            }
        }
        int[] arr1 = carver.findVerticalSeam();
        // 8 7 7 7 7 8 9 8 7 7
        for (int val : arr1)
            StdOut.print(val + " ");
        StdOut.println();
        // w=10, h=10
        StdOut.println("w=" + carver.width() + ", h=" + carver.height());
        carver.removeVerticalSeam(arr1);
        // w=9, h=10
        StdOut.println("After removing vSeam, w=" + carver.width() +
                               ", h=" + carver.height());

        StdOut.println();

        int[] arr2 = carver.findHorizontalSeam();
        // 2 1 2 3 3 3 4 3 4
        for (int val : arr2)
            StdOut.print(val + " ");
        carver.removeHorizontalSeam(arr2);
        StdOut.println();
        // w=9, h=9
        StdOut.println("After removing hSeam, w=" + carver.width() +
                               ", h=" + carver.height());

        // save new 9x9 image to folder
        carver.picture().save("9x9.png");


        // keep W constant
        for (int height = 125; height <= 16000; height *= 2) {
            Picture p = SCUtility.randomPicture(2000, height);
            SeamCarver sc = new SeamCarver(p);
            Stopwatch sw = new Stopwatch();
            sc.removeHorizontalSeam(sc.findHorizontalSeam());
            sc.removeVerticalSeam(sc.findVerticalSeam());
            StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        }

        // keep H constant
        for (int width = 125; width <= 16000; width *= 2) {
            Picture p = SCUtility.randomPicture(width, 2000);
            SeamCarver sc = new SeamCarver(p);
            Stopwatch sw = new Stopwatch();
            sc.removeHorizontalSeam(sc.findHorizontalSeam());
            sc.removeVerticalSeam(sc.findVerticalSeam());
            StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        }
    }
}
