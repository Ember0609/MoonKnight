import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Knight extends Character {

    public BufferedImage idleImage, readyImage, attackImage;
    public BufferedImage[] walkImages = new BufferedImage[3];

    public String currentAction = "idle";
    public int spriteCounter = 0; // สำหรับท่าฟัน (slashing)
    public int spriteNum = 1;     // สำหรับท่าฟัน (slashing)

    public int walkSpriteCounter = 0;
    public int walkSpriteNum = 0; // เริ่มจากเฟรม 0

    public int dodgeTargetX;

    // +++ เพิ่มตัวแปร walkSpeed +++
    public int walkSpeed;

    public Knight() {
        super("Knight", 100, 25);
        this.originalX = 200;
        this.originalY = 480;
        this.dodgeTargetX = originalX - 80;
        this.x = originalX;
        this.y = originalY;
        this.speed = 30; // ความเร็วเดิม (อาจใช้ใน Battle หรืออื่นๆ)
        // +++ กำหนดค่า walkSpeed +++
        this.walkSpeed = 8; // ความเร็วตอนเดินสำรวจโลก (ปรับค่าได้ตามต้องการ)
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

            BufferedImage walkSheet = ImageIO.read(getClass().getResourceAsStream("Picture/Walk.png"));
            walkImages[0] = walkSheet.getSubimage(0, 0, frameWidth, frameHeight);
            walkImages[1] = walkSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            walkImages[2] = walkSheet.getSubimage(frameWidth * 2, 0, frameWidth, frameHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // +++ อัปเดตเมธอดนี้ ให้ใช้ walkSpeed +++
    public void updateForWorld(KeyHandler keyH) {
        boolean isMoving = false;

        if (keyH.upPressed) {
            y -= walkSpeed;
            isMoving = true;
        }
        if (keyH.leftPressed) {
            x -= walkSpeed;
            isMoving = true;
        }
        if (keyH.rightPressed) {
            x += walkSpeed;
            isMoving = true;
        }
        // --- จบส่วนที่แก้ไข ---

        if (isMoving) {
            currentAction = "walking";
            walkSpriteCounter++;
            if (walkSpriteCounter > 10) {
                walkSpriteNum++;
                if (walkSpriteNum > 2) {
                    walkSpriteNum = 0;
                }
                walkSpriteCounter = 0;
            }
        } else {
            currentAction = "idle";
            walkSpriteNum = 0;
            walkSpriteCounter = 0;
        }

        solidArea.x = x + 48;
        solidArea.y = y + 48;
    }

    @Override
    public void updateForBattle() {
        // (ส่วนนี้เหมือนเดิม)
        spriteCounter++;
        if (currentAction.equals("slashing")) {
            if (spriteCounter > 6) {
                spriteNum++;
                if (spriteNum > 1) {
                    // spriteNum = 1; // อาจจะรีเซ็ตเลยก็ได้
                }
                spriteCounter = 0;
            }
        } else {
             spriteNum = 1;
             spriteCounter = 0;
        }

         walkSpriteNum = 0;
         walkSpriteCounter = 0;

         if (!currentAction.equals("idle") && !currentAction.equals("ready") &&
             !currentAction.equals("slashing") && !currentAction.equals("dodging")) {
            currentAction = "idle";
        }
    }

    @Override
    public BufferedImage getCurrentImage() {
        // (ส่วนนี้เหมือนเดิม)
        BufferedImage imageToDraw = null;

        switch (currentAction) {
            case "idle":
                imageToDraw = walkImages[0];
                break;
            case "walking":
                imageToDraw = walkImages[walkSpriteNum];
                break;
            case "ready":
                imageToDraw = readyImage;
                break;
            case "slashing":
                 imageToDraw = attackImage;
                break;
            case "dodging":
                 imageToDraw = walkImages[0];
                break;
            default:
                 imageToDraw = walkImages[0];
                break;
        }

        if (imageToDraw == null) {
            imageToDraw = idleImage;
        }

        return imageToDraw;
    }
}