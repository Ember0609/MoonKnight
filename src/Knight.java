import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Knight extends Character {

    public BufferedImage idleImage, readyImage, attackImage;
    public String currentAction = "idle"; // idle, ready, slashing, dodging
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public int dodgeTargetX;

    public Knight() {
        super("Knight", 100, 25);
        this.originalX = 200;
        this.originalY = 470;
        this.dodgeTargetX = originalX - 80;
        this.x = originalX;
        this.y = originalY;
        this.speed = 30;
        this.solidArea = new Rectangle(x + 48, y + 48, 50, 50);
        loadKnightImages();
    }

    public void loadKnightImages() {
        try {
            idleImage = ImageIO.read(getClass().getResourceAsStream("Picture/MainFront.png"));
            BufferedImage attackSheet = ImageIO.read(getClass().getResourceAsStream("Picture/MainPSlash.png"));
            int frameWidth = 64;
            int frameHeight = 64;
            readyImage = attackSheet.getSubimage(0, 0, frameWidth, frameHeight);
            attackImage = attackSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateForWorld(KeyHandler keyH) {
        if (keyH.upPressed) { y -= 4; }
        if (keyH.downPressed) { y += 4; }
        if (keyH.leftPressed) { x -= 4; }
        if (keyH.rightPressed) { x += 30; }
        solidArea.x = x + 48;
        solidArea.y = y + 48;
    }

    @Override
    public void updateForBattle() {
        spriteCounter++;
        if (currentAction.equals("slashing")) {
            if (spriteCounter > 6) {
                spriteNum++;
                spriteCounter = 0;
            }
        }
    }

    // +++ อัปเดตเมธอดนี้ +++
    @Override
    public BufferedImage getCurrentImage() {
        BufferedImage imageToDraw = idleImage; // ท่าเริ่มต้น
        
        if (currentAction.equals("ready")) {
            imageToDraw = readyImage;
        }
        else if (currentAction.equals("slashing")) {
            imageToDraw = attackImage;
        }
        // +++ เพิ่ม else if สำหรับท่าหลบ +++
        else if (currentAction.equals("dodging")) {
            imageToDraw = idleImage; // (ถ้ามีรูปหลบโดยเฉพาะ ก็เปลี่ยนตรงนี้)
        }
        
        return imageToDraw;
    }
}