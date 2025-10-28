
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    public int gameState;
    public final int playState = 1;
    public final int battleState = 2;
    public final int gameOverState = 3;

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

    Image backgroundImage, slimeImage, battleBackgroundImage;

    int qteBarX, qteBarSpeed = 8;
    long messageDisplayTime;

    public GamePanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        backgroundImage = new ImageIcon(getClass().getResource("Picture/Map2.png")).getImage();
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage();
        battleBackgroundImage = new ImageIcon(getClass().getResource("Picture/Map2.png")).getImage();
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
            if (knight.solidArea.intersects(slime.solidArea)) {
                gameState = battleState;
                battleSubState = PLAYER_TURN_START;
                knight.x = knight.originalX;
                knight.y = knight.originalY;
                slime.x = slime.originalX;
                slime.y = slime.originalY;
            }
        }
        if (gameState == battleState) {
            knight.updateForBattle();
            handleBattle();
        }
    }

    // --- จัดการ Battle State Machine ใหม่ทั้งหมด ---
    public void handleBattle() {
        switch (battleSubState) {
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
        int targetX = slime.x - 100;
        if (knight.x < targetX) {
            knight.x += knight.speed;
        } else {
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
                knight.attack(slime); // ทำ Damage จริง
                ui.currentDialogue = "Success!";
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = PLAYER_SLASHING; // ไปแสดงท่าฟัน
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
        if (slime.isAlive()) {
            battleSubState = ENEMY_MOVING_TO_TARGET;
            slime.currentAction = "attacking"; // สมมติว่ามี action นี้
        } else {
            gameState = gameOverState;
        }
    }

    private void handleEnemyMovingToTarget() {
        int targetX = knight.x + 100; // เป้าหมายคือหน้า Knight
        if (slime.x > targetX) {
            slime.x -= slime.speed;
        } else {
            slime.x = targetX;
            battleSubState = PLAYER_DEFENSE_QTE; // Slime มาถึง -> ผู้เล่นเตรียมป้องกัน
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
                knight.attack(slime); // สวนกลับ
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = ENEMY_RETURNING; // ศัตรูกลับที่เลย
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
        // Slime กลับไปตำแหน่ง originalX
        if (slime.x < slime.originalX) {
            slime.x += slime.speed;
        } else {
            slime.x = slime.originalX;
            slime.currentAction = "idle";
            // ถ้า Knight กำลังหลบอยู่ ให้ Knight เริ่มกลับด้วย
            if (knight.currentAction.equals("dodging")) {
                battleSubState = PLAYER_RETURNING_FROM_DODGE;
            } else {
                // ถ้าไม่ได้หลบ (โดนตี หรือ Parry) -> แสดงข้อความเลย
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
            if (!knight.isAlive() || !slime.isAlive()) {
                gameState = gameOverState;
            } else {
                // เช็คว่า Action ล่าสุดมาจาก Player หรือ Enemy เพื่อสลับเทิร์น
                boolean lastActionWasPlayerAttack = ui.currentDialogue.equals("Success!") || ui.currentDialogue.equals("Miss!");
                boolean lastActionWasPlayerDefense = ui.currentDialogue.equals("Parry!") || ui.currentDialogue.equals("Dodge!") || ui.currentDialogue.equals("Hit!");

                if (lastActionWasPlayerAttack) {
                    battleSubState = ENEMY_TURN_START; // จบเทิร์นผู้เล่น -> เริ่มเทิร์นศัตรู
                } else if (lastActionWasPlayerDefense) {
                    battleSubState = PLAYER_TURN_START; // จบเทิร์นศัตรู -> เริ่มเทิร์นผู้เล่น
                } else {
                    // กรณีอื่นๆ (เช่น Skip) อาจจะต้องปรับตามต้องการ
                    // ปัจจุบัน Skip จะไป ENEMY_TURN_START โดยตรง ไม่ผ่าน Message
                    battleSubState = PLAYER_TURN_START; // Default กลับไปเทิร์นผู้เล่น
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == playState) {
            // --- วาดฉาก Play State ---
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(knight.idleImage, knight.x, knight.y, 200, 200, this);
            g2.drawImage(slimeImage, slime.x, slime.y, 200, 200, this);

        } else if (gameState == battleState) {
            // --- วาดพื้นฐาน Battle State ---
            g2.drawImage(battleBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(slimeImage, slime.x, slime.y, 200, 200, this); // ใช้ slime.x

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
        }

        g2.dispose();
    }
}
