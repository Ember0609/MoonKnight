
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Knight extends Character {

    // Animation Images
    public BufferedImage idleImage, readyImage, attackImage;
    public String currentAction = "idle";

    // Animation Timers
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // Position Memory
    public int originalX;

    public Knight() {
        super("Knight", 100, 25);

        this.originalX = 200; // ตำแหน่งยืนปกติในฉากต่อสู้
        this.x = originalX;
        this.y = 470;
        this.speed = 10; // เพิ่มความเร็วตอนพุ่งเข้าไป
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
            x += 4;
        }
        solidArea.x = x + 48;
        solidArea.y = y + 48;
    }

    public void updateForBattle() {
        // Method สำหรับอัปเดต Animation ในฉากต่อสู้
        spriteCounter++;
        // ทำให้ท่าฟันแสดงผลเร็วขึ้น
        if (currentAction.equals("slashing")) {
            if (spriteCounter > 6) { // เปลี่ยนจาก 12 เป็น 6
                spriteNum++; // นับ frame ไปเรื่อยๆ
                spriteCounter = 0;
            }
        }
    }
}
