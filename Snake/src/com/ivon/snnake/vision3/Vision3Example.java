package com.ivon.snnake.vision3;

import com.ivon.snnake.Example;
import com.ivon.snnake.Utils;

import java.util.Arrays;

/**
 * Created by Owner on 3/14/2016.
 */
public class Vision3Example extends Example {

    /**
     * INPUT:
     *
     * input[0]: distance forward until apple. 0 if apple is behind.
     * input[1]: distance right until apple. 0 if apple is left.
     * input[2]: distance backwards until apple. 0 if apple is forward.
     * input[3]: distance left until apple. 0 if apple is right.
     *
     * input[4]: distance up until wall
     * input[5]: distance right until wall
     * input[6]: distance down until wall
     * input[7]: distance left until wall
     *
     * input[8] : distance up until snake
     * input[9] : distance right until snake
     * input[10]: distance down until snake
     * input[11]: distance left until snake
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

    public Vision3Example(int[] input, int[] output) {
        super(input, output);
    }

    @Override
    public String toString() {

        String s = "Vision 3\n";

        s += Arrays.toString(input) + "\n";

        s += String.format("Action taken: [%d, %d, %d]%n",
                output[0],
                output[1],
                output[2]
        );

        return s;
    }

    /* Helper methods */

    private static int[] getRelativeDist(boolean up, boolean right, boolean down, boolean left, int[] dist) {
        if (up) {
            return dist;
        } else if (right) {
            return Utils.shift(dist, -1);
        } else if (down) {
            return Utils.shift(dist, 2);
        } else if (left) {
            return Utils.shift(dist, 1);
        }
        return dist;
    }

    public static int[] generateInput(int positions, int blockSize, int appleX, int appleY,
                                      int[] snakeX, int[] snakeY, int snakeLen,
                                      boolean up, boolean right, boolean down, boolean left) {

        int[] input = new int[12];

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

        // Distances to snake and walls
        int[] walls = new int[4];
        boolean upHitWall = false;
        boolean rightHitWall = false;
        boolean downHitWall = false;
        boolean leftHitWall = false;

        int[] snake = new int[4];
        boolean upHitSnake = false;
        boolean rightHitSnake = false;
        boolean downHitSnake = false;
        boolean leftHitSnake = false;

        for (int i=1; i<=positions; i++) {

            int testUp = headY - i;
            int testRight = headX + i;
            int testDown = headY + i;
            int testLeft = headX - i;

            // Detect world boundaries
            if (!upHitWall && (testUp < 0 || testUp >= positions)) {
                upHitWall = true;
                walls[0] = i-1;
            }
            if (!rightHitWall && (testRight < 0 || testRight >= positions)) {
                rightHitWall = true;
                walls[1] = i-1;
            }
            if (!downHitWall && (testDown < 0 || testDown >= positions)) {
                downHitWall = true;
                walls[2] = i-1;
            }
            if (!leftHitWall && (testLeft < 0 || testLeft >= positions)) {
                leftHitWall = true;
                walls[3] = i-1;
            }

            // Detect snake
            for (int z=0; z<snakeLen; z++) {
                int x = snakeX[z] / blockSize;
                int y = snakeY[z] / blockSize;
                if (!upHitSnake && (headX == x && testUp == y)) {
                    upHitSnake = true;
                    snake[0] = i-1;
                }
                if (!rightHitSnake && (testRight == x && headY == y)) {
                    rightHitSnake = true;
                    snake[1] = i-1;
                }
                if (!downHitSnake && (headX == x && testDown == y)) {
                    downHitSnake = true;
                    snake[2] = i-1;
                }
                if (!leftHitSnake && (testLeft == x && headY == y)) {
                    leftHitSnake = true;
                    snake[3] = i-1;
                }
            }

        }

        if (!upHitSnake) {
            snake[0] = positions;
        }
        if (!rightHitSnake) {
            snake[1] = positions;
        }
        if (!downHitSnake) {
            snake[2] = positions;
        }
        if (!leftHitSnake) {
            snake[3] = positions;
        }

        // Get values relative to direction
        apple = getRelativeDist(up, right, down, left, apple);
        walls = getRelativeDist(up, right, down, left, walls);
        snake = getRelativeDist(up, right, down, left, snake);

        for (int i=0; i<apple.length; i++) {
            input[i] = apple[i];
        }

        for (int i=0; i<walls.length; i++) {
            input[4+i] = walls[i];
        }

        for (int i=0; i<snake.length; i++) {
            input[8+i] = snake[i];
        }

        return input;

    }

}
