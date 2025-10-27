
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    // Game States
    public int gameState;
    public final int playState = 1;
    public final int battleState = 2;
    public final int gameOverState = 3;

    // Battle Sub-States
    public int battleSubState;
    public final int playerTurn = 0;
    public final int movingToTarget = 1;
    public final int attackQTE = 2;
    public final int slashing = 3;
    public final int returning = 4;
    public final int enemyTurn = 5;
    public final int defenseQTE = 6;
    public final int messageTurn = 7;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    UI ui = new UI();

    Knight knight = new Knight();
    Slime slime = new Slime();

    Image backgroundImage, slimeImage, battleBackgroundImage;

    int qteBarX, qteBarSpeed = 8;
    long messageDisplayTime;

    public GamePanel() {
        // ... (Constructor เหมือนเดิม)
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        backgroundImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage();
        battleBackgroundImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();
        gameState = playState;
    }

    public void startGameThread() {
        /* ... เหมือนเดิม ... */
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        /* ... Game Loop เหมือนเดิม ... */
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
                battleSubState = playerTurn;

                knight.x = knight.originalX;
                knight.y = 490;
            }
        }
        if (gameState == battleState) {
            knight.updateForBattle();
            handleBattle();
        }
    }

    public void handleBattle() {
        switch (battleSubState) {
            case playerTurn:
                handlePlayerTurn();
                break;
            case movingToTarget:
                handleMovingToTarget();
                break;
            case attackQTE:
                handleAttackQTE();
                break;
            case slashing:
                handleSlashing();
                break;
            case returning:
                handleReturning();
                break;
            case enemyTurn:
                handleEnemyTurn();
                break;
            case defenseQTE:
                handleDefenseQTE();
                break;
            case messageTurn:
                handleMessageTurn();
                break;
        }
    }

    private void handlePlayerTurn() {
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
                battleSubState = movingToTarget;
            } else { // Skip
                battleSubState = enemyTurn;
            }
            keyH.enterPressed = false;
        }
    }

    private void handleMovingToTarget() {
        int targetX = slime.x - 100; // ตำแหน่งเป้าหมายหน้า Slime
        if (knight.x < targetX) {
            knight.x += knight.speed;
        } else {
            knight.x = targetX;
            knight.currentAction = "ready";
            battleSubState = attackQTE;
            qteBarX = 400; // Reset QTE bar
        }
    }

    private void handleAttackQTE() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (qteBarX >= 680 && qteBarX <= 760) {
                knight.attack(slime);
                ui.currentDialogue = "Success!";
                knight.currentAction = "slashing";
                knight.spriteNum = 1; // เริ่มนับ frame ใหม่
            } else {
                ui.currentDialogue = "Miss!";
                knight.currentAction = "idle"; // กลับท่ายืนเฉย
            }
            keyH.spacePressed = false;
            battleSubState = (knight.currentAction.equals("slashing")) ? slashing : returning;
        }
        if (qteBarX > 900) {
            ui.currentDialogue = "Miss!";
            knight.currentAction = "idle";
            battleSubState = returning;
        }
    }

    private void handleSlashing() {
        // รอให้ animation ฟันเล่นจบ (สมมติว่ามี 1 frame)
        if (knight.spriteNum > 1) { // ถ้า spriteNum ถูกนับไปถึง 2 แล้ว
            battleSubState = returning;
        }
    }

    private void handleReturning() {
        if (knight.x > knight.originalX) {
            knight.x -= knight.speed;
        } else {
            knight.x = knight.originalX;
            knight.currentAction = "idle";
            battleSubState = messageTurn;
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleEnemyTurn() {
        /* ... เหมือนเดิม ... */
        if (slime.isAlive()) {
            battleSubState = defenseQTE;
            qteBarX = 400;
        } else {
            gameState = gameOverState;
        }
    }

    private void handleDefenseQTE() {
        /* ... เหมือนเดิม ... */
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            if (qteBarX >= 680 && qteBarX <= 760) {
                ui.currentDialogue = "Parry!";
                knight.attack(slime);
            } else if (qteBarX >= 600 && qteBarX <= 800) {
                ui.currentDialogue = "Dodge!";
            } else {
                ui.currentDialogue = "Hit!";
                slime.attack(knight);
            }
            keyH.spacePressed = false;
            battleSubState = messageTurn;
            messageDisplayTime = System.nanoTime();
        }
        if (qteBarX > 900) {
            ui.currentDialogue = "Hit!";
            slime.attack(knight);
            battleSubState = messageTurn;
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleMessageTurn() {
        if (System.nanoTime() - messageDisplayTime > 1500000000) {
            if (!knight.isAlive() || !slime.isAlive()) {
                gameState = gameOverState;
            } else {
                if (ui.currentDialogue.equals("Success!") || ui.currentDialogue.equals("Miss!")) {
                    battleSubState = enemyTurn;
                } else {
                    battleSubState = playerTurn;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == playState) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(knight.idleImage, knight.x, knight.y, 200, 200, this);
            g2.drawImage(slimeImage, slime.x, slime.y, 200, 200, this);
        } else if (gameState == battleState) {
            // ... (ส่วนวาดพื้นหลัง, HP, Slime เหมือนเดิม)
            g2.drawImage(battleBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawString("Knight HP: " + knight.hp, 50, 50);
            g2.drawString("Slime HP: " + slime.hp, 900, 50);
            g2.drawImage(slimeImage, 800, 490, 200, 200, this);

            // --- วาด Knight ตาม Action และตำแหน่งที่เปลี่ยนไป ---
            BufferedImage imageToDraw = knight.idleImage;
            switch (knight.currentAction) {
                case "ready":
                    imageToDraw = knight.readyImage;
                    break;
                case "slashing":
                    imageToDraw = knight.attackImage;
                    break;
            }
            g2.drawImage(imageToDraw, knight.x, knight.y, 200, 200, this);

            // --- วาด UI ตามสถานะ ---
            if (battleSubState == playerTurn) {
                ui.draw(g2);
            }
            if (battleSubState == attackQTE) {
                ui.drawAttackQTE(g2, qteBarX);
            }
            if (battleSubState == defenseQTE) {
                ui.drawDefenseQTE(g2, qteBarX);
            }
            if (battleSubState == messageTurn) {
                ui.drawBattleMessage(g2);
            }

        } else if (gameState == gameOverState) {
            // ... (เหมือนเดิม)
        }

        g2.dispose();
    }
}
