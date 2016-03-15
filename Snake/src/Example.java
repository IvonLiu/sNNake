/**
 * Created by Owner on 3/14/2016.
 */
public class Example {
    /**
     * First position*position*2 elements represent [isSnake, isApple]
     * where 1 represents true, 0 represents false.
     *
     * Ex. [0, 0] is empty, [1, 0] is snake, [0, 1] is apple.
     *      [1, 1] is impossible and you screwed up.
     *
     * Last 4 positions represent direction, [up, right, down, left],
     * where 1 represents the snake is heading in that direction.
     *
     * Ex. [0, 1, 0, 0] means the snake is heading right.
     *     If there is not exactly one "1", then again,
     *     you did something wrong.
     */
    private int[] input;

    /**
     * [up, right, down, left, straight]
     *
     * Ex. [1, 0, 0, 0, 0] means user hit up arrow,
     *      [0, 0, 0, 0, 1] means user let snake continue forward,
     *      [0, 0, 0, 0, 0] means you are bad and screwed up.
     *
     * If there is more than one "1" then either you are amazing
     * and hit more than one arrow key at once, or you screwed up.
     */
    private int[] output;

    public Example(int[] input, int[] output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public String toString() {

        String s = String.format("Direction: [%d, %d, %d, %d]%n",
                input[input.length-4],
                input[input.length-3],
                input[input.length-2],
                input[input.length-1]
        );

        s += String.format("Action taken: [%d, %d, %d, %d, %d]%n",
                output[0],
                output[1],
                output[2],
                output[3],
                output[4]
        );

        int positions = (int) Math.pow(input.length / 2, 0.5);
        for (int y=0; y<positions; y++) {

            String rowStr = "";

            for (int x=0; x<positions; x++) {

                int isSnakeIndex = y*positions*2 + x*2;
                int isAppleIndex = isSnakeIndex + 1;

                if (input[isSnakeIndex] == 1) {
                    rowStr += "S ";
                } else if (input[isAppleIndex] == 1) {
                    rowStr += "A ";
                } else {
                    rowStr += ". ";
                }

            }

            s += rowStr + "\n";

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
