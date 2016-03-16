import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Snake extends JPanel implements ActionListener{

    public static final String SERVER_URL = "http://127.0.0.1:5000";
    public static final boolean IS_TRAINING = true;

    //variables for Window
    private final static int W_HEIGHT = 700;
    private final static int B_DIMEN = 600;

    //game setting
    private final static int DELAY = 150;
    private final static int BLOCKSIZE = 40;
    private final static int INITLOCIX = 5*BLOCKSIZE;
    private final static int INITLOCIY = 5*BLOCKSIZE;
    private final static int POSITIONS = B_DIMEN/BLOCKSIZE;

    // for Snake
    private static int x[] = new int[POSITIONS*POSITIONS];
    private static int y[] = new int[POSITIONS*POSITIONS];

    // for apple
    private static int appleX;
    private static int appleY;

    //in game stuff
    private static boolean gameCont = true;
    private static Integer score = 0;
    private static String sscore;
    private static int snakeLen = 3;
    private static boolean pause = false;
    private static boolean isWaitingForInput = true;

    // directions
    private static boolean up = false;
    private static boolean down = false;
    private static boolean left = false;
    private static boolean right = true;



    @Override
    protected void paintComponent(Graphics g){

        // refresh board
        super.paintComponent(g);
        setBackground(Color.BLACK);

        // set font
        Font font = new Font("Helvetica", Font.BOLD, 50);
        g.setFont(font);
        FontMetrics metr = getFontMetrics(font);

        //set message
        sscore = "SCORE: " + score.toString();


        if (gameCont){
            // draw border
            g.setColor(Color.GREEN);
            g.drawRect(0,0,B_DIMEN, B_DIMEN);

            // draw score
            g.drawString(sscore,  10, B_DIMEN + font.getSize());

            // draw apple
            g.setColor(Color.RED);
            g.fillRect(appleX, appleY, BLOCKSIZE, BLOCKSIZE);

            // draw snake
            for (int z = 0; z < snakeLen; z++) {
                if (z == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[z], y[z], BLOCKSIZE,BLOCKSIZE);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect( x[z], y[z], BLOCKSIZE,BLOCKSIZE);
                }
            }

        }else{

            // game over
            String message = "GAME OVER";
            g.setColor(Color.GREEN);
            g.drawString(message, (B_DIMEN - metr.stringWidth(message))/2, W_HEIGHT/2 - font.getSize());

            // score
            g.drawString(sscore, (B_DIMEN-metr.stringWidth(sscore)) /2, W_HEIGHT/2 );


        }


    }

    public Snake(JFrame window){
        window.addKeyListener(new ArrowAdapter());
        window.setSize(B_DIMEN + 20,W_HEIGHT);

    }

    public static void main(String args[]) throws InterruptedException{
        // initialize window
        JFrame window = new JFrame();
        window.setTitle("Snake3");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);


        // initialize game
        makeSnake();
        spawnApple();
        Snake snake = new Snake(window);
        window.add(snake);


        while(gameCont){
            move();
            checkCollisions();
            snake.repaint();
            if (pause){
                while(pause){
                    Thread.sleep(DELAY);
                }
            }

            // Normal game mode
            //Thread.sleep(DELAY);

            // AI game mode
            if (IS_TRAINING) {
                isWaitingForInput = true;
                while(gameCont && isWaitingForInput) {
                    Thread.sleep(DELAY);
                }
            } else {
                int action = getNextAction();
                Thread.sleep(DELAY);
                simulateKeyPress(action);
            }

        }

        // display end game
        snake.repaint();
    }

    private static int[][] testX = new int[][] {
            {21, 27},
            {31, 0},
            {14, 16},
            {16, 14},
            {3, 6},
            {9, 9}
    };

    private static int getNextAction() {

        try {

            int[][] input = new int[][] {
                    Utils.generateInput(POSITIONS, BLOCKSIZE, appleX, appleY, x, y, snakeLen, up, right, down, left)
            };

            HttpResponse<JsonNode> jsonResponse = Unirest.post(SERVER_URL + "/forward")
                    .header("content-type", "application/json")
                    .body("{\"X\": " + Utils.toJsonString(input) + "}")
                    .asJson();

            double[] yHat = Utils.toMatrix(jsonResponse.getBody().getObject().getJSONArray("yHat"))[0];
            System.out.println("\nyHat: " + Arrays.toString(yHat));

            int maxIndex = -1;
            double max = 0;

            for (int i=0; i<yHat.length; i++) {
                if (yHat[i] > max) {
                    max = yHat[i];
                    maxIndex = i;
                }
            }

            switch (maxIndex) {
                case 0:
                    System.out.println("Action: up");
                    return KeyEvent.VK_UP;
                case 1:
                    System.out.println("Action: right");
                    return KeyEvent.VK_RIGHT;
                case 2:
                    System.out.println("Action: down");
                    return KeyEvent.VK_DOWN;
                case 3:
                    System.out.println("Action: left");
                    return KeyEvent.VK_LEFT;
                case 4:
                default:
                    System.out.println("Action: continue");
                    return KeyEvent.VK_SPACE;
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return KeyEvent.VK_SPACE;
    }

    private static void simulateKeyPress(int action) {
        try {
            Robot robot = new Robot();
            robot.keyPress(action);
            robot.keyRelease(action);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void makeSnake() {
        // TODO Auto-generated method stub
        for (int z = 0; z < snakeLen; z++) {
            x[z] = INITLOCIX - z * BLOCKSIZE;
            y[z] = INITLOCIY;
        }
    }

    private static void spawnApple(){

        if (snakeLen >= POSITIONS * POSITIONS) {
            return;
        }

        int r = (int) (Math.random() * (POSITIONS ));
        appleX = ((r * BLOCKSIZE));

        r = (int) (Math.random() * (POSITIONS ));
        appleY = ((r * BLOCKSIZE));

        boolean collide = false;
        for (int z=0; z<snakeLen; z++) {
            if (x[z] == appleX && y[z] == appleY) {
                collide = true;
                break;
            }
        }
        if (collide) {
            spawnApple();
        }
    }


    private static void move(){
        for (int z = snakeLen; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (left) {
            x[0] -= BLOCKSIZE;
        }

        if (right) {
            x[0] += BLOCKSIZE;
        }

        if (up) {
            y[0] -= BLOCKSIZE;
        }

        if (down) {
            y[0] += BLOCKSIZE;
        }
    }

    private static void checkCollisions(){
        // if snake hits itself
        for (int z = snakeLen; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                gameCont = false;
            }
        }
        // check if snake hits wall
        if (y[0] >= B_DIMEN) {
            gameCont = false;
        }

        if (y[0] < 0) {
            gameCont = false;
        }

        if (x[0] >= B_DIMEN) {
            gameCont = false;
        }

        if (x[0] < 0) {
            gameCont = false;
        }
        // if snake hits apple
        if ((x[0] == appleX) && (y[0] == appleY)) {
            snakeLen++;
            score++;
            spawnApple();
        }
    }

    private class ArrowAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT && !right)
                    || (key == KeyEvent.VK_RIGHT && !left)
                    || (key == KeyEvent.VK_UP && !down)
                    || (key == KeyEvent.VK_DOWN && !up)
                    || key == KeyEvent.VK_SPACE) {

                if (IS_TRAINING) {
                    Utils.addExample(POSITIONS, BLOCKSIZE, appleX, appleY, x, y, snakeLen, up, right, down, left, key);
                }
                isWaitingForInput = false;

            }

            if (key == KeyEvent.VK_ENTER) {
                System.out.println(Arrays.toString(x));
                System.out.println(Arrays.toString(y));
            }

            if ((key == KeyEvent.VK_LEFT) && (!right)) {
                left = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!left)) {
                right = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_UP) && (!down)) {
                up = true;
                right = false;
                left = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!up)) {
                down = true;
                right = false;
                left = false;
            }

            if (key == KeyEvent.VK_P){
                if (pause){
                    pause = false;
                }else{
                    pause = true;
                }
            }

            if (key == KeyEvent.VK_E) {
                if (IS_TRAINING) {
                    Utils.exportExamples();
                }
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }
}
