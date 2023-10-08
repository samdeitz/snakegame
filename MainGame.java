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

public class MainGame extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        new MainGame();
    }

    enum GS {
        PLAYING,
        ENDGAME
    }
    
    GS gamestate = GS.PLAYING;
    DrawingPanel dp = new DrawingPanel();
    final static int SCRW = 608, SCRH = 808;
    final static int SIZE = 8;
    final static int CELL = SCRW/SIZE;
    final static int START = 1;
    final static int SEG = 2;
    final static int END = -1;
    final static int APPLE = 3;
    
    Point dir = new Point(0, 1);
    Segment apple = new Segment(0, 0);
    int length = 1;
    Color bgColor = new Color(65, 207, 21), secondGreen = new Color(30, 102, 8);
    int[][] board = new int[16][16];
    Timer gameTimer = new Timer(1, this), timer = new Timer(1000, new TL()), moveTimer = new Timer(350, new ML());
    int time = 0;
    ArrayList<Segment> snake = new ArrayList<>();
    boolean moved = false;
    Font f = new Font("Times New Roman", Font.BOLD, 40);


    MainGame() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(dp);
        this.setResizable(false);
        this.addKeyListener(this);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        snake.add(new Segment(3,3));
        generateApple();
        gameTimer.start();
        timer.start();
        moveTimer.start();
    }

    void makeSnake() {
        for(Segment segment : snake) {
            if(snake.indexOf(segment) == 0) board[segment.x][segment.y] = START;
            else if(snake.indexOf(segment) == length-1) board[segment.x][segment.y] = END;
            else board[segment.x][segment.y] = SEG;
        }
    }

    void moveSnake() {
        Segment prev = snake.get(0);
        for(int i = 0; i < snake.size(); i++) {
            Segment current = snake.get(i);            
            if(i == 0) {
                if(current.x - dir.x >= 0 && current.x - dir.x < SIZE
                && current.y - dir.y >= 0 && current.y -dir.y < SIZE) {
                    snake.set(i, new Segment(current.x - dir.x, current.y - dir.y));
                }
                else endGame();
            }
            else {
                snake.set(i, prev);
            }
            prev = current;
        }
        moved = true;
    }

    void generateApple() {
        while(true) {
            int x = (int) (Math.random() * (SIZE-1));
            int y = (int) (Math.random() * (SIZE-1));
            if(board[x][y] == 0) {
                System.out.printf("%d %d%n", x, y);
                apple.x = x;
                apple.y = y;
                break;
            }
        }
        board[apple.x][apple.y] = APPLE;
    }

    void checkFood() {
        Segment head = snake.get(0);
        Segment end = snake.get(length-1);
        if(head.equals(apple)) {
            snake.add(new Segment(end.x, end.y));
            length++;
            generateApple();
        }
    }

    void checkCollision() {
        for(int i = 2; i < snake.size(); i++) {
            Segment check = snake.get(i);
            if(snake.get(0).equals(check)) {
                endGame();
            }
        }
    }

    void clearBoard() {
        for(int i = 0; i < SIZE; i++) {
            for(int j =0; j < SIZE; j++) {
                if(board[j][i] !=0 && board[j][i] != APPLE) board[j][i] = 0;
            }
        }
    }

    void endGame() {
        gameTimer.stop();
        moveTimer.stop();
        timer.stop();
        gamestate = GS.ENDGAME;
    }

    class DrawingPanel extends JPanel {
        DrawingPanel() {
            this.setPreferredSize(new Dimension(SCRW, SCRH));
        }

        @Override
        public void paintComponent(Graphics g) {
            this.setBackground(bgColor);
            super.paintComponent(g);
            g.setFont(f);

            if(gamestate == GS.PLAYING) {
                g.setColor(secondGreen);
                g.fillRect(0, 200, SCRW, SCRH-200);
                for(int i = 0; i < SIZE; i++) {
                    for(int j = 0; j < SIZE; j++) {
                        g.setColor(bgColor);
                        g.fillOval(j*CELL+5, i*CELL+200+5, CELL-10, CELL-10);

                        if(board[j][i] == START) g.setColor(Color.orange);
                        if(board[j][i] == SEG) g.setColor(Color.blue);
                        if(board[j][i] == END) g.setColor(Color.black);
                        if(board[j][i] == APPLE) g.setColor(Color.RED);
                        g.fillOval(j*CELL+15, i*CELL+200+15, CELL-30, CELL-30);
                    }
                }
                g.setColor(secondGreen);
                g.fillRoundRect(25, 25, 558, 75, 20, 20);
                g.fillRoundRect(25, 115, 558, 75, 20, 20);

                g.setColor(bgColor);
                g.drawString("Time:   " + time, 230, 75);
                g.drawString("Length:   " + length, 220, 165);

            }

            if(gamestate == GS.ENDGAME) {
                g.setColor(secondGreen);
                g.fillRoundRect(100, 100, 400, 600, 30, 30);

                g.setColor(bgColor);
                g.drawString("YOU LOSE!", SCRW/4+30, SCRH/2-40);
                g.drawString("Time: " + time, SCRW/4+70, SCRH/2+10);
                g.drawString("Length: " + length, SCRW/4+50, SCRH/2+60);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkCollision();
        makeSnake();
        checkFood();
        repaint();
    }

    class TL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            time++;
        }

    }

    class ML implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            clearBoard();
            moveSnake();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
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
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}

