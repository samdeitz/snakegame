import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainGame extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new MainGame();
    }

    DrawingPanel dp = new DrawingPanel();
    final static int SCRW = 608, SCRH = 808;
    final static int CELL = 38;
    final static int SIZE = 16;
    Color bgColor = new Color(65, 207, 21);
    int[][] board = new int[16][16];
    Timer gameTimer = new Timer(1, this), timer = new Timer(1000, new TL());
    int time = 0;

    MainGame() {
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.add(dp);
        this.setBackground(bgColor);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        gameTimer.start();
        timer.start();
    }

    class DrawingPanel extends JPanel {
        DrawingPanel() {
            this.setPreferredSize(new Dimension(SCRW, SCRH));
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(new Color(30, 102, 8));
            g.fillRect(0, 200, SCRW, SCRH-200);
            for(int i = 0; i < SIZE; i++) {
                for(int j = 0; j < SIZE; j++) {
                    g.setColor(bgColor);
                    g.fillOval(j*CELL+5, i*CELL+200+5, CELL-5, CELL-5);
                }
            }
            g.setColor(Color.BLACK);
            g.drawString(time + "", 100, 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    class TL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            time++;
        }

    }

}

