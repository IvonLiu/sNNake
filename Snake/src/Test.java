/**
 * Created by Owner on 3/16/2016.
 */
public class Test {

    public static void main(String[] args) {
        int[][] matrix = new int[][] {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        int[][] rotated = Utils.rotate90CW(matrix);
        System.out.println(matrixToString(rotated));
    }

    private static String matrixToString(int[][] matrix) {

        String s = "";

        for (int i=0; i<matrix.length; i++) {
            String row = "";
            for (int j=0; j<matrix[i].length; j++) {
                row += matrix[i][j] + " ";
            }
            s += row + "\n";
        }

        return s;
    }
}
