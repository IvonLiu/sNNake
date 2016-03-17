package com.ivon.snnake.vision2;

import com.ivon.snnake.Example;
import com.ivon.snnake.Utils;

import java.util.Arrays;

/**
 * Created by Owner on 3/16/2016.
 */
public class Vision2Example extends Example {

    /**
     * INPUT:
     *
     * input[0]: distance forward until apple. 0 if apple is behind.
     * input[1]: distance right until apple. 0 if apple is left.
     * input[2]: distance backwards until apple. 0 if apple is forward.
     * input[3]: distance left until apple. 0 if apple is right.
     *
     * input[4]: distance left until obstacle
     * input[5]: distance forward until obstacle
     * input[7]: distance right until obstacle
     * Where obstacle = wall or snake
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

    public Vision2Example(int[] input, int[] output) {
        super(input, output);
    }

    @Override
    public String toString() {

        String s = "Vision 2\n";

        s += Arrays.toString(input) + "\n";

        s += String.format("Action taken: [%d, %d, %d]%n",
                output[0],
                output[1],
                output[2]
        );

        return s;
    }

    /* Helper methods */

    private static int[] getRelativeAppleDist(boolean up, boolean right, boolean down, boolean left, int[] apple) {
        if (up) {
            return apple;
        } else if (right) {
            return Utils.shift(apple, -1);
        } else if (down) {
            return Utils.shift(apple, 2);
        } else if (left) {
            return Utils.shift(apple, 1);
        }
        return apple;
    }

    private static int[] getRelativeObstacleDist(boolean up, boolean right, boolean down, boolean left, int[] obstacles) {

        int[] shifted;
        if (up) {
            shifted = obstacles;
        } else if (right) {
            shifted = Utils.shift(obstacles, -1);
        } else if (down) {
            shifted = Utils.shift(obstacles, 2);
        } else if (left) {
            shifted = Utils.shift(obstacles, 1);
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
}
