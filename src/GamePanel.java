
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    public int gameState;
    public final int playState = 1;
    public final int battleState = 2;
    public final int gameOverState = 3;
    public final int gameClearState = 4;
    // Battle Sub-States (ละเอียดขึ้น)
    public int battleSubState;
    public final int PLAYER_TURN_START = 0; // ผู้เล่นเลือกคำสั่ง
    public final int PLAYER_MOVING_TO_TARGET = 1; // Knight วิ่งไปตี
    public final int PLAYER_ATTACK_QTE = 2; // Knight กด QTE โจมตี
    public final int PLAYER_SLASHING = 3; // Knight แสดงท่าฟัน
    public final int PLAYER_RETURNING = 4; // Knight วิ่งกลับ
    public final int ENEMY_TURN_START = 5; // เริ่มเทิร์นศัตรู (เตรียมตัว)
    public final int ENEMY_MOVING_TO_TARGET = 6; // Slime วิ่งมาตี
    public final int PLAYER_DEFENSE_QTE = 7; // ผู้เล่นกด QTE ป้องกัน
    public final int PLAYER_DODGING_BACK = 8; // Knight ถอยหลบ
    public final int ENEMY_ATTACK_EXECUTE = 9; // Slime โจมตี (ถ้าผู้เล่นหลบไม่สำเร็จ)
    public final int ENEMY_RETURNING = 10; // Slime วิ่งกลับ
    public final int PLAYER_RETURNING_FROM_DODGE = 11; // Knight กลับจากหลบ
    public final int BATTLE_MESSAGE = 12; // แสดงข้อความผลลัพธ์

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    UI ui = new UI();

    Knight knight = new Knight();
    Slime slime = new Slime();
    Dermoon dermoon = null;
    Character currentCharacterEnemy; // <-- ตัวแปรเก็บศัตรูปัจจุบัน

    Image map1Image, map2Image, currentMapImage, currentBattleBackgroundImage; // <-- เปลี่ยนชื่อตัวแปร Map
    Image slimeImage; // รูป Slime ยังต้องใช้

    int qteBarX, qteBarSpeed = 8;
    long messageDisplayTime;

    public GamePanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        // --- โหลด Map ทั้งสอง ---
        map1Image = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage(); //
        map2Image = new ImageIcon(getClass().getResource("Picture/Map2.png")).getImage(); //
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage(); //

        // --- เริ่มต้นด่านแรก ---
        currentMapImage = map2Image;
        currentBattleBackgroundImage = map2Image;
        currentCharacterEnemy = slime; // <-- ศัตรูตัวแรกคือ Slime
        gameState = playState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            knight.updateForWorld(keyH);
            // (ถ้าจะให้ศัตรูเดินสุ่ม ก็อัปเดต currentCharacterEnemy ที่นี่)

            // เช็คการชนกับศัตรูปัจจุบัน
            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()
                    && knight.solidArea.intersects(currentCharacterEnemy.solidArea)) {
                gameState = battleState;
                battleSubState = PLAYER_TURN_START;
                // Reset ตำแหน่ง Knight และ ศัตรูปัจจุบัน
                knight.x = knight.originalX;
                knight.y = knight.originalY;
                currentCharacterEnemy.x = currentCharacterEnemy.originalX; // ใช้ originalX จาก object ศัตรู
                currentCharacterEnemy.y = currentCharacterEnemy.originalY; // ใช้ originalY จาก object ศัตรู
            }
        }
        if (gameState == battleState) {
            knight.updateForBattle();
            // อัปเดตศัตรูปัจจุบัน (ถ้ามี animation)
            if (currentCharacterEnemy instanceof Slime) {
                ((Slime) currentCharacterEnemy).updateForBattle();
            } else if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon) currentCharacterEnemy).updateForBattle();
            }
            handleBattle();
        }
    }

    // --- จัดการ Battle State Machine ใหม่ทั้งหมด ---
    public void handleBattle() {
        // ใช้ currentCharacterEnemy แทน slime ใน logic ส่วนใหญ่
        if (currentCharacterEnemy == null || !currentCharacterEnemy.isAlive()) {
            // ถ้าศัตรูตายแล้ว หรือไม่มีศัตรู (อาจจะบั๊ก) ให้ลองไปเช็คเงื่อนไขชนะ/แพ้
            checkBattleEndCondition();
            return; // หยุดการทำงานถ้าศัตรูตายแล้ว
        }

        switch (battleSubState) {
            // ... (case ต่างๆ เหมือนเดิม แต่ logic ข้างในจะใช้ currentCharacterEnemy) ...
            case PLAYER_TURN_START:
                handlePlayerTurnStart();
                break;
            case PLAYER_MOVING_TO_TARGET:
                handlePlayerMovingToTarget();
                break;
            case PLAYER_ATTACK_QTE:
                handlePlayerAttackQTE();
                break;
            case PLAYER_SLASHING:
                handlePlayerSlashing();
                break;
            case PLAYER_RETURNING:
                handlePlayerReturning();
                break;
            case ENEMY_TURN_START:
                handleEnemyTurnStart();
                break;
            case ENEMY_MOVING_TO_TARGET:
                handleEnemyMovingToTarget();
                break;
            case PLAYER_DEFENSE_QTE:
                handlePlayerDefenseQTE();
                break;
            case PLAYER_DODGING_BACK:
                handlePlayerDodgingBack();
                break;
            case ENEMY_ATTACK_EXECUTE:
                handleEnemyAttackExecute();
                break;
            case ENEMY_RETURNING:
                handleEnemyReturning();
                break;
            case PLAYER_RETURNING_FROM_DODGE:
                handlePlayerReturningFromDodge();
                break;
            case BATTLE_MESSAGE:
                handleBattleMessage();
                break;
        }
    }

    private void handlePlayerTurnStart() {
        knight.currentAction = "idle"; // ให้แน่ใจว่าเริ่มเทิร์นด้วยท่า idle
        slime.currentAction = "idle";
        if (keyH.upPressed) {
            ui.commandNum = 0;
            keyH.upPressed = false;
        }
        if (keyH.downPressed) {
            ui.commandNum = 1;
            keyH.downPressed = false;
        }
        if (keyH.enterPressed) {
            if (ui.commandNum == 0) { // Attack
                battleSubState = PLAYER_MOVING_TO_TARGET;
            } else { // Skip
                battleSubState = ENEMY_TURN_START;
            }
            keyH.enterPressed = false;
        }
    }

    private void handlePlayerMovingToTarget() {
        int targetX = currentCharacterEnemy.x - 100; // ใช้ตำแหน่งศัตรูปัจจุบัน
        if (knight.x < targetX) {
            knight.x += knight.speed; 
        }else {
            knight.x = targetX;
            knight.currentAction = "ready";
            battleSubState = PLAYER_ATTACK_QTE;
            qteBarX = 400;
        }
    }

    private void handlePlayerAttackQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (qteBarX >= 680 && qteBarX <= 760) {
                knight.attack(currentCharacterEnemy); // โจมตีศัตรูปัจจุบัน
                ui.currentDialogue = "Success!";
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = PLAYER_SLASHING;
            } else {
                ui.currentDialogue = "Miss!";
                knight.currentAction = "idle";
                battleSubState = PLAYER_RETURNING; // พลาด -> กลับที่เลย
            }
            keyH.spacePressed = false;
            // ไม่ไป messageTurn ทันที
        }
        if (qteBarX > 900) { // เลยโซน
            ui.currentDialogue = "Miss!";
            knight.currentAction = "idle";
            battleSubState = PLAYER_RETURNING;
        }
    }

    private void handlePlayerSlashing() {
        if (knight.spriteNum > 1) { // รอ animation จบ
            battleSubState = PLAYER_RETURNING; // ฟันเสร็จ -> กลับที่
        }
    }

    private void handlePlayerReturning() {
        if (knight.x > knight.originalX) {
            knight.x -= knight.speed;
        } else {
            knight.x = knight.originalX;
            knight.currentAction = "idle";
            battleSubState = BATTLE_MESSAGE; // กลับถึงที่ -> แสดงข้อความ
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleEnemyTurnStart() {
        if (currentCharacterEnemy.isAlive()) { // เช็คศัตรูปัจจุบัน
            battleSubState = ENEMY_MOVING_TO_TARGET;
            // currentCharacterEnemy.currentAction = "attacking"; // ถ้ามี animation
        } else {
            // checkBattleEndCondition(); // ไปเช็คเงื่อนไขชนะ/แพ้
            gameState = gameOverState; // ชั่วคราว: ถ้าศัตรูตาย -> จบเกม (จะแก้ทีหลัง)
        }
    }

    private void handleEnemyMovingToTarget() {
        int targetX = knight.x + 100;
        if (currentCharacterEnemy.x > targetX) {
            currentCharacterEnemy.x -= currentCharacterEnemy.speed; // ใช้ speed ของศัตรูปัจจุบัน
        } else {
            currentCharacterEnemy.x = targetX;
            battleSubState = PLAYER_DEFENSE_QTE;
            qteBarX = 400;
        }
    }

    private void handlePlayerDefenseQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            boolean parried = (qteBarX >= 680 && qteBarX <= 760);
            boolean dodged = (qteBarX >= 600 && qteBarX <= 800);
            if (parried) {
                ui.currentDialogue = "Parry!";
                knight.attack(currentCharacterEnemy); // สวนศัตรูปัจจุบัน
                knight.currentAction = "slashing";
                knight.spriteNum = 1; // แสดงท่าฟันตอน Parry ด้วย
                battleSubState = ENEMY_RETURNING;
            } else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging"; // ตั้งสถานะ Knight
                battleSubState = PLAYER_DODGING_BACK; // Knight เริ่มถอย
            } else {
                ui.currentDialogue = "Hit!";
                battleSubState = ENEMY_ATTACK_EXECUTE; // ผู้เล่นโดนตี
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) { // เลยโซน
            ui.currentDialogue = "Hit!";
            battleSubState = ENEMY_ATTACK_EXECUTE;
        }
    }

    private void handlePlayerDodgingBack() {
        // Knight ถอยไปตำแหน่ง dodgeTargetX
        if (knight.x > knight.dodgeTargetX) {
            knight.x -= knight.speed / 2; // ถอยช้ากว่าวิ่งเข้า
        } else {
            knight.x = knight.dodgeTargetX;
            battleSubState = ENEMY_RETURNING; // Knight ถอยสุด -> Slime กลับ
        }
    }

    private void handleEnemyAttackExecute() {
        // Slime โจมตีจริง (อาจจะมี animation สั้นๆ ตรงนี้)
        slime.attack(knight);
        // หลังจากโจมตีเสร็จ
        battleSubState = ENEMY_RETURNING; // Slime กลับที่
    }

    private void handleEnemyReturning() {
        if (currentCharacterEnemy.x < currentCharacterEnemy.originalX) { // ใช้ originalX ของศัตรู
            currentCharacterEnemy.x += currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = currentCharacterEnemy.originalX;
            // currentCharacterEnemy.currentAction = "idle"; // ถ้ามี animation
            if (knight.currentAction.equals("dodging")) {
                battleSubState = PLAYER_RETURNING_FROM_DODGE;
            } else {
                battleSubState = BATTLE_MESSAGE;
                messageDisplayTime = System.nanoTime();
            }
        }
    }

    private void handlePlayerReturningFromDodge() {
        // Knight กลับจากตำแหน่งหลบ มาที่ originalX
        if (knight.x < knight.originalX) {
            knight.x += knight.speed / 2; // กลับช้าๆ
        } else {
            knight.x = knight.originalX;
            knight.currentAction = "idle";
            battleSubState = BATTLE_MESSAGE; // กลับถึงที่ -> แสดงข้อความ
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleBattleMessage() {
        if (System.nanoTime() - messageDisplayTime > 1500000000) {
            checkBattleEndCondition(); // <-- เรียก method ใหม่มาเช็ค
        }
    }

    private void checkBattleEndCondition() {
        if (!knight.isAlive()) {
            gameState = gameOverState; // แพ้ -> จบเกม
        } else if (!currentCharacterEnemy.isAlive()) {
            // ชนะศัตรูปัจจุบัน!
            if (currentCharacterEnemy instanceof Slime) {
                // ชนะ Slime -> เปลี่ยนไปด่าน Dermoon
                ui.currentDialogue = "Slime Defeated!"; // อาจจะแสดงข้อความก่อน
                // (อาจจะรออีกแป๊บ หรือกด Enter เพื่อไปต่อ)
                System.out.println("Switching to Dermoon stage..."); // Debug message
                currentMapImage = map1Image;
                currentBattleBackgroundImage = map1Image;
                dermoon = new Dermoon(); // สร้าง Dermoon object
                currentCharacterEnemy = dermoon; // เปลี่ยนศัตรู
                // อาจจะฟื้นเลือด Knight เล็กน้อย? knight.hp += 20; if(knight.hp > knight.maxHp) knight.hp = knight.maxHp;
                gameState = playState; // กลับไปฉากเดิน (เจอ Dermoon)
            } else if (currentCharacterEnemy instanceof Dermoon) {
                // ชนะ Dermoon -> เคลียร์เกม!
                gameState = gameClearState;
            }
        } else {
            // ยังไม่มีใครตาย -> สลับเทิร์น
            boolean lastActionWasPlayerAttack = ui.currentDialogue.equals("Success!") || ui.currentDialogue.equals("Miss!");
            // boolean lastActionWasPlayerDefense = ui.currentDialogue.equals("Parry!") || ui.currentDialogue.equals("Dodge!") || ui.currentDialogue.equals("Hit!");

            if (lastActionWasPlayerAttack) {
                battleSubState = ENEMY_TURN_START;
            } else { // if (lastActionWasPlayerDefense) {
                battleSubState = PLAYER_TURN_START;
            }
            // else { // กรณีอื่นๆ เช่น Skip
            //      battleSubState = PLAYER_TURN_START;
            // }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == playState) {
            // --- วาดฉาก Play State ---
            g2.drawImage(currentMapImage, 0, 0, getWidth(), getHeight(), this); // ใช้ Map ปัจจุบัน
            g2.drawImage(knight.idleImage, knight.x, knight.y, 200, 200, this);
            // วาดศัตรูปัจจุบัน
            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()) {
                if (currentCharacterEnemy instanceof Slime) {
                    g2.drawImage(slimeImage, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
                } else if (currentCharacterEnemy instanceof Dermoon && ((Dermoon) currentCharacterEnemy).image != null) {
                    g2.drawImage(((Dermoon) currentCharacterEnemy).image, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
                }
            }

        } else if (gameState == battleState && currentCharacterEnemy != null) { // เช็ค null เพิ่ม
            // --- วาดพื้นฐาน Battle State ---
            g2.drawImage(currentBattleBackgroundImage, 0, 0, getWidth(), getHeight(), this); // ใช้ BG ปัจจุบัน

            // วาดศัตรูปัจจุบัน
            if (currentCharacterEnemy instanceof Slime) {
                g2.drawImage(slimeImage, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
            } else if (currentCharacterEnemy instanceof Dermoon && ((Dermoon) currentCharacterEnemy).image != null) {
                g2.drawImage(((Dermoon) currentCharacterEnemy).image, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
            }

            // วาด Knight ตาม Action
            BufferedImage imageToDraw = knight.idleImage;
            if (knight.currentAction.equals("ready")) {
                imageToDraw = knight.readyImage;
            }
            if (knight.currentAction.equals("slashing")) {
                imageToDraw = knight.attackImage;
            }
            // อาจจะเพิ่มท่า dodging ถ้ามี sprite
            g2.drawImage(imageToDraw, knight.x, knight.y, 200, 200, this); // ใช้ knight.x

            // วาด HP Bars
            ui.drawHPBar(g2, 50, 50, 400, 40, knight.hp, knight.maxHp);
            g2.setFont(ui.arial_40);
            g2.setColor(Color.white);
            g2.drawString(knight.name, 50, 40);
            ui.drawHPBar(g2, 830, 50, 400, 40, slime.hp, slime.maxHp);
            g2.setFont(ui.arial_40);
            g2.setColor(Color.white);
            g2.drawString(slime.name, 830, 40);

            // --- วาด UI เฉพาะตาม Sub-State ---
            if (battleSubState == PLAYER_TURN_START) {
                ui.drawBattleScreen(g2, knight, slime);
            }
            if (battleSubState == PLAYER_ATTACK_QTE) {
                ui.drawAttackQTE(g2, qteBarX);
            }
            if (battleSubState == PLAYER_DEFENSE_QTE) {
                ui.drawDefenseQTE(g2, qteBarX);
            }
            if (battleSubState == BATTLE_MESSAGE) {
                ui.drawBattleMessage(g2);
            }

        } else if (gameState == gameOverState) {
            // --- วาดฉาก Game Over ---
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            String text = knight.isAlive() ? "You Win!" : "You Lose!";
            int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
        } else if (gameState == gameClearState) {
              // --- วาดฉาก Game Clear ---
              g2.setColor(Color.BLUE); // พื้นหลังสีฟ้า
              g2.fillRect(0, 0, getWidth(), getHeight());
              g2.setColor(Color.WHITE);
              g2.setFont(new Font("Arial", Font.BOLD, 80));
              String text = "Game Clear!";
              int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
              g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
          }

        g2.dispose();
    }
}
