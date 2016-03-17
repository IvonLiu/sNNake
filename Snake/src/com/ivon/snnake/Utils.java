package com.ivon.snnake;

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

    private static List<List<Example>> examples = new ArrayList<>(Example.NUM_TYPES);
    static {
        for (int i=0; i<Example.NUM_TYPES; i++) {
            examples.add(new ArrayList<>());
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
        for (int i=0; i<Example.NUM_TYPES; i++) {
            int[] input = Example.generateInput(i, positions, blockSize, appleX, appleY,
                    snakeX, snakeY, snakeLen, up, right, down, left);
            int[] output = generateOutput(up, right, down, left, action);

            Example e = Example.newInstance(i, input, output);
            System.out.println(e);
            examples.get(i).add(e);
        }

    }

    public static void exportExamples() {

        System.out.println("Exporting examples");

        Path workingDir = Paths.get("");
        Path parentDir = workingDir.toAbsolutePath().getParent();
        Path trainingDir = parentDir.resolve("TrainingData");
        long now = System.currentTimeMillis();

        for (int i=0; i<Example.NUM_TYPES; i++) {
            Path outfilePath = trainingDir.resolve(SAVE_FILE_PREFIX + now + Example.getSaveFileSuffix(i) + ".csv");

            List<String> lines = examples.get(i).stream().map(Example::toCSV).collect(Collectors.toList());

            try {
                File outfile = outfilePath.toFile();
                System.out.println(outfilePath.toString());
                outfile.createNewFile();
                Files.write(outfilePath, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
