import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    // ประกาศตัวแปรสำหรับเก็บรูปภาพ
    Image backgroundImage;
    Image knightImage;
    Image slimeImage;

    public GamePanel() {
        // โหลดรูปภาพจากไฟล์โดยตรงจากโฟลเดอร์ Picture
        backgroundImage = new ImageIcon("Picture/Map1.png").getImage();
        knightImage = new ImageIcon("Picture/MainFront.png").getImage();
        slimeImage = new ImageIcon("Picture/Slimekung.png").getImage();
    }

    // method นี้จะถูกเรียกอัตโนมัติเพื่อวาดภาพลงบน Panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดพื้นหลังให้เต็มจอ
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // วาด Knight
        g.drawImage(knightImage, 150, 400, 150, 150, this);

        // วาด Slime
        g.drawImage(slimeImage, 800, 420, 120, 120, this);
    }
}