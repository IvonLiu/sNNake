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

    public static int[] shift(int[] array, int shift) {
        int len = array.length;
        int[] shifted = new int[len];
        for (int i=0; i<len; i++) {
            int j = (len + i + shift) % len;
            shifted[j] = array[i];
        }
        return shifted;
    }

    public static int[] getRelativeAppleDist(boolean up, boolean right, boolean down, boolean left, int[] apple) {
        if (up) {
            return apple;
        } else if (right) {
            return shift(apple, -1);
        } else if (down) {
            return shift(apple, 2);
        } else if (left) {
            return shift(apple, 1);
        }
        return apple;
    }

    public static int[] getRelativeObstacleDist(boolean up, boolean right, boolean down, boolean left, int[] obstacles) {

        int[] shifted;
        if (up) {
            shifted = obstacles;
        } else if (right) {
            shifted = shift(obstacles, -1);
        } else if (down) {
            shifted = shift(obstacles, 2);
        } else if (left) {
            shifted = shift(obstacles, 1);
        } else {
            shifted = obstacles;
        }

        int[] relative = new int[3];
        relative[0] = shifted[3];
        relative[1] = shifted[0];   // Skip over shifted[2] since that is
        relative[2] = shifted[1];   // always the square behind the snake

        return relative;
    }

    public static int[] generateInput(int positions, int blockSize, int appleX, int appleY,
                                      int[] snakeX, int[] snakeY, int snakeLen,
                                      boolean up, boolean right, boolean down, boolean left) {

        int[] input = new int[7];

        int headX = snakeX[0] / blockSize;
        int headY = snakeY[0] / blockSize;
        appleX /= blockSize;
        appleY /= blockSize;

        // Distances to apple
        int[] apple = new int[4];
        if (appleY < headY) {
            apple[0] = headY - appleY;
            apple[2] = 0;
        } else {
            apple[0] = 0;
            apple[2] = appleY - headY;
        }
        if (appleX < headX) {
            apple[1] = 0;
            apple[3] = headX - appleX;
        } else {
            apple[1] = appleX - headX;
            apple[3] = 0;
        }

        // Distances to obstacles
        int[] obstacles = new int[4];
        boolean upHit = false;
        boolean rightHit = false;
        boolean downHit=  false;
        boolean leftHit = false;

        for (int i=1; i<=positions; i++) {

            int testUp = headY - i;
            int testRight = headX + i;
            int testDown = headY + i;
            int testLeft = headX - i;

            // Detect world boundaries
            if (!upHit && (testUp < 0 || testUp >= positions)) {
                upHit = true;
                obstacles[0] = i-1;
            }
            if (!rightHit && (testRight < 0 || testRight >= positions)) {
                rightHit = true;
                obstacles[1] = i-1;
            }
            if (!downHit && (testDown < 0 || testDown >= positions)) {
                downHit = true;
                obstacles[2] = i-1;
            }
            if (!leftHit && (testLeft < 0 || testLeft >= positions)) {
                leftHit = true;
                obstacles[3] = i-1;
            }

            // Detect snake
            for (int z=0; z<snakeLen; z++) {
                int x = snakeX[z] / blockSize;
                int y = snakeY[z] / blockSize;
                if (!upHit && (headX == x && testUp == y)) {
                    upHit = true;
                    obstacles[0] = i-1;
                }
                if (!rightHit && (testRight == x && headY == y)) {
                    rightHit = true;
                    obstacles[1] = i-1;
                }
                if (!downHit && (headX == x && testDown == y)) {
                    downHit = true;
                    obstacles[2] = i-1;
                }
                if (!leftHit && (testLeft == x && headY == y)) {
                    leftHit = true;
                    obstacles[3] = i-1;
                }
            }

        }

        // Get values relative to direction
        apple = getRelativeAppleDist(up, right, down, left, apple);
        obstacles = getRelativeObstacleDist(up, right, down, left, obstacles);

        for (int i=0; i<apple.length; i++) {
            input[i] = apple[i];
        }

        for (int i=0; i<obstacles.length; i++) {
            input[apple.length+i] = obstacles[i];
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
