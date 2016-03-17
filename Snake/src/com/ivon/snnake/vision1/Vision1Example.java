package com.ivon.snnake.vision1;

import com.ivon.snnake.Example;
import com.ivon.snnake.Utils;

/**
 * Created by Owner on 3/14/2016.
 */
public class Vision1Example extends Example {

    public static final int APPLE = 1;
    public static final int OBSTACLE = -1;

    /**
     * INPUT:
     *
     * Describes a 21x21 detection area centered around the snake's head.
     * Contains 21*21-1=440 elements (center square is always snake
     * head, so it can be ignored).
     *
     * Elements are ordered row-major, from left to right then top to bottom.
     * The orientation of the detection area is relative to the snake's head,
     * not the game board. Thus, if the snake is facing right, element 0 will
     * be the top left relative to the snake, which is top right relative to
     * the board.
     *
     * Example: assuming the snake is facing right, this is the numbering
     *
     *          419 398 ... 021 000
     *          420 399 ... 022 001
     *           .   .  .    .   .
     *           .   . --H-> .   .
     *           .   .    .  .   .
     *          438 417 ... 040 019
     *          439 418 ... 041 020
     *
     */

    /**
     * OUTPUT:
     *
     * Represents the action taken, relative to the snake:
     * [left, straight, right]
     *
     * 1 means the action was taken, otherwise 0.
     *
     * Example: assuming the snake is facing right,
     *          [ 1, 0, 0 ] will turn the snake to face up
     *          [ 0, 1, 0 ] will continue facing right
     *          [ 0, 0, 1 ] will turn the snake to face down
     *
     * In training sets, if there is not exactly one element
     * with the value 1 and the rest with values 0, then you
     * screwed up. In testing sets, the three values will
     * be probabilities and may be any non-negative real value.
     * The action to be taken will be the maximum of the three.
     */

    public Vision1Example(int[] input, int[] output) {
        super(input, output);
    }

    @Override
    public String toString() {

        String s = "Vision 1\n";

        s += String.format("Action taken: [%d, %d, %d]%n",
                output[0],
                output[1],
                output[2]
        );

        for (int i=0; i<21; i++) {
            String row = "";
            for (int j=0; j<21; j++) {
                if (i == 10 && j == 10) {
                    row += "H ";
                } else {
                    int index = getIndex(i, j);
                    if (input[index] == APPLE) {
                        row += "O ";
                    } else if (input[index] == OBSTACLE) {
                        row += "X ";
                    } else {
                        row += ". ";
                    }
                }
            }
            s += row + "\n";
        }

        return s;
    }

    /* Helper methods */

    /**
     * Map the i,j th element of the
     * detection matrix to the nth
     * element of the input vector.
     */
    private static int getIndex(int i, int j) {
        int index = i * 21 + j;
        if (i<10 || (i==10 && j<10)) {
            return index;
        } else if (i>10 || (i==10 && j>10)) {
            return index - 1;
        } else {
            return -1;
        }
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
                    matrix[i][j] = Vision1Example.OBSTACLE;
                }

                for (int z = 0; z < snakeLen; z++) {
                    if (x == snakeX[z] / blockSize && y == snakeY[z] / blockSize) {
                        matrix[i][j] = Vision1Example.OBSTACLE;
                    }
                }

                if (x == appleX / blockSize && y == appleY / blockSize) {
                    matrix[i][j] = Vision1Example.APPLE;
                }

            }
        }

        // Rotate it so it is relative
        // to snake's orientation
        if (right) {
            matrix = Utils.rotate90CCW(matrix);
        } else if (down) {
            matrix = Utils.rotate180(matrix);
        } else if (left) {
            matrix = Utils.rotate90CW(matrix);
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

}
