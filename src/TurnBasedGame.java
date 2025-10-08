import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TurnBasedGame extends JPanel implements KeyListener {

    private Image playerImg, enemyImg;
    private boolean playerTurn = true;
    private int playerHP = 100, enemyHP = 100;

    public TurnBasedGame() {
        // โหลดรูป
        playerImg = new ImageIcon("player.png").getImage();
        enemyImg = new ImageIcon("enemy.png").getImage();

        JFrame frame = new JFrame("Turn-Based Demo");
        frame.add(this);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(this);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // วาดฉาก
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0,getWidth(),getHeight());

        // วาด Player
        g.drawImage(playerImg, 100, 300, 150, 150, this);
        g.drawString("HP: " + playerHP, 100, 280);

        // วาด Enemy
        g.drawImage(enemyImg, 500, 100, 150, 150, this);
        g.drawString("HP: " + enemyHP, 500, 80);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (playerTurn && enemyHP > 0 && playerHP > 0) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                // Player โจมตี
                enemyHP -= 10;
                playerTurn = false;
                repaint();

                // Enemy ตอบโต้ (ดีเลย์นิดหน่อย)
                new Timer(1000, (evt) -> {
                    playerHP -= 5;
                    playerTurn = true;
                    repaint();
                }).start();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new TurnBasedGame();
    }
}