import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Slime extends Character {
    public BufferedImage image;
    public String currentAction = "idle"; // idle, attacking, returning

    public Slime() {
        super("Slime", 150, 50);
        this.originalX = 800;
        this.originalY = 490;
        this.x = originalX;
        this.y = originalY;
        this.speed = 20;
        this.solidArea = new Rectangle(x + 16, y + 16, 90, 90);
        loadSlimeImage();
    }

    public void loadSlimeImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("Picture/Slimekung.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateForBattle() {
        // (ยังไม่มี animation)
    }

    @Override
    public BufferedImage getCurrentImage() {
        return this.image; // คืนค่ารูป Slime ที่โหลดไว้
    }
}