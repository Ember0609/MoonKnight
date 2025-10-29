
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    public int gameState;
    public final int titleState = 0;
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
    public final int ENEMY_ATTACK_ANIMATION = 13; // (ท่า Zap)

    // +++ เพิ่ม State ใหม่สำหรับ Dermoon +++
    public final int ENEMY_RUN_MOVE = 14; // ท่าวิ่ง (Dermoon1)
    public final int PLAYER_DEFENSE_QTE_RUN = 15; // QTE สำหรับท่าวิ่ง
    public final int ENEMY_ATTACK_EXECUTE_RUN = 16; // โจมตีหลังจากวิ่ง
    public final int ENEMY_PHINK_CHARGE = 17; // ชาร์จยิง Phink (Dermoon2)
    public final int PLAYER_DEFENSE_QTE_PHINK = 18; // QTE หลบ Phink
    public final int ENEMY_PHINK_HIT_EXECUTE = 19; // โดน Phink โจมตี
    public final int ENEMY_LAST_STAND_QTE = 20;

    // +++ เพิ่ม State ใหม่สำหรับท่า Last Stand (วิ่งเข้ามาก่อน) +++
    public final int ENEMY_LAST_STAND_MOVE = 21;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    UI ui = new UI();

    Knight knight = new Knight();
    Slime slime = new Slime();

    Dermoon dermoon = null;
    Character currentCharacterEnemy; // <-- ตัวแปรเก็บศัตรูปัจจุบัน

    Image map1Image, map2Image, currentMapImage, currentBattleBackgroundImage; // <-- เปลี่ยนชื่อตัวแปร Map
    Image slimeImage; // รูป Slime ยังต้องใช้
    Image phinkImage;
    Image titleImage;

    int qteBarX, qteBarSpeed = 8;
    long messageDisplayTime;
    int dermoonAttackChoice = 0; // 0=Zap, 1=Run, 2=Phink
    int phinkDodgeCount = 0; // ต้องหลบ 2 ครั้ง
    int phinkX, phinkY, phinkSpeed = 25;
    boolean phinkVisible = false;
    boolean dermoonLastStandTriggered = false;

    // Attack QTE (Knight)
    int attackSuccessX = 680;
    int attackSuccessWidth = 80;

    // Defense QTE (Default/Slime)
    int defenseDodgeX = 600;
    int defenseDodgeWidth = 200;
    int defenseParryX = 680;
    int defenseParryWidth = 80;

    // Defense QTE (Dermoon Run) - อาจจะปรับให้ยากขึ้น
    int runDodgeX = 620;
    int runDodgeWidth = 180;
    int runParryX = 690;
    int runParryWidth = 60;

    // Defense QTE (Dermoon Phink) - Dodge กว้าง Parry แคบ
    int phinkDodgeX = 580;
    int phinkDodgeWidth = 240;
    int phinkParryX = 700;
    int phinkParryWidth = 40;

    // Defense QTE (Last Stand) - Dodge อย่างเดียว, โซนอาจจะเลื่อน
    int lastStandDodgeX = 650;
    int lastStandDodgeWidth = 150;
    // (ไม่มี Parry Zone สำหรับ Last Stand แต่ต้องส่งค่าไปให้ UI)
    int lastStandParryX = 0; // ตำแหน่ง X ที่มองไม่เห็น
    int lastStandParryWidth = 0; // ความกว้างเป็น 0

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
        phinkImage = new ImageIcon(getClass().getResource("Picture/Phink.png")).getImage(); // +++ โหลดรูป Phink +++
        titleImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage(); // +++ โหลดรูป Title Screen +++

        // --- เริ่มต้นด่านแรก ---
        currentMapImage = map2Image;
        currentBattleBackgroundImage = map2Image;
        currentCharacterEnemy = slime; // <-- ศัตรูตัวแรกคือ Slime
        gameState = titleState;
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
        if (gameState == titleState) {
            handleTitleState(); // +++ เรียกเมธอดจัดการ Title State +++
        }
        else if (gameState == playState) {
            // ... (โค้ด Play State เดิม) ...
            knight.updateForWorld(keyH);
            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()
                    && knight.solidArea.intersects(currentCharacterEnemy.solidArea)) {
                gameState = battleState;
                battleSubState = PLAYER_TURN_START;
                knight.x = knight.originalX;
                knight.y = knight.originalY;
                currentCharacterEnemy.x = currentCharacterEnemy.originalX;
                currentCharacterEnemy.y = currentCharacterEnemy.originalY;
            }
        }
       else if (gameState == battleState) {
            // ... (โค้ด Battle State เดิม) ...
            knight.updateForBattle();
            if (currentCharacterEnemy instanceof Slime) ((Slime) currentCharacterEnemy).updateForBattle();
            else if (currentCharacterEnemy instanceof Dermoon) ((Dermoon) currentCharacterEnemy).updateForBattle();
            if (phinkVisible) { /* ... Phink logic ... */
                phinkX -= phinkSpeed;
                 if (phinkX < knight.x + 50) {
                     if (battleSubState == PLAYER_DEFENSE_QTE_PHINK) {
                         battleSubState = ENEMY_PHINK_HIT_EXECUTE;
                     }
                 }
            }
            handleBattle();
        }
    }

    private void handleTitleState() {
        if (keyH.upPressed) {
            ui.commandNum--; // ใช้ commandNum ของ UI ร่วมกัน
            if (ui.commandNum < 0) {
                ui.commandNum = 1; // วนกลับไปเมนูล่างสุด (Exit)
            }
            keyH.upPressed = false; // เคลียร์สถานะปุ่ม
        }
        if (keyH.downPressed) {
            ui.commandNum++;
            if (ui.commandNum > 1) {
                ui.commandNum = 0; // วนกลับไปเมนูบนสุด (Start)
            }
            keyH.downPressed = false; // เคลียร์สถานะปุ่ม
        }
        if (keyH.enterPressed) {
            if (ui.commandNum == 0) { // เลือก Start
                gameState = playState; // เปลี่ยนไป Play State
                // ไม่ต้อง reset อะไร เพราะเกมยังไม่ได้เริ่ม
            } else if (ui.commandNum == 1) { // เลือก Exit
                System.exit(0); // ออกจากโปรแกรม
            }
             keyH.enterPressed = false; // เคลียร์สถานะปุ่ม (สำคัญ!)
        }
    }

    // --- จัดการ Battle State Machine ใหม่ทั้งหมด ---
    public void handleBattle() {

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
            case ENEMY_ATTACK_ANIMATION: // แอนิเมชัน (Zap)
                handleEnemyAttackAnimation();
                break;

            // +++ Case ใหม่ของ Dermoon +++
            case ENEMY_RUN_MOVE:
                handleEnemyRunMove();
                break;
            case PLAYER_DEFENSE_QTE_RUN:
                handlePlayerDefenseQTERun();
                break;
            case ENEMY_ATTACK_EXECUTE_RUN:
                handleEnemyAttackExecuteRun();
                break;
            case ENEMY_PHINK_CHARGE:
                handleEnemyPhinkCharge();
                break;
            case PLAYER_DEFENSE_QTE_PHINK:
                handlePlayerDefenseQTEPhink();
                break;
            case ENEMY_PHINK_HIT_EXECUTE:
                handleEnemyPhinkHitExecute();
                break;
            case ENEMY_LAST_STAND_QTE:
                handleEnemyLastStandQTE();
                break;

            // +++ Case ใหม่สำหรับ Last Stand +++
            case ENEMY_LAST_STAND_MOVE:
                handleEnemyLastStandMove();
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
        if (currentCharacterEnemy.isAlive()) { // +++ ตรวจสอบว่าศัตรูยังมีชีวิตอยู่ (สำคัญสำหรับ Last Stand) +++
            if (currentCharacterEnemy instanceof Slime) {
                battleSubState = ENEMY_MOVING_TO_TARGET;
            } else if (currentCharacterEnemy instanceof Dermoon) {
                Dermoon d = (Dermoon) currentCharacterEnemy;

                // +++ แก้ไขการสุ่ม: สุ่มแค่ 1 (Run) หรือ 2 (Phink) +++
                dermoonAttackChoice = (int) (Math.random() * 2) + 1;

                System.out.println("Dermoon chooses attack: " + dermoonAttackChoice);

                switch (dermoonAttackChoice) {
                    // (ลบ case 0 (Zap) ออกจากการสุ่มปกติ)
                    case 1: // ท่า Run (Dermoon1)
                        battleSubState = ENEMY_RUN_MOVE;
                        d.currentAction = "running";
                        break;
                    case 2: // ท่า Phink (Dermoon2)
                        phinkDodgeCount = 2; // ต้องหลบ 2 ครั้ง
                        battleSubState = ENEMY_PHINK_CHARGE;
                        // d.currentAction = "phink_charge"; // (ลบออก, ไปตั้งใน handleEnemyPhinkCharge)
                        messageDisplayTime = System.nanoTime(); // เริ่มจับเวลาชาร์จ
                        break;
                }
            }
        } else {
            // +++ ถ้าศัตรูตาย, ไปที่ checkBattleEndCondition (ซึ่งจะจัดการ Last Stand) +++
            checkBattleEndCondition();
        }
    }

    // +++ แก้ไข: ท่านี้จะใช้สำหรับ Slime เท่านั้น +++
    private void handleEnemyMovingToTarget() {
        // (Dermoon ท่า Zap จะไม่ถูกเรียกมาที่นี่อีกต่อไป)
        int targetX = knight.x + 100;
        if (currentCharacterEnemy.x > targetX) {
            currentCharacterEnemy.x -= currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = targetX;
            battleSubState = PLAYER_DEFENSE_QTE;
            qteBarX = 400;
        }
    }

    // +++ แก้ไข: ท่านี้จะใช้สำหรับ Slime เท่านั้น +++
    private void handlePlayerDefenseQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            // ใช้ defenseParryX, defenseParryWidth, defenseDodgeX, defenseDodgeWidth
            boolean parried = (qteBarX >= defenseParryX && qteBarX <= defenseParryX + defenseParryWidth);
            boolean dodged = (qteBarX >= defenseDodgeX && qteBarX <= defenseDodgeX + defenseDodgeWidth);
            if (parried) {
                ui.currentDialogue = "Parry!";
                knight.attack(currentCharacterEnemy);
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = ENEMY_RETURNING;
            } else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = PLAYER_DODGING_BACK;
            } else {
                ui.currentDialogue = "Hit!";
                battleSubState = ENEMY_ATTACK_EXECUTE;
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

            // +++ เพิ่มเงื่อนไขเช็ค Last Stand +++
            if (dermoonLastStandTriggered && ui.currentDialogue.equals("Last Stand Dodge!")) {
                // ถ้าเป็นการหลบท่า Last Stand
                currentCharacterEnemy.hp = 0; // ทำให้ Dermoon ตาย
                battleSubState = BATTLE_MESSAGE; // ไปแสดงผล "Last Stand Dodge!"
                messageDisplayTime = System.nanoTime(); // รีเซ็ตเวลาสำหรับ BATTLE_MESSAGE
            } else {
                // ถ้าเป็นการหลบธรรมดา (Slime หรือ Dermoon Run)
                battleSubState = ENEMY_RETURNING; // ให้ศัตรูวิ่งกลับตามปกติ
            }
            // --- จบส่วนที่เพิ่ม/แก้ไข ---
        }
    }

    // +++ แก้ไข: ท่านี้จะใช้สำหรับ Slime เท่านั้น +++
    private void handleEnemyAttackExecute() {
        currentCharacterEnemy.attack(knight);
        // (Dermoon จะไม่ใช้ท่า Zap ที่นี่แล้ว)
        battleSubState = ENEMY_RETURNING; // Slime โจมตีเสร็จ กลับที่เลย
    }

    // +++ แก้ไข: ท่านี้ใช้สำหรับ Zap (Last Stand) เท่านั้น +++
    private void handleEnemyAttackAnimation() {
        // รอ 0.5 วินาที ให้ท่าโจมตี (Zap) แสดง
        if (System.nanoTime() - messageDisplayTime > 500000000) {
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon) currentCharacterEnemy).currentAction = "running"; // เปลี่ยนเป็นท่าวิ่งเพื่อกลับ
            }
            battleSubState = ENEMY_RETURNING; // ค่อยกลับที่
        }
    }

    private void handleEnemyReturning() {
        if (currentCharacterEnemy instanceof Dermoon) {
            ((Dermoon) currentCharacterEnemy).currentAction = "running"; // วิ่งกลับ
        }

        if (currentCharacterEnemy.x < currentCharacterEnemy.originalX) {
            currentCharacterEnemy.x += currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = currentCharacterEnemy.originalX;
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon) currentCharacterEnemy).currentAction = "idle"; // กลับท่า Idle
            }
            if (knight.currentAction.equals("dodging")) {
                battleSubState = PLAYER_RETURNING_FROM_DODGE;
            } else {
                battleSubState = BATTLE_MESSAGE;
                messageDisplayTime = System.nanoTime();
            }
        }
    }

    // +++ แก้ไข: ท่า 1 (Run) ให้เปลี่ยนท่าตอน QTE +++
    private void handleEnemyRunMove() {
        ((Dermoon) currentCharacterEnemy).currentAction = "running";
        int targetX = knight.x + 100;

        if (currentCharacterEnemy.x > targetX) {
            currentCharacterEnemy.x -= currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = targetX;
            // +++ เปลี่ยนเป็นท่าชาร์จ (Dermoon1-frame2) +++
            ((Dermoon) currentCharacterEnemy).currentAction = "run_ready";
            battleSubState = PLAYER_DEFENSE_QTE_RUN; // เริ่ม QTE
            qteBarX = 400;
        }
    }

    // +++ แก้ไข: ท่า 1 (Run) ให้กลับเป็นท่าวิ่งหลัง QTE +++
    private void handlePlayerDefenseQTERun() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon)currentCharacterEnemy).currentAction = "running";
            }
            // ใช้ runParryX, runParryWidth, runDodgeX, runDodgeWidth
            boolean parried = (qteBarX >= runParryX && qteBarX <= runParryX + runParryWidth);
            boolean dodged = (qteBarX >= runDodgeX && qteBarX <= runDodgeX + runDodgeWidth);
            if (parried) {
                ui.currentDialogue = "Parry!";
                knight.attack(currentCharacterEnemy);
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = ENEMY_RETURNING;
            } else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = PLAYER_DODGING_BACK;
            } else {
                ui.currentDialogue = "Hit!";
                battleSubState = ENEMY_ATTACK_EXECUTE_RUN;
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) { // เลยโซน
             if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon)currentCharacterEnemy).currentAction = "running";
            }
            ui.currentDialogue = "Hit!";
            battleSubState = ENEMY_ATTACK_EXECUTE_RUN;
        }
    }

    private void handleEnemyAttackExecuteRun() {
        currentCharacterEnemy.attack(knight);
        // (เราไม่ต้องเซ็ต action ที่นี่ เพราะ handleEnemyReturning จะเซ็ตเป็น "running" ให้อยู่แล้ว)
        battleSubState = ENEMY_RETURNING; // กลับที่
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

    // +++ แก้ไข: ท่า 2 (Phink) Logic ใหม่สำหรับ 4 เฟรม +++
    private void handleEnemyPhinkCharge() {
        Dermoon d = (Dermoon) currentCharacterEnemy;
        String chargeAction = "";
        String fireAction = "";

        if (phinkDodgeCount == 2) { // First Phink
            chargeAction = "phink_charge_1"; // (จะใช้ Frame 2)
            fireAction = "phink_fire_1";   // (จะใช้ Frame 3)
        } else if (phinkDodgeCount == 1) { // Second Phink
            chargeAction = "phink_fire_1"; // (จะใช้ Frame 4)
            fireAction = "phink_charge_2";   // (จะใช้ Frame 4)
        }

        d.currentAction = chargeAction; // ตั้งค่าท่าชาร์จ

        // รอ 1 วินาที
        if (System.nanoTime() - messageDisplayTime > 1000000000) {
            d.currentAction = fireAction; // ตั้งค่าท่ายิง

            // ยิง Phink
            phinkVisible = true;
            phinkX = currentCharacterEnemy.x - 50;
            phinkY = currentCharacterEnemy.y + 70;

            battleSubState = PLAYER_DEFENSE_QTE_PHINK;
            qteBarX = 400;
        }
    }

    // (ท่า 2 Phink - แก้ไข QTE Speed แล้ว)
    private void handlePlayerDefenseQTEPhink() {
        qteBarX += 25; // ความเร็ว QTE ของท่านี้

        if (keyH.spacePressed) {
            phinkVisible = false;
            phinkDodgeCount--;

            // ใช้ phinkParryX, phinkParryWidth, phinkDodgeX, phinkDodgeWidth
            boolean parried = (qteBarX >= phinkParryX && qteBarX <= phinkParryX + phinkParryWidth);
            boolean dodged = (qteBarX >= phinkDodgeX && qteBarX <= phinkDodgeX + phinkDodgeWidth);

            if (parried) {
                ui.currentDialogue = "Parry!";
                battleSubState = BATTLE_MESSAGE;
            }
            else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = BATTLE_MESSAGE;
            } else {
                ui.currentDialogue = "Hit!";
                knight.hp -= (currentCharacterEnemy.atk / 2);
                battleSubState = BATTLE_MESSAGE;
            }
            messageDisplayTime = System.nanoTime();
            keyH.spacePressed = false;
        }
        // (ถ้า Phink ชน จะถูก handle ใน update())
    }

    private void handleEnemyPhinkHitExecute() {
        // ถูกเรียกเมื่อ Phink ชน Knight (จาก .update())
        phinkVisible = false;
        phinkDodgeCount--;
        ui.currentDialogue = "Hit!";
        knight.hp -= (currentCharacterEnemy.atk / 2);
        battleSubState = BATTLE_MESSAGE;
        messageDisplayTime = System.nanoTime();
    }

    // +++ แก้ไข: ท่า 3 (Last Stand) Logic ใหม่ +++
    private void handleBattleMessage() {
        // รอ 1.5 วินาที
        if (System.nanoTime() - messageDisplayTime > 1500000000) {

            // +++ ถ้ากำลังสู้กับ Phink +++
            if (phinkDodgeCount > 0 && currentCharacterEnemy instanceof Dermoon && ui.currentDialogue != "Dermoon's Last Stand!") {
                battleSubState = ENEMY_PHINK_CHARGE; // ยิงลูกต่อไป
                messageDisplayTime = System.nanoTime();
                return;
            }

            // --- Logic ใหม่สำหรับ Last Stand ---
            if (ui.currentDialogue.equals("Dermoon's Last Stand!")) {
                // ถ้าข้อความคือ Last Stand ให้รอ 2.5 วิ แล้วเริ่มวิ่งเข้ามา
                if (System.nanoTime() - messageDisplayTime > 2500000000L) {
                    battleSubState = ENEMY_LAST_STAND_MOVE; // +++ เปลี่ยนไป State วิ่ง
                }
                return;
            } else if (ui.currentDialogue.equals("Last Stand Dodge!")) {
                // ถ้าหลบท่าไม้ตายได้
                currentCharacterEnemy.hp = 0; // +++ ฆ่า Dermoon จริงๆ (Dermoon จะหายไป)
                checkBattleEndCondition(); // ไปที่ฉากจบ
                return;
            } else if (ui.currentDialogue.equals("Last Stand Hit!")) {
                // ถ้าโดนท่าไม้ตาย
                knight.hp = 0; // ผู้เล่นตาย
                ((Dermoon) currentCharacterEnemy).currentAction = "zap_fire"; // แสดงท่าโจมตี
                battleSubState = ENEMY_RETURNING;
                messageDisplayTime = System.nanoTime();
                return;
            }
            // --- จบ Logic Last Stand ---

            // (โค้ด "Slime Defeated!" และ "Dermoon Defeated!" เดิม)
            if (ui.currentDialogue.equals("Slime Defeated!")) {
                System.out.println("Switching to Dermoon stage...");
                currentMapImage = map1Image;
                currentBattleBackgroundImage = map1Image;
                dermoon = new Dermoon();
                currentCharacterEnemy = dermoon;
                gameState = playState;
                keyH.enterPressed = false;
            } else if (ui.currentDialogue.equals("Dermoon Defeated!")) {
                gameState = gameClearState;
            } else {
                checkBattleEndCondition(); // <-- ไปเช็คเงื่อนไข (ซึ่งจะสลับเทิร์น)
            }
        }
    }

    private void checkBattleEndCondition() {
        if (!knight.isAlive()) {
            gameState = gameOverState; // แพ้ -> จบเกม
        } else if (!currentCharacterEnemy.isAlive()) {
            // ชนะศัตรูปัจจุบัน!
            if (currentCharacterEnemy instanceof Slime) {
                ui.currentDialogue = "Slime Defeated!";
                messageDisplayTime = System.nanoTime();
            } else if (currentCharacterEnemy instanceof Dermoon) {

                // +++ นี่คือจุดสำคัญ +++
                if (!dermoonLastStandTriggered) {
                    // ถ้ายังไม่ได้ใช้ Last Stand
                    dermoonLastStandTriggered = true; // ตั้งธงว่าใช้แล้ว
                    currentCharacterEnemy.hp = 1; // +++ ชุบชีวิตชั่วคราว (เพื่อให้เขายังคงแสดงผล)

                    ((Dermoon) currentCharacterEnemy).currentAction = "zap_charge"; // ท่าชาร์จ
                    ui.currentDialogue = "Dermoon's Last Stand!"; // แสดงข้อความ
                    battleSubState = BATTLE_MESSAGE; // ไปที่ state แสดงข้อความ
                    messageDisplayTime = System.nanoTime(); // เริ่มจับเวลา (สำหรับข้อความ)
                } else {
                    // ถ้าใช้ Last Stand ไปแล้ว (และผู้เล่นหลบได้)
                    ui.currentDialogue = "Dermoon Defeated!";
                    messageDisplayTime = System.nanoTime();
                }
            }
        } else {
            // ยังไม่มีใครตาย -> สลับเทิร์น
            boolean lastActionWasPlayerAttack = ui.currentDialogue.equals("Success!") || ui.currentDialogue.equals("Miss!");

            if (lastActionWasPlayerAttack) {
                battleSubState = ENEMY_TURN_START;
            } else {
                battleSubState = PLAYER_TURN_START;
                // +++ เพิ่ม: รีเซ็ตท่าทาง Dermoon เมื่อจบเทิร์นของเขา +++
                if (currentCharacterEnemy instanceof Dermoon) {
                    ((Dermoon) currentCharacterEnemy).currentAction = "idle";
                }
            }
        }
    }

    // +++ เพิ่ม Method ใหม่สำหรับท่า Last Stand (วิ่ง) +++
    private void handleEnemyLastStandMove() {
        Dermoon d = (Dermoon) currentCharacterEnemy;
        d.currentAction = "running";
        int targetX = knight.x + 100;

        if (d.x > targetX) {
            d.x -= d.speed;
        } else {
            d.x = targetX;
            // +++ เมื่อมาถึง ให้เปลี่ยนเป็นท่าระเบิด (Zap) +++
            d.currentAction = "zap_fire";
            battleSubState = ENEMY_LAST_STAND_QTE; // เริ่ม QTE
            qteBarX = 400;
        }
    }

    // +++ แก้ไข: ท่า 3 (Last Stand QTE) +++
    private void handleEnemyLastStandQTE() {
        qteBarX += qteBarSpeed;

        if (keyH.spacePressed) {
            // ใช้ lastStandDodgeX, lastStandDodgeWidth (ไม่มี Parry)
            boolean dodged = (qteBarX >= lastStandDodgeX && qteBarX <= lastStandDodgeX + lastStandDodgeWidth);

            if (dodged) {
                ui.currentDialogue = "Last Stand Dodge!";
                knight.currentAction = "dodging";
                battleSubState = PLAYER_DODGING_BACK; // <<< ไป State ถอยหลัง
            } else {
                ui.currentDialogue = "Last Stand Hit!";
                battleSubState = BATTLE_MESSAGE;
            }
            // messageDisplayTime จะถูกตั้งใน handlePlayerDodgingBack หรือ BATTLE_MESSAGE
            keyH.spacePressed = false;
        }

        if (qteBarX > 900) { // เลยโซน
            ui.currentDialogue = "Last Stand Hit!";
            battleSubState = BATTLE_MESSAGE;
            messageDisplayTime = System.nanoTime();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // +++ เพิ่มการวาด Title State +++
        if (gameState == titleState) {
            ui.drawTitleScreen(g2, titleImage); // <<< เรียกใช้เมธอดวาดจาก UI
        }
        // --- ส่วนที่เหลือเหมือนเดิม ---
        else if (gameState == playState) {
            // ... (โค้ดวาด Play State เดิม) ...
            g2.drawImage(currentMapImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(knight.idleImage, knight.x, knight.y, 200, 200, this);
            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()) {
                if (currentCharacterEnemy instanceof Slime) g2.drawImage(slimeImage, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
                else if (currentCharacterEnemy instanceof Dermoon && ((Dermoon) currentCharacterEnemy).zapAnim[0] != null) g2.drawImage(((Dermoon) currentCharacterEnemy).zapAnim[0], currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
            }
        } else if (gameState == battleState && currentCharacterEnemy != null) {
            // ... (โค้ดวาด Battle State เดิม + ส่งค่า QTE Zones) ...
            g2.drawImage(currentBattleBackgroundImage, 0, 0, getWidth(), getHeight(), this);

            // วาดศัตรู
            if (currentCharacterEnemy instanceof Slime) { if (currentCharacterEnemy.hp > 0) g2.drawImage(slimeImage, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this); }
            else if (currentCharacterEnemy instanceof Dermoon) { /* ... โค้ดวาด Dermoon ... */
                 Dermoon d = (Dermoon) currentCharacterEnemy;
                 BufferedImage imageToDraw = d.zapAnim[0];
                 if (d.currentAction.equals("running")) imageToDraw = d.runImage;
                 else if (d.currentAction.equals("run_ready")) imageToDraw = d.runReadyImage;
                 else if (d.currentAction.equals("zap_charge")) imageToDraw = d.zapAnim[1];
                 else if (d.currentAction.equals("zap_fire")) imageToDraw = d.zapAnim[2];
                 else if (d.currentAction.equals("phink_charge_1")) imageToDraw = d.phinkAnim[1];
                 else if (d.currentAction.equals("phink_fire_1")) imageToDraw = d.phinkAnim[2];
                 else if (d.currentAction.equals("phink_charge_2")) imageToDraw = d.phinkAnim[3];
                 else imageToDraw = d.zapAnim[0];
                 if (d.hp > 0) g2.drawImage(imageToDraw, d.x, d.y, 200, 200, this);
            }

            // วาด Phink, Knight, HP Bars
            if (phinkVisible) g2.drawImage(phinkImage, phinkX, phinkY, 100, 100, this);
            BufferedImage imageToDrawKnight = knight.idleImage;
            if (knight.currentAction.equals("ready")) imageToDrawKnight = knight.readyImage;
            if (knight.currentAction.equals("slashing")) imageToDrawKnight = knight.attackImage;
            g2.drawImage(imageToDrawKnight, knight.x, knight.y, 200, 200, this);
            ui.drawHPBar(g2, 50, 50, 400, 40, knight.hp, knight.maxHp);
            g2.setFont(ui.arial_40); g2.setColor(Color.white); g2.drawString(knight.name, 50, 40);
            if(currentCharacterEnemy.hp > 0) ui.drawHPBar(g2, 830, 50, 400, 40, currentCharacterEnemy.hp, currentCharacterEnemy.maxHp);
            else ui.drawHPBar(g2, 830, 50, 400, 40, 0, currentCharacterEnemy.maxHp);
            g2.setFont(ui.arial_40); g2.setColor(Color.white); g2.drawString(currentCharacterEnemy.name, 830, 40);

            // วาด UI Battle ตาม State
            if (battleSubState == PLAYER_TURN_START) ui.drawBattleScreen(g2, knight, currentCharacterEnemy);
            if (battleSubState == PLAYER_ATTACK_QTE) ui.drawAttackQTE(g2, qteBarX, attackSuccessX, attackSuccessWidth);

            // ส่งค่า QTE Zones ให้ UI
            if (battleSubState == PLAYER_DEFENSE_QTE) ui.drawDefenseQTE(g2, qteBarX, defenseDodgeX, defenseDodgeWidth, defenseParryX, defenseParryWidth);
            else if (battleSubState == PLAYER_DEFENSE_QTE_RUN) ui.drawDefenseQTE(g2, qteBarX, runDodgeX, runDodgeWidth, runParryX, runParryWidth);
            else if (battleSubState == PLAYER_DEFENSE_QTE_PHINK) ui.drawDefenseQTE(g2, qteBarX, phinkDodgeX, phinkDodgeWidth, phinkParryX, phinkParryWidth);
            else if (battleSubState == ENEMY_LAST_STAND_QTE) ui.drawDefenseQTE(g2, qteBarX, lastStandDodgeX, lastStandDodgeWidth, lastStandParryX, lastStandParryWidth);

            if (battleSubState == BATTLE_MESSAGE) ui.drawBattleMessage(g2);

        } else if (gameState == gameOverState) {
            // ... (โค้ดวาด Game Over เดิม) ...
            g2.setColor(Color.BLACK); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 80));
            String text = knight.isAlive() ? "You Win!" : "You Lose!";
            int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
        } else if (gameState == gameClearState) {
            // ... (โค้ดวาด Game Clear เดิม) ...
            g2.setColor(Color.BLACK); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 80));
            String text = "Game Clear!";
            int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
        }

        g2.dispose();
    }
} // ปิดคลาส GamePanel
