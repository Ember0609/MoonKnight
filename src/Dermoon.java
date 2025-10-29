
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Dermoon extends Character {

    // ท่าโจมตี 3 รูปแบบ
    public BufferedImage runImage; // +++ แก้ไข: ท่าวิ่ง (Dermoon1-frame1)
    public BufferedImage runReadyImage; // +++ แก้ไข: ท่าชาร์จวิ่ง (Dermoon1-frame2)
    public BufferedImage[] phinkAnim = new BufferedImage[4]; // จาก Dermoon2.png
    public BufferedImage[] zapAnim = new BufferedImage[3]; // จาก Dermoon3.png

    public String currentAction = "idle";
    public int spriteCounter = 0;
    // public int spriteNum = 1; // --- ลบออก: ท่าวิ่งจะใช้รูปเดียว ---

    public Dermoon() {
        super("Dermoon", 125, 35);
        this.originalX = 800;
        this.originalY = 460;
        this.x = originalX;
        this.y = originalY;
        this.speed = 15;
        this.solidArea = new Rectangle(x + 32, y + 16, 64, 112);
        loadDermoonImage();

        this.currentAction = "idle"; // +++ แก้ไข: เริ่มต้นเป็น idle (zapAnim[0])
    }

    public void loadDermoonImage() {
        try {
            int frameWidth = 64;
            int frameHeight = 64;

            // 1. โหลดท่าวิ่ง (Dermoon1.png)
            BufferedImage runSheet = ImageIO.read(getClass().getResourceAsStream("Picture/Dermoon1.png"));
            runImage = runSheet.getSubimage(0, 0, frameWidth, frameHeight); // +++ แก้ไข: รูปวิ่ง
            runReadyImage = runSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight); // +++ แก้ไข: รูปชาร์จ

            // 2. โหลดท่ายิง Phink (Dermoon2.png)
            BufferedImage phinkSheet = ImageIO.read(getClass().getResourceAsStream("Picture/Dermoon2.png"));
            phinkAnim[0] = phinkSheet.getSubimage(0, 0, frameWidth, frameHeight);
            phinkAnim[1] = phinkSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            phinkAnim[2] = phinkSheet.getSubimage(frameWidth * 2, 0, frameWidth, frameHeight);
            phinkAnim[3] = phinkSheet.getSubimage(frameWidth * 3, 0, frameWidth, frameHeight);

            // 3. โหลดท่า Zap (Dermoon3.png)
            BufferedImage zapSheet = ImageIO.read(getClass().getResourceAsStream("Picture/Dermoon3.png"));
            zapAnim[0] = zapSheet.getSubimage(0, 0, frameWidth, frameHeight);
            zapAnim[1] = zapSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            zapAnim[2] = zapSheet.getSubimage(frameWidth * 2, 0, frameWidth, frameHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateForBattle() {
        spriteCounter++;
    }
}
