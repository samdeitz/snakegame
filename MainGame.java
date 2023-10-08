import java.awt.Color;
import java.awt.Dimension;
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
    Color bgColor = new Color(65, 207, 21);
    int[][] board = new int[16][16];
    Timer gameTimer = new Timer(1, this), timer = new Timer(1000, new TL()), moveTimer = new Timer(350, new ML());
    int time = 0;
    ArrayList<Segment> snake = new ArrayList<>();
    boolean moved = false;

    MainGame() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(dp);
        this.setResizable(false);
        this.addKeyListener(this);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        snake.add(new Segment(2,7));
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
                else System.exit(0);
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
                apple.x = x;
                apple.y = y;
                break;
            }
        }
        board[apple.x][apple.y] = APPLE;
    }

    void eatFood() {
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
                System.exit(0);
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

    class DrawingPanel extends JPanel {
        DrawingPanel() {
            this.setPreferredSize(new Dimension(SCRW, SCRH));

        }

        @Override
        public void paintComponent(Graphics g) {
            this.setBackground(bgColor);
            super.paintComponent(g);
            g.setColor(new Color(30, 102, 8));
            g.fillRect(0, 200, SCRW, SCRH-200);
            for(int i = 0; i < SIZE; i++) {
                for(int j = 0; j < SIZE; j++) {
                    g.setColor(bgColor);
                    g.fillOval(j*CELL+2, i*CELL+200+2, CELL-4, CELL-4);

                    if(board[j][i] == START) g.setColor(Color.orange);
                    if(board[j][i] == SEG) g.setColor(Color.blue);
                    if(board[j][i] == END) g.setColor(Color.black);
                    if(board[j][i] == APPLE) g.setColor(Color.RED);
                    g.fillOval(j*CELL+10, i*CELL+200+10, CELL-20, CELL-20);
                }
            }
            g.setColor(Color.BLACK);
            g.drawString(time + "", 100, 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eatFood();
        checkCollision();
        makeSnake();
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
        //FIXME CAN GO BACKWARD FIX 
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
    public void keyReleased(KeyEvent e) {
    }
}

