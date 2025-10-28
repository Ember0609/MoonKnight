
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Knight extends Character {

    public BufferedImage idleImage, readyImage, attackImage;
    public String currentAction = "idle"; // idle, ready, slashing, dodging
    public int spriteCounter = 0;
    public int spriteNum = 1; // <-- เพิ่ม originalY
    public int dodgeTargetX; // ตำแหน่ง X ตอนถอยหลบ

    public Knight() {
        super("Knight", 100, 25);
        this.originalX = 200;
        this.originalY = 470; // <-- เก็บตำแหน่ง Y เริ่มต้น
        this.dodgeTargetX = originalX - 80; // <-- ถอยไป 80 pixels
        this.x = originalX;
        this.y = originalY; // <-- ใช้ originalY
        this.speed = 30;
        this.solidArea = new Rectangle(x + 48, y + 48, 50, 50);
        loadKnightImages();
    }

    // ในไฟล์ Knight.java
    public void loadKnightImages() {
        try {
            idleImage = ImageIO.read(getClass().getResourceAsStream("Picture/MainFront.png"));
            BufferedImage attackSheet = ImageIO.read(getClass().getResourceAsStream("Picture/MainPSlash.png"));

            // --- แก้ไขตรงนี้ ---
            int frameWidth = 64;  // แก้จาก 48 เป็น 64
            int frameHeight = 64; // แก้จาก 48 เป็น 64

            // ตัด Spritesheet ด้วยขนาด 64x64
            readyImage = attackSheet.getSubimage(0, 0, frameWidth, frameHeight);
            attackImage = attackSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateForWorld(KeyHandler keyH) {
        // Method สำหรับอัปเดตตอนเดินสำรวจโลก
        if (keyH.upPressed) {
            y -= 4; // ใช้ความเร็วปกติ

                }if (keyH.downPressed) {
            y += 4;
        }
        if (keyH.leftPressed) {
            x -= 4;
        }
        if (keyH.rightPressed) {
            x += 30;
        }
        solidArea.x = x + 48;
        solidArea.y = y + 48;
    }

    @Override // <-- เพิ่ม Override annotation
    public void updateForBattle() { // แก้ไข method นี้ในคลาสแม่
        spriteCounter++;
        if (currentAction.equals("slashing")) {
            if (spriteCounter > 6) {
                spriteNum++;
                spriteCounter = 0;
            }
        }
    }
}
