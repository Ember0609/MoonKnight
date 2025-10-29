import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Dermoon extends Character {

    public BufferedImage runImage;
    public BufferedImage runReadyImage;
    public BufferedImage[] phinkAnim = new BufferedImage[4];
    public BufferedImage[] zapAnim = new BufferedImage[3];
    public String currentAction = "idle";
    public int spriteCounter = 0;

    public Dermoon() {
        super("Dermoon", 125, 35);
        this.originalX = 800;
        this.originalY = 460;
        this.x = originalX;
        this.y = originalY;
        this.speed = 15;
        this.solidArea = new Rectangle(x + 32, y + 16, 64, 112);
        loadDermoonImage();
        this.currentAction = "idle";
    }

    public void loadDermoonImage() {
        try {
            int frameWidth = 64;
            int frameHeight = 64;
            // 1. โหลดท่าวิ่ง (Dermoon1.png)
            BufferedImage runSheet = ImageIO.read(getClass().getResourceAsStream("Picture/Dermoon1.png"));
            runImage = runSheet.getSubimage(0, 0, frameWidth, frameHeight);
            runReadyImage = runSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
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

    // +++ เพิ่มเมธอด performTurn (AI) (สำหรับ Polymorphism) +++
    @Override
    public void performTurn(GamePanel gp) {
        // ตรรกะ AI ของ Dermoon ย้ายมาอยู่ที่นี่
        int dermoonAttackChoice = (int) (Math.random() * 2) + 1; // สุ่ม 1 (Run) หรือ 2 (Phink)
        
        // GamePanel ยังต้องเก็บค่าเหล่านี้ไว้ใช้ใน State อื่นๆ
        gp.dermoonAttackChoice = dermoonAttackChoice; 
        
        System.out.println("Dermoon chooses attack: " + dermoonAttackChoice);

        switch (dermoonAttackChoice) {
            case 1: // ท่า Run (Dermoon1)
                gp.battleSubState = BattleSubState.ENEMY_RUN_MOVE; // เปลี่ยน State ของ GamePanel
                this.currentAction = "running"; // Dermoon เปลี่ยนท่าทางของตัวเอง
                break;
            case 2: // ท่า Phink (Dermoon2)
                gp.phinkDodgeCount = 2;
                gp.battleSubState = BattleSubState.ENEMY_PHINK_CHARGE;
                gp.messageDisplayTime = System.nanoTime(); // เริ่มจับเวลาชาร์จ
                break;
        }
    }

    // +++ เพิ่มเมธอด getCurrentImage (สำหรับ Polymorphism) +++
    @Override
    public BufferedImage getCurrentImage() {
        // ย้ายตรรกะการเลือกรูปมาจาก GamePanel
        BufferedImage imageToDraw = zapAnim[0]; // ท่า idle เริ่มต้น
        
        if (currentAction.equals("running")) imageToDraw = runImage;
        else if (currentAction.equals("run_ready")) imageToDraw = runReadyImage;
        else if (currentAction.equals("zap_charge")) imageToDraw = zapAnim[1];
        else if (currentAction.equals("zap_fire")) imageToDraw = zapAnim[2];
        else if (currentAction.equals("phink_charge_1")) imageToDraw = phinkAnim[1];
        else if (currentAction.equals("phink_fire_1")) imageToDraw = phinkAnim[2];
        else if (currentAction.equals("phink_charge_2")) imageToDraw = phinkAnim[3];
        
        return imageToDraw;
    }
}