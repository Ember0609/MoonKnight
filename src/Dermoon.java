import java.awt.Rectangle;
import java.awt.image.BufferedImage; // เพิ่ม import นี้
import java.io.IOException;      // เพิ่ม import นี้
import javax.imageio.ImageIO;        // เพิ่ม import นี้

public class Dermoon extends Character {
    public BufferedImage image;
    // --- ลบการประกาศ originalX, originalY ตรงนี้ ---
    public String currentAction = "idle";

    public Dermoon() {
        super("Dermoon", 150, 35); //
        // --- กำหนดค่า originalX, originalY ที่นี่ ---
        this.originalX = 800;
        this.originalY = 460;
        this.x = originalX;
        this.y = originalY;
        this.speed = 15; //
        this.solidArea = new Rectangle(x + 32, y + 16, 64, 112); //
        loadDermoonImage();
    }

    public void loadDermoonImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("Picture/DerMoon.png")); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // <-- เพิ่ม Override annotation
    public void updateForBattle() {
        // (ยังไม่มี animation)
    }
}