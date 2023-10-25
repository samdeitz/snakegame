import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


/*
 * Sam Deitz
 * Snake replica using java swing
 */

public class MainGame extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        new MainGame();
    }

    // enum for game state
    enum GS {
        PLAYING,
        ENDGAME
    }
    
    GS gamestate = GS.PLAYING;
    DrawingPanel dp = new DrawingPanel();

    // screen sizing
    final static int SCRW = 608, SCRH = 808;
    final static int SIZE = 8; // dimensions
    final static int CELL = SCRW/SIZE; // cell size

    int[][] board = new int[16][16]; // board array of numbers
    ArrayList<Segment> snake = new ArrayList<>(); // snake segment array with coordinates

    // values for items in array
    final static int START = 1; // snake head
    final static int SEG = 2; // middle segment
    final static int END = -1; // snake tail
    final static int APPLE = 3; // food/apple
    
    Point dir = new Point(0, 1); // direction of snake
    Segment apple = new Segment(0, 0);// apple location
    int length = 1; //length of snake

    //Color and font
    Color bgColor = new Color(65, 207, 21), secondGreen = new Color(30, 102, 8);
    Font f = new Font("Times New Roman", Font.BOLD, 40);

    
    Timer gameTimer = new Timer(1, this), // timer for game mechanics
    timer = new Timer(1000, new TL()), //timer for keeping track of time
    moveTimer = new Timer(350, new ML()); //timer for moving snake

    int time = 0;// time of playing
    boolean moved = false; // keep ttrack of when the snake has moved


    MainGame() {
        //setup 
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(dp);
        this.setResizable(false);
        this.addKeyListener(this);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        // add head segment, generate first apple
        snake.add(new Segment(3,3));
        generateApple();

        // start timers
        gameTimer.start();
        timer.start();
        moveTimer.start();
    }


    /**
     * Add snake segements to the board
     */
    void makeSnake() {

        // loop through each segment in the snake arraylist
        for(Segment segment : snake) {
            // if it's the head, set it to the START integer value
            if(snake.indexOf(segment) == 0) board[segment.x][segment.y] = START;

            // if it's the tail, set it to the END value
            else if(snake.indexOf(segment) == length-1) board[segment.x][segment.y] = END;
            
            // set it to normal body segment
            else board[segment.x][segment.y] = SEG;
        }
    }

    /**
     * Move snake segments on the board
     */
    void moveSnake() {
        // store previous moved segment
        Segment prev = snake.get(0);

        // loop through all segments to move
        for(int i = 0; i < snake.size(); i++) {
            Segment current = snake.get(i);// get current segment
            
            // if it is the head, move with the 'dir' point
            if(i == 0) {
                // if it will not move out of the array
                if(current.x - dir.x >= 0 && current.x - dir.x < SIZE
                && current.y - dir.y >= 0 && current.y -dir.y < SIZE) {
                    snake.set(i, new Segment(current.x - dir.x, current.y - dir.y));
                }
                
                // it moves out of the array(hits wall)
                else endGame();
            }

            // if it is not the head
            else {
                //put the current segment at the previous location of the last moved segment
                snake.set(i, prev);
            }
            // set the previous segment
            prev = current;
        }

        //the snake completed its movement
        moved = true;
    }   

    /**
     * get apple coordinates
     */
    void generateApple() {

        // pick until valid location
        while(true) {

            // choose coordinates
            int x = (int) (Math.random() * (SIZE-1));
            int y = (int) (Math.random() * (SIZE-1));

            // if there is nothing occupying the location
            if(board[x][y] == 0) {
                // set coordinates, end loop
                apple.x = x;
                apple.y = y;
                break;
            }
        }
        board[apple.x][apple.y] = APPLE; // add apple to board
    }

    /**
     * check if snake has hit food
     */
    void checkFood() {

        // get head and tail of snake
        Segment head = snake.get(0);
        Segment end = snake.get(length-1);

        // if head collides with apple
        if(head.equals(apple)) {
            // create new snake segment
            snake.add(new Segment(end.x, end.y));
            length++; // keep track of length
            generateApple(); // add new apple
        }
    }

    /**
     * check if snake collides with itself
     */
    void checkCollision() {
        // loop from 3rd snake segment to end
        for(int i = 2; i < snake.size(); i++) {
            Segment current = snake.get(i); // get current segment

            // if current is colliding with head
            if(snake.get(0).equals(current)) endGame();
        }
    }

    /**
     * clear the board completely
     */
    void clearBoard() {

        // loop through board array
        for(int i = 0; i < SIZE; i++) {
            for(int j =0; j < SIZE; j++) {
                // clear any items in board
                if(board[j][i] !=0 && board[j][i] != APPLE) board[j][i] = 0;
            }
        }
    }

    /**
     * set end screen
     */
    void endGame() {
        // stop timers
        gameTimer.stop();
        moveTimer.stop();
        timer.stop();

        // change gamestate to end 
        gamestate = GS.ENDGAME;
    }

    // drawing panel inner class
    class DrawingPanel extends JPanel {
        DrawingPanel() {
            // set screen size
            this.setPreferredSize(new Dimension(SCRW, SCRH));
        }

        /**
         * draws all visuals on screen
         * @param g graphics to draw to
         */
        @Override
        public void paintComponent(Graphics g) {

            // set backgrounds
            this.setBackground(bgColor);
            super.paintComponent(g);
            g.setFont(f);


            // if the game hasnt ended 
            if(gamestate == GS.PLAYING) {

                // set board color and draw board
                g.setColor(secondGreen);
                g.fillRect(0, 200, SCRW, SCRH-200);

                // loop through board array
                for(int i = 0; i < SIZE; i++) {
                    for(int j = 0; j < SIZE; j++) {
                        // make cell ovals to match background
                        g.setColor(bgColor);
                        g.fillOval(j*CELL+5, i*CELL+200+5, CELL-10, CELL-10);

                        // choose color based on segment type
                        if(board[j][i] == START) g.setColor(Color.BLACK);
                        if(board[j][i] == APPLE) g.setColor(Color.RED);
                        if(board[j][i] == SEG || board[j][i] == END) g.setColor(Color.blue);


                        // draw segment with color
                        g.fillOval(j*CELL+15, i*CELL+200+15, CELL-30, CELL-30);
                    }
                }

                // draw info menus
                g.setColor(secondGreen);
                g.fillRoundRect(25, 25, 558, 75, 20, 20);
                g.fillRoundRect(25, 115, 558, 75, 20, 20);

                // draw information on menu
                g.setColor(bgColor);
                g.drawString("Time:   " + time, 230, 75);
                g.drawString("Length:   " + length, 220, 165);

            }


            // end screen
            if(gamestate == GS.ENDGAME) {

                // menu
                g.setColor(secondGreen);
                g.fillRoundRect(100, 100, 400, 600, 30, 30);

                // end game information
                g.setColor(bgColor);
                g.drawString("YOU LOSE!", SCRW/4+30, SCRH/2-40);
                g.drawString("Time: " + time, SCRW/4+70, SCRH/2+10);
                g.drawString("Length: " + length, SCRW/4+50, SCRH/2+60);
            }
        }
    }

    /**
     * gametimer controlling game mechanics and checks
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        checkCollision();
        makeSnake();
        checkFood();
        repaint();
    }

    /**
     * Timer to keep track of playtime
     */
    class TL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            time++;
        }

    }

    /**
     * timer for moving snake
     */
    class ML implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearBoard();
            moveSnake();
        }
    }

    /**
     * check key movement
     * @param e key event when pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode(); // get key pressed

        // change direction accordingly
        if(key == KeyEvent.VK_W && moved && dir.y == 0) {
            dir.x = 0;
            dir.y = 1;
        }
        if(key == KeyEvent.VK_A && moved && dir.x == 0) {
            dir.x = 1;
            dir.y = 0;
        }
        if(key == KeyEvent.VK_S && moved && dir.y == 0) {
            dir.x = 0;
            dir.y = -1;
        }
        if(key == KeyEvent.VK_D && moved && dir.x == 0) {
            dir.x = -1;
            dir.y = 0;
        }
        moved = false;
    }

    //unused
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}

