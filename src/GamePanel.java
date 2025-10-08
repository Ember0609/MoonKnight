import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
    // --- ตัวแปรใหม่ ---
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; // เส้นด้ายที่จะรัน Game Loop
    Knight knight = new Knight(); // สร้าง Knight ของเรา

    // --- ตัวแปรเดิม ---
    Image backgroundImage;
    Image knightImage;
    Image slimeImage;

    public GamePanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // ช่วยให้การวาดภาพลื่นขึ้น
        this.addKeyListener(keyH); // เพิ่ม KeyHandler เข้าไปใน Panel
        this.setFocusable(true); // ทำให้ Panel สามารถรับการกดปุ่มได้

        // โหลดรูปภาพ
        backgroundImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();
        knightImage = new ImageIcon(getClass().getResource("Picture/MainFront.png")).getImage();
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage();
    }

    // --- Method ใหม่: เริ่ม Game Loop ---
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // --- Method ใหม่: Game Loop! ---
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / 60; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update(); // 1. อัปเดตข้อมูล (ตำแหน่งตัวละคร)
                repaint(); // 2. วาดหน้าจอใหม่
                delta--;
            }
        }
    }

    // --- Method ใหม่: อัปเดตข้อมูลเกม ---
    public void update() {
        knight.update(keyH);
    }

    // --- Method เดิมที่แก้ไข ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // วาดพื้นหลัง
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // วาด Knight ตามตำแหน่ง x, y ที่อัปเดตแล้ว
        g2.drawImage(knightImage, knight.x, knight.y, 150, 150, this);

        // วาด Slime (ยังอยู่ที่เดิม)
        g2.drawImage(slimeImage, 800, 420, 120, 120, this);

        g2.dispose();
    }
}