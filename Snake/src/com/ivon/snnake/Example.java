package com.ivon.snnake;

import com.ivon.snnake.vision1.Vision1Example;
import com.ivon.snnake.vision2.Vision2Example;
import com.ivon.snnake.vision3.Vision3Example;

/**
 * Created by Owner on 3/16/2016.
 */
public abstract class Example {

    public static final int VISION_1 = 0;
    public static final int VISION_2 = 1;
    public static final int VISION_3 = 2;
    public static final int NUM_TYPES = 3;

    protected int[] input;
    protected int[] output;

    public Example(int[] input, int[] output) {
        this.input = input;
        this.output = output;
    }

    public String toCSV() {
        String s = "";
        for (int i : input) {
            s += i + ",";
        }
        for (int i : output) {
            s += i + ",";
        }
        s = s.substring(0, s.length()-1);
        return s;
    }

    public static int[] generateInput(int visionType, int positions, int blockSize, int appleX, int appleY,
                                      int[] snakeX, int[] snakeY, int snakeLen,
                                      boolean up, boolean right, boolean down, boolean left) {

        switch (visionType) {
            case VISION_1:
                return Vision1Example.generateInput(positions, blockSize, appleX, appleY,
                        snakeX, snakeY, snakeLen, up, right, down, left);
            case VISION_2:
                return Vision2Example.generateInput(positions, blockSize, appleX, appleY,
                        snakeX, snakeY, snakeLen, up, right, down, left);
            case VISION_3:
                return Vision3Example.generateInput(positions, blockSize, appleX, appleY,
                        snakeX, snakeY, snakeLen, up, right, down, left);
            default:
                return new int[]{};
        }

    }

    public static Example newInstance(int visionType, int[] input, int[] output) {
        switch (visionType) {
            case VISION_1:
                return new Vision1Example(input, output);
            case VISION_2:
                return new Vision2Example(input, output);
            case VISION_3:
                return new Vision3Example(input, output);
            default:
                return null;
        }
    }

    public static String getSaveFileSuffix(int visionType) {
        switch (visionType) {
            case VISION_1:
                return "_vision1";
            case VISION_2:
                return "_vision2";
            case VISION_3:
                return "_vision3";
            default:
                return "";
        }
    }


}
