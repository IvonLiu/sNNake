/**
 * Created by Owner on 3/14/2016.
 */
public class Example {

    public static final int APPLE = 1;
    public static final int OBSTACLE = -1;

    /**
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

        String s = String.format("Action taken: [%d, %d, %d]%n",
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
                    int index = Utils.getIndex(i, j);
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
