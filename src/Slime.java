import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Slime extends Character {
    public BufferedImage image;

    public String currentAction = "idle"; // idle, attacking, returning

    public Slime() {
        super("Slime", 25, 50);
        this.originalX = 800; // <-- เก็บตำแหน่ง X เริ่มต้น
        this.originalY = 490; // <-- เก็บตำแหน่ง Y เริ่มต้น
        this.x = originalX;
        this.y = originalY;
        this.speed = 20; // <-- กำหนดความเร็วให้ Slime ด้วย
        this.solidArea = new Rectangle(x + 16, y + 16, 90, 90);
        loadDermoonImage();
    }

    public void loadDermoonImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("Picture/Slimekung.png")); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // <-- เพิ่ม Override annotation
    public void updateForBattle() {
        // (ยังไม่มี animation)
    }
}