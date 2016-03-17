import org.json.JSONArray;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Owner on 3/14/2016.
 */
public class Utils {

    private static final String SAVE_FILE_PREFIX = "training_";

    private static List<Example> examples = new ArrayList<>();

    /**
     * Map the i,j th element of the
     * detection matrix to the nth
     * element of the input vector.
     */
    public static int getIndex(int i, int j) {
        int index = i * 21 + j;
        if (i<10 || (i==10 && j<10)) {
            return index;
        } else if (i>10 || (i==10 && j>10)) {
            return index - 1;
        } else {
            return -1;
        }
    }

    public static int[][] rotate90CW(int[][] m) {
        int len = m.length;
        int[][] r = new int[len][len];
        for (int i=0; i<len; i++) {
            for (int j=0; j<len; j++) {
                r[j][len-i-1] = m[i][j];
            }
        }
        return r;
    }

    public static int[][] rotate90CCW(int[][] m) {
        int len = m.length;
        int[][] r = new int[len][len];
        for (int i=0; i<len; i++) {
            for (int j=0; j<len; j++) {
                r[len-j-1][i] = m[i][j];
            }
        }
        return r;
    }

    public static int[][] rotate180(int[][] m) {
        int len = m.length;
        int[][] r = new int[len][len];
        for (int i=0; i<len; i++) {
            for (int j=0; j<len; j++) {
                r[len-i-1][len-j-1] = m[i][j];
            }
        }
        return r;
    }

    public static int[] generateInput(int positions, int blockSize, int appleX, int appleY,
                                      int[] snakeX, int[] snakeY, int snakeLen,
                                      boolean up, boolean right, boolean down, boolean left) {

        int[][] matrix = new int[21][21];

        int cX = snakeX[0]/blockSize;
        int cY = snakeY[0]/blockSize;
        int xMin = cX - 10;
        int xMax = cX + 10;
        int yMin = cY - 10;
        int yMax = cY + 10;

        for (int y=yMin; y<=yMax; y++) {
            for (int x=xMin; x<=xMax; x++) {

                int i = y - yMin;
                int j = x - xMin;

                if (x==cX && y==cY) {
                    continue;
                }

                if (x<0 || x>=positions || y<0 || y>= positions) {
                    // You are out of the board
                    matrix[i][j] = Example.OBSTACLE;
                }

                for (int z = 0; z < snakeLen; z++) {
                    if (x == snakeX[z] / blockSize && y == snakeY[z] / blockSize) {
                        matrix[i][j] = Example.OBSTACLE;
                    }
                }

                if (x == appleX / blockSize && y == appleY / blockSize) {
                    matrix[i][j] = Example.APPLE;
                }

            }
        }

        // Rotate it so it is relative
        // to snake's orientation
        if (right) {
            matrix = rotate90CCW(matrix);
        } else if (down) {
            matrix = rotate180(matrix);
        } else if (left) {
            matrix = rotate90CW(matrix);
        }

        // Map it to single vector
        int[] input = new int[21*21-1];
        for (int i=0; i<21; i++) {
            for (int j=0; j<21; j++) {
                if (!(i == 10 && j == 10)) {
                    input[getIndex(i, j)] = matrix[i][j];
                }
            }
        }

        return input;

    }
    public static int[] generateOutput(boolean up, boolean right, boolean down, boolean left, int action) {

        if (up) {
            switch (action) {
                case KeyEvent.VK_LEFT:
                    return new int[] {1, 0, 0};
                case KeyEvent.VK_UP:
                    return new int[] {0, 1, 0};
                case KeyEvent.VK_RIGHT:
                    return new int[] {0, 0, 1};
            }
        } else if (right) {
            switch (action) {
                case KeyEvent.VK_UP:
                    return new int[] {1, 0, 0};
                case KeyEvent.VK_RIGHT:
                    return new int[] {0, 1, 0};
                case KeyEvent.VK_DOWN:
                    return new int[] {0, 0, 1};
            }
        } else if (down) {
            switch (action) {
                case KeyEvent.VK_RIGHT:
                    return new int[] {1, 0, 0};
                case KeyEvent.VK_DOWN:
                    return new int[] {0, 1, 0};
                case KeyEvent.VK_LEFT:
                    return new int[] {0, 0, 1};
            }
        } else if (left) {
            switch (action) {
                case KeyEvent.VK_DOWN:
                    return new int[] {1, 0, 0};
                case KeyEvent.VK_LEFT:
                    return new int[] {0, 1, 0};
                case KeyEvent.VK_UP:
                    return new int[] {0, 0, 1};
            }
        }

        return new int[] {0, 1, 0};

    }

    public static void addExample(int positions, int blockSize, int appleX, int appleY,
                                  int[] snakeX, int[] snakeY, int snakeLen,
                                  boolean up, boolean right, boolean down, boolean left,
                                  int action) {

        int[] input = generateInput(positions, blockSize, appleX, appleY,
                snakeX, snakeY, snakeLen, up, right, down, left);
        int[] output = generateOutput(up, right, down, left, action);

        Example e = new Example(input, output);
        System.out.println(e);
        examples.add(e);

    }

    public static void exportExamples() {

        System.out.println("Exporting examples");

        Path workingDir = Paths.get("");
        Path parentDir = workingDir.toAbsolutePath().getParent();
        Path trainingDir = parentDir.resolve("TrainingData");
        Path outfilePath = trainingDir.resolve(SAVE_FILE_PREFIX + System.currentTimeMillis() + ".csv");

        List<String> lines = examples.stream().map(Example::toCSV).collect(Collectors.toList());

        try {
            File outfile = outfilePath.toFile();
            System.out.println(outfilePath.toString());
            outfile.createNewFile();
            Files.write(outfilePath, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String toJsonString(int[][] x) {
        return new JSONArray(Arrays.asList(x)).toString();
    }

    public static double[][] toMatrix(JSONArray y) {
        double[][] matrix = new double[y.length()][y.getJSONArray(0).length()];
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[i].length; j++) {
                matrix[i][j] = y.getJSONArray(i).getDouble(j);
            }
        }
        return matrix;
    }

    public static String matrixToString(double[][] matrix) {

        int numRows = matrix.length;
        int numCols = matrix[0].length;

        String s = "";
        for (int i=0; i<numRows; i++) {
            String row = "|";
            for (int j=0; j<numCols; j++) {
                row += String.format("%10.2f", matrix[i][j]);
                if (j != numCols-1) {
                    row += " ";
                }
            }
            row += "|";
            if (i != numRows-1) {
                row += "\n";
            }
            s += row;
        }
        return s + "\n";
    }

}
