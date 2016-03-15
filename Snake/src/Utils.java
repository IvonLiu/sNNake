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

    public static void addExample(int positions, int blockSize, int appleX, int appleY,
                                  int[] snakeX, int[] snakeY, int snakeLen,
                                  boolean up, boolean right, boolean down, boolean left,
                                  int action) {

        int[] input = new int[positions*positions*2 + 4];
        int[] output;

        for (int y=0; y<positions; y++) {
            for (int x=0; x<positions; x++) {

                int isSnakeIndex = y*positions*2 + x*2;
                int isAppleIndex = isSnakeIndex + 1;

                for (int z=0; z<snakeLen; z++) {
                    if (x == snakeX[z]/blockSize && y == snakeY[z]/blockSize) {
                        input[isSnakeIndex] = 1;
                    }
                }

                if (x == appleX/blockSize && y == appleY/blockSize) {
                    input[isAppleIndex] = 1;
                }

            }
        }

        // Snake direction
        input[input.length-4] = up ? 1 : 0;
        input[input.length-3] = right ? 1 : 0;
        input[input.length-2] = down ? 1 : 0;
        input[input.length-1] = left ? 1 : 0;

        if (action == KeyEvent.VK_UP) {
            if (up) {
                output = new int[]{0, 0, 0, 0, 1};
            } else {
                output = new int[]{1, 0, 0, 0, 0};
            }
        } else if (action == KeyEvent.VK_RIGHT) {
            if (right) {
                output = new int[]{0, 0, 0, 0, 1};
            } else {
                output = new int[]{0, 1, 0, 0, 0};
            }
        } else if (action == KeyEvent.VK_DOWN) {
            if (down) {
                output = new int[]{0, 0, 0, 0, 1};
            } else {
                output = new int[]{0, 0, 1, 0, 0};
            }
        } else if (action == KeyEvent.VK_LEFT) {
            if (left) {
                output = new int[]{0, 0, 0, 0, 1};
            } else {
                output = new int[]{0, 0, 0, 1, 0};
            }
        } else if (action == KeyEvent.VK_SPACE) {
            output = new int[] {0, 0, 0, 0, 1};
        } else {
            output = new int[] {0, 0, 0, 0, 0};
        }

        Example e = new Example(input, output);
        System.out.println(e);
        examples.add(e);

    }

    public static void exportExamples() {

        System.out.println("Exporting examples");

        Path workingDir = Paths.get("");
        Path parentDir = workingDir.toAbsolutePath().getParent();
        Path trainingDir = parentDir.resolve("TrainingData");
        Path outfilePath = trainingDir.resolve(SAVE_FILE_PREFIX+System.currentTimeMillis()+".csv");

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
