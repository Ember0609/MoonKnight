import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
public class GamePanel extends JPanel implements Runnable {

    public GameState gameState;
    public BattleSubState battleSubState;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    UI ui = new UI();

    Knight knight = new Knight();
    Slime slime = new Slime();

    Dermoon dermoon = null;
    Character currentCharacterEnemy;

    Image map1Image, map2Image, currentMapImage, currentBattleBackgroundImage;
    Image slimeImage;
    Image phinkImage;
    Image titleImage;

    int qteBarX, qteBarSpeed = 8;
    long messageDisplayTime;
    int dermoonAttackChoice = 0;
    int phinkDodgeCount = 0;
    int phinkX, phinkY, phinkSpeed = 25;
    boolean phinkVisible = false;
    boolean dermoonLastStandTriggered = false;

    int attackSuccessX = 680;
    int attackSuccessWidth = 80;
    int defenseDodgeX = 600;
    int defenseDodgeWidth = 200;
    int defenseParryX = 680;
    int defenseParryWidth = 80;
    int runDodgeX = 620;
    int runDodgeWidth = 180;
    int runParryX = 690;
    int runParryWidth = 60;
    int phinkDodgeX = 580;
    int phinkDodgeWidth = 240;
    int phinkParryX = 700;
    int phinkParryWidth = 40;
    int lastStandDodgeX = 650;
    int lastStandDodgeWidth = 70;
    int lastStandParryX = 0;
    int lastStandParryWidth = 0;

    
    
    public GamePanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        map1Image = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();
        map2Image = new ImageIcon(getClass().getResource("Picture/Map2.png")).getImage();
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage();
        phinkImage = new ImageIcon(getClass().getResource("Picture/Phink.png")).getImage();
        titleImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();

        currentMapImage = map2Image;
        currentBattleBackgroundImage = map2Image;
        currentCharacterEnemy = slime;
        
