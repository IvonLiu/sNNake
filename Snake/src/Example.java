import java.util.Arrays;

/**
 * Created by Owner on 3/14/2016.
 */
public class Example {

    public static final int APPLE = 10;
    public static final int OBSTACLE = -1;

    /**
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
    private int[] input;

    /**
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
    private int[] output;

    public Example(int[] input, int[] output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public String toString() {

        String s = Arrays.toString(input) + "\n";

        s += String.format("Action taken: [%d, %d, %d]%n",
                output[0],
                output[1],
                output[2]
        );

        return s;
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

}