        gameState = GameState.TITLE;
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
        if (gameState == GameState.TITLE) {
            handleTitleState();
        }
        else if (gameState == GameState.PLAY) {
            knight.updateForWorld(keyH);
            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()
                    && knight.solidArea.intersects(currentCharacterEnemy.solidArea)) {
                
                gameState = GameState.BATTLE;
                battleSubState = BattleSubState.PLAYER_TURN_START;
                
                knight.x = knight.originalX;
                knight.y = knight.originalY;
                currentCharacterEnemy.x = currentCharacterEnemy.originalX;
                currentCharacterEnemy.y = currentCharacterEnemy.originalY;
            }
        }
       else if (gameState == GameState.BATTLE) {
            knight.updateForBattle();
            if (currentCharacterEnemy != null) {
                currentCharacterEnemy.updateForBattle();
            }

            if (phinkVisible) {
                phinkX -= phinkSpeed;
                 if (phinkX < knight.x + 50) {
                     if (battleSubState == BattleSubState.PLAYER_DEFENSE_QTE_PHINK) {
                         battleSubState = BattleSubState.ENEMY_PHINK_HIT_EXECUTE;
                     }
                 }
            }
            handleBattle();
        }
    }

    private void handleTitleState() {
        if (keyH.upPressed) {
            ui.commandNum--;
            if (ui.commandNum < 0) ui.commandNum = 1;
            keyH.upPressed = false;
        }
        if (keyH.downPressed) {
            ui.commandNum++;
            if (ui.commandNum > 1) ui.commandNum = 0;
            keyH.downPressed = false;
        }
        if (keyH.enterPressed) {
            if (ui.commandNum == 0) {
                gameState = GameState.PLAY;
            } else if (ui.commandNum == 1) {
                System.exit(0);
            }
             keyH.enterPressed = false;
        }
    }

    public void handleBattle() {
        switch (battleSubState) {
            case PLAYER_TURN_START:         handlePlayerTurnStart(); break;
            case PLAYER_MOVING_TO_TARGET:   handlePlayerMovingToTarget(); break;
            case PLAYER_ATTACK_QTE:         handlePlayerAttackQTE(); break;
            case PLAYER_SLASHING:           handlePlayerSlashing(); break;
            case PLAYER_RETURNING:          handlePlayerReturning(); break;
            case ENEMY_TURN_START:          handleEnemyTurnStart(); break;
            case ENEMY_MOVING_TO_TARGET:    handleEnemyMovingToTarget(); break;
            case PLAYER_DEFENSE_QTE:        handlePlayerDefenseQTE(); break;
            case PLAYER_DODGING_BACK:       handlePlayerDodgingBack(); break;
            case ENEMY_ATTACK_EXECUTE:      handleEnemyAttackExecute(); break;
            case ENEMY_RETURNING:           handleEnemyReturning(); break;
            case PLAYER_RETURNING_FROM_DODGE: handlePlayerReturningFromDodge(); break;
            case BATTLE_MESSAGE:            handleBattleMessage(); break;
            case ENEMY_ATTACK_ANIMATION:    handleEnemyAttackAnimation(); break;
            case ENEMY_RUN_MOVE:            handleEnemyRunMove(); break;
            case PLAYER_DEFENSE_QTE_RUN:    handlePlayerDefenseQTERun(); break;
            case ENEMY_ATTACK_EXECUTE_RUN:  handleEnemyAttackExecuteRun(); break;
            case ENEMY_PHINK_CHARGE:        handleEnemyPhinkCharge(); break;
            case PLAYER_DEFENSE_QTE_PHINK:  handlePlayerDefenseQTEPhink(); break;
            case ENEMY_PHINK_HIT_EXECUTE:   handleEnemyPhinkHitExecute(); break;
            case ENEMY_LAST_STAND_QTE:      handleEnemyLastStandQTE(); break;
            case ENEMY_LAST_STAND_MOVE:     handleEnemyLastStandMove(); break;
        }
    }

    private void handlePlayerTurnStart() {
        knight.currentAction = "idle";
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
                battleSubState = BattleSubState.PLAYER_MOVING_TO_TARGET;
            } else { // Skip
                battleSubState = BattleSubState.ENEMY_TURN_START;
            }
            keyH.enterPressed = false;
        }
    }

    private void handlePlayerMovingToTarget() {
        int targetX = currentCharacterEnemy.x - 100;
        if (knight.x < targetX) {
            knight.x += knight.speed;
        } else {
            knight.x = targetX;
            knight.currentAction = "ready";
            battleSubState = BattleSubState.PLAYER_ATTACK_QTE;
            qteBarX = 400;
        }
    }

    private void handlePlayerAttackQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (qteBarX >= 680 && qteBarX <= 760) {
                knight.attack(currentCharacterEnemy);
                ui.currentDialogue = "Success!";
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = BattleSubState.PLAYER_SLASHING;
            } else {
                ui.currentDialogue = "Miss!";
                knight.currentAction = "idle";
                battleSubState = BattleSubState.PLAYER_RETURNING;
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) {
            ui.currentDialogue = "Miss!";
            knight.currentAction = "idle";
            battleSubState = BattleSubState.PLAYER_RETURNING;
        }
    }

    private void handlePlayerSlashing() {
        if (knight.spriteNum > 1) {
            battleSubState = BattleSubState.PLAYER_RETURNING;
        }
    }

    private void handlePlayerReturning() {
        if (knight.x > knight.originalX) {
            knight.x -= knight.speed;
        } else {
            knight.x = knight.originalX;
            knight.currentAction = "idle";
            battleSubState = BattleSubState.BATTLE_MESSAGE;
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleEnemyTurnStart() {
        if (currentCharacterEnemy.isAlive()) {
            currentCharacterEnemy.performTurn(this);
        } else {
            checkBattleEndCondition();
        }
    }

    private void handleEnemyMovingToTarget() {
        int targetX = knight.x + 100;
        if (currentCharacterEnemy.x > targetX) {
            currentCharacterEnemy.x -= currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = targetX;
            battleSubState = BattleSubState.PLAYER_DEFENSE_QTE;
            qteBarX = 400;
        }
    }

    private void handlePlayerDefenseQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            boolean parried = (qteBarX >= defenseParryX && qteBarX <= defenseParryX + defenseParryWidth);
            boolean dodged = (qteBarX >= defenseDodgeX && qteBarX <= defenseDodgeX + defenseDodgeWidth);
            if (parried) {
                ui.currentDialogue = "Parry!";
                knight.attack(currentCharacterEnemy);
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = BattleSubState.ENEMY_RETURNING;
            } else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = BattleSubState.PLAYER_DODGING_BACK;
            } else {
                ui.currentDialogue = "Hit!";
                battleSubState = BattleSubState.ENEMY_ATTACK_EXECUTE;
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) {
            ui.currentDialogue = "Hit!";
            battleSubState = BattleSubState.ENEMY_ATTACK_EXECUTE;
        }
    }

    private void handlePlayerDodgingBack() {
        if (knight.x > knight.dodgeTargetX) {
            knight.x -= knight.speed / 2;
        } else {
            knight.x = knight.dodgeTargetX;
            if (dermoonLastStandTriggered && ui.currentDialogue.equals("Last Stand Dodge!")) {
                currentCharacterEnemy.hp = 0;
                battleSubState = BattleSubState.BATTLE_MESSAGE;
                messageDisplayTime = System.nanoTime();
            } else {
                battleSubState = BattleSubState.ENEMY_RETURNING;
            }
        }
    }

    private void handleEnemyAttackExecute() {
        currentCharacterEnemy.attack(knight);
        battleSubState = BattleSubState.ENEMY_RETURNING;
    }

    private void handleEnemyAttackAnimation() {
        if (System.nanoTime() - messageDisplayTime > 500000000) {
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon) currentCharacterEnemy).currentAction = "running";
            }
            battleSubState = BattleSubState.ENEMY_RETURNING;
        }
    }

    private void handleEnemyReturning() {
        if (currentCharacterEnemy instanceof Dermoon) {
            ((Dermoon) currentCharacterEnemy).currentAction = "running";
        }

        if (currentCharacterEnemy.x < currentCharacterEnemy.originalX) {
            currentCharacterEnemy.x += currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = currentCharacterEnemy.originalX;
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon) currentCharacterEnemy).currentAction = "idle";
            }
            if (knight.currentAction.equals("dodging")) {
                battleSubState = BattleSubState.PLAYER_RETURNING_FROM_DODGE;
            } else {
                battleSubState = BattleSubState.BATTLE_MESSAGE;
                messageDisplayTime = System.nanoTime();
            }
        }
    }

    private void handleEnemyRunMove() {
        ((Dermoon) currentCharacterEnemy).currentAction = "running";
        int targetX = knight.x + 100;
        if (currentCharacterEnemy.x > targetX) {
            currentCharacterEnemy.x -= currentCharacterEnemy.speed;
        } else {
            currentCharacterEnemy.x = targetX;
            ((Dermoon) currentCharacterEnemy).currentAction = "run_ready";
            battleSubState = BattleSubState.PLAYER_DEFENSE_QTE_RUN;
            qteBarX = 400;
        }
    }

    private void handlePlayerDefenseQTERun() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon)currentCharacterEnemy).currentAction = "running";
            }
            boolean parried = (qteBarX >= runParryX && qteBarX <= runParryX + runParryWidth);
            boolean dodged = (qteBarX >= runDodgeX && qteBarX <= runDodgeX + runDodgeWidth);
            if (parried) {
                ui.currentDialogue = "Parry!";
                knight.attack(currentCharacterEnemy);
                knight.currentAction = "slashing";
                knight.spriteNum = 1;
                battleSubState = BattleSubState.ENEMY_RETURNING;
            } else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = BattleSubState.PLAYER_DODGING_BACK;
            } else {
                ui.currentDialogue = "Hit!";
                battleSubState = BattleSubState.ENEMY_ATTACK_EXECUTE_RUN;
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) {
             if (currentCharacterEnemy instanceof Dermoon) {
                ((Dermoon)currentCharacterEnemy).currentAction = "running";
            }
            ui.currentDialogue = "Hit!";
            battleSubState = BattleSubState.ENEMY_ATTACK_EXECUTE_RUN;
        }
    }

    private void handleEnemyAttackExecuteRun() {
        currentCharacterEnemy.attack(knight);
        battleSubState = BattleSubState.ENEMY_RETURNING;
    }

    private void handlePlayerReturningFromDodge() {
        if (knight.x < knight.originalX) {
            knight.x += knight.speed / 2;
        } else {
            knight.x = knight.originalX;
            knight.currentAction = "idle";
            battleSubState = BattleSubState.BATTLE_MESSAGE;
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleEnemyPhinkCharge() {
        Dermoon d = (Dermoon) currentCharacterEnemy;
        String chargeAction = "";
        String fireAction = "";
        if (phinkDodgeCount == 2) {
            chargeAction = "phink_charge_1";
            fireAction = "phink_fire_1";
        } else if (phinkDodgeCount == 1) {
            chargeAction = "phink_fire_1";
            fireAction = "phink_charge_2";
        }
        d.currentAction = chargeAction;
        if (System.nanoTime() - messageDisplayTime > 1000000000) {
            d.currentAction = fireAction;
            phinkVisible = true;
            phinkX = currentCharacterEnemy.x - 50;
            phinkY = currentCharacterEnemy.y + 70;
            battleSubState = BattleSubState.PLAYER_DEFENSE_QTE_PHINK;
            qteBarX = 400;
        }
    }

    private void handlePlayerDefenseQTEPhink() {
        qteBarX += 25;
        if (keyH.spacePressed) {
            phinkVisible = false;
            phinkDodgeCount--;
            boolean parried = (qteBarX >= phinkParryX && qteBarX <= phinkParryX + phinkParryWidth);
            boolean dodged = (qteBarX >= phinkDodgeX && qteBarX <= phinkDodgeX + phinkDodgeWidth);
            if (parried) {
                ui.currentDialogue = "Parry!";
                battleSubState = BattleSubState.BATTLE_MESSAGE;
            }
            else if (dodged) {
                ui.currentDialogue = "Dodge!";
                knight.currentAction = "dodging";
                battleSubState = BattleSubState.BATTLE_MESSAGE;
            } else {
                ui.currentDialogue = "Hit!";
                knight.hp -= (currentCharacterEnemy.atk / 2);
                battleSubState = BattleSubState.BATTLE_MESSAGE;
            }
            messageDisplayTime = System.nanoTime();
            keyH.spacePressed = false;
        }
    }

    private void handleEnemyPhinkHitExecute() {
        phinkVisible = false;
        phinkDodgeCount--;
        ui.currentDialogue = "Hit!";
        knight.hp -= (currentCharacterEnemy.atk / 2);
        battleSubState = BattleSubState.BATTLE_MESSAGE;
        messageDisplayTime = System.nanoTime();
    }

    private void handleBattleMessage() {
        if (System.nanoTime() - messageDisplayTime > 1500000000) {
            if (phinkDodgeCount > 0 && currentCharacterEnemy instanceof Dermoon && ui.currentDialogue != "Dermoon's Last Stand!") {
                battleSubState = BattleSubState.ENEMY_PHINK_CHARGE;
                messageDisplayTime = System.nanoTime();
                return;
            }
            if (ui.currentDialogue.equals("Dermoon's Last Stand!")) {
                if (System.nanoTime() - messageDisplayTime > 2500000000L) {
                    battleSubState = BattleSubState.ENEMY_LAST_STAND_MOVE;
                }
                return;
            } else if (ui.currentDialogue.equals("Last Stand Dodge!")) {
                currentCharacterEnemy.hp = 0;
                checkBattleEndCondition();
                return;
            } else if (ui.currentDialogue.equals("Last Stand Hit!")) {
                knight.hp = 0;
                ((Dermoon) currentCharacterEnemy).currentAction = "zap_fire";
                battleSubState = BattleSubState.ENEMY_RETURNING;
                messageDisplayTime = System.nanoTime();
                return;
            }
            if (ui.currentDialogue.equals("Slime Defeated!")) {
                currentMapImage = map1Image;
                currentBattleBackgroundImage = map1Image;
                dermoon = new Dermoon();
                currentCharacterEnemy = dermoon;
                gameState = GameState.PLAY;
                keyH.enterPressed = false;
            } else if (ui.currentDialogue.equals("Dermoon Defeated!")) {
                gameState = GameState.GAME_CLEAR;
            } else {
                checkBattleEndCondition();
            }
        }
    }

    private void checkBattleEndCondition() {
        if (!knight.isAlive()) {
            gameState = GameState.GAME_OVER;
        } else if (!currentCharacterEnemy.isAlive()) {
            if (currentCharacterEnemy instanceof Slime) {
                ui.currentDialogue = "Slime Defeated!";
                messageDisplayTime = System.nanoTime();
            } else if (currentCharacterEnemy instanceof Dermoon) {
                if (!dermoonLastStandTriggered) {
                    dermoonLastStandTriggered = true;
                    currentCharacterEnemy.hp = 1;
                    ((Dermoon) currentCharacterEnemy).currentAction = "zap_charge";
                    ui.currentDialogue = "Dermoon's Last Stand!";
                    battleSubState = BattleSubState.BATTLE_MESSAGE;
                    messageDisplayTime = System.nanoTime();
                } else {
                    ui.currentDialogue = "Dermoon Defeated!";
                    messageDisplayTime = System.nanoTime();
                }
            }
        } else {
            boolean lastActionWasPlayerAttack = ui.currentDialogue.equals("Success!") || ui.currentDialogue.equals("Miss!");
            if (lastActionWasPlayerAttack) {
                battleSubState = BattleSubState.ENEMY_TURN_START;
            } else {
                battleSubState = BattleSubState.PLAYER_TURN_START;
                if (currentCharacterEnemy instanceof Dermoon) {
                    ((Dermoon) currentCharacterEnemy).currentAction = "idle";
                }
            }
        }
    }

    private void handleEnemyLastStandMove() {
        Dermoon d = (Dermoon) currentCharacterEnemy;
        d.currentAction = "running";
        int targetX = knight.x + 100;
        if (d.x > targetX) {
            d.x -= d.speed;
        } else {
            d.x = targetX;
            d.currentAction = "zap_fire";
            battleSubState = BattleSubState.ENEMY_LAST_STAND_QTE;
            qteBarX = 400;
        }
    }

    private void handleEnemyLastStandQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            boolean dodged = (qteBarX >= lastStandDodgeX && qteBarX <= lastStandDodgeX + lastStandDodgeWidth);
            if (dodged) {
                ui.currentDialogue = "Last Stand Dodge!";
                knight.currentAction = "dodging";
                battleSubState = BattleSubState.PLAYER_DODGING_BACK;
            } else {
                ui.currentDialogue = "Last Stand Hit!";
                battleSubState = BattleSubState.BATTLE_MESSAGE;
            }
            keyH.spacePressed = false;
        }
        if (qteBarX > 900) {
            ui.currentDialogue = "Last Stand Hit!";
            battleSubState = BattleSubState.BATTLE_MESSAGE;
            messageDisplayTime = System.nanoTime();
        }
    }


    // +++ อัปเดตเมธอด paintComponent ทั้งหมด +++
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == GameState.TITLE) {
            ui.drawTitleScreen(g2, titleImage);
        }
        else if (gameState == GameState.PLAY) {
            g2.drawImage(currentMapImage, 0, 0, getWidth(), getHeight(), this);
            
            // +++ อัปเดตการวาด Knight ใน PLAY state +++
            BufferedImage knightImgPlay = knight.getCurrentImage();
            if (knightImgPlay != null) {
                g2.drawImage(knightImgPlay, knight.x, knight.y, 200, 200, this);
            }
            // --- จบส่วนอัปเดต ---

            if (currentCharacterEnemy != null && currentCharacterEnemy.isAlive()) {
                BufferedImage enemyImgPlay = currentCharacterEnemy.getCurrentImage();
                if (enemyImgPlay != null) {
                    g2.drawImage(enemyImgPlay, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
                }
            }
        } 
        else if (gameState == GameState.BATTLE && currentCharacterEnemy != null) {
            g2.drawImage(currentBattleBackgroundImage, 0, 0, getWidth(), getHeight(), this);

            // วาดศัตรู (Polymorphism)
            if (currentCharacterEnemy.hp > 0) {
                BufferedImage imageToDraw = currentCharacterEnemy.getCurrentImage();
                if (imageToDraw != null) {
                    g2.drawImage(imageToDraw, currentCharacterEnemy.x, currentCharacterEnemy.y, 200, 200, this);
                }
            }

            // วาด Phink
            if (phinkVisible) g2.drawImage(phinkImage, phinkX, phinkY, 100, 100, this);
            
            // +++ อัปเดตการวาด Knight ใน BATTLE state (เพิ่ม Null Check) +++
            BufferedImage knightImgBattle = knight.getCurrentImage();
            if (knightImgBattle != null) {
                g2.drawImage(knightImgBattle, knight.x, knight.y, 200, 200, this);
            }
            // --- จบส่วนอัปเดต ---
            
            // วาด HP Bars
            ui.drawHPBar(g2, 50, 50, 400, 40, knight.hp, knight.maxHp);
            g2.setFont(ui.arial_40); g2.setColor(Color.white); g2.drawString(knight.name, 50, 40);
            if(currentCharacterEnemy.hp > 0) ui.drawHPBar(g2, 830, 50, 400, 40, currentCharacterEnemy.hp, currentCharacterEnemy.maxHp);
            else ui.drawHPBar(g2, 830, 50, 400, 40, 0, currentCharacterEnemy.maxHp);
            g2.setFont(ui.arial_40); g2.setColor(Color.white); g2.drawString(currentCharacterEnemy.name, 830, 40);

            // วาด UI Battle ตาม State
            if (battleSubState == BattleSubState.PLAYER_TURN_START) ui.drawBattleScreen(g2, knight, currentCharacterEnemy);
            if (battleSubState == BattleSubState.PLAYER_ATTACK_QTE) ui.drawAttackQTE(g2, qteBarX, attackSuccessX, attackSuccessWidth);
            if (battleSubState == BattleSubState.PLAYER_DEFENSE_QTE) ui.drawDefenseQTE(g2, qteBarX, defenseDodgeX, defenseDodgeWidth, defenseParryX, defenseParryWidth);
            else if (battleSubState == BattleSubState.PLAYER_DEFENSE_QTE_RUN) ui.drawDefenseQTE(g2, qteBarX, runDodgeX, runDodgeWidth, runParryX, runParryWidth);
            else if (battleSubState == BattleSubState.PLAYER_DEFENSE_QTE_PHINK) ui.drawDefenseQTE(g2, qteBarX, phinkDodgeX, phinkDodgeWidth, phinkParryX, phinkParryWidth);
            else if (battleSubState == BattleSubState.ENEMY_LAST_STAND_QTE) ui.drawDefenseQTE(g2, qteBarX, lastStandDodgeX, lastStandDodgeWidth, lastStandParryX, lastStandParryWidth);
            if (battleSubState == BattleSubState.BATTLE_MESSAGE) ui.drawBattleMessage(g2);

        } else if (gameState == GameState.GAME_OVER) {
            g2.setColor(Color.BLACK); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 80));
            String text = knight.isAlive() ? "You Win!" : "You Lose!";
            int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
        } else if (gameState == GameState.GAME_CLEAR) {
            g2.setColor(Color.BLACK); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 80));
            String text = "Game Clear!";
            int textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            g2.drawString(text, (getWidth() - textLength) / 2, getHeight() / 2);
        }

        g2.dispose();
    }
}