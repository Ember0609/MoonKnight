import java.awt.*;
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
    public final int qteTurn = 1;
    public final int enemyTurn = 2;
    public final int messageTurn = 3;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    UI ui = new UI();
    
    Knight knight = new Knight();
    Slime slime = new Slime();
    
    Image backgroundImage, knightImage, slimeImage, battleBackgroundImage;

    // QTE Variables
    int qteBarX;
    int qteBarSpeed = 8;
    long messageDisplayTime;

    public GamePanel() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        backgroundImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage();
        knightImage = new ImageIcon(getClass().getResource("Picture/MainFront.png")).getImage();
        slimeImage = new ImageIcon(getClass().getResource("Picture/Slimekung.png")).getImage();
        battleBackgroundImage = new ImageIcon(getClass().getResource("Picture/Map1.png")).getImage(); // เปลี่ยนเป็นพื้นหลังที่ต้องการ

        gameState = playState;
    }

    public void setupGame() {
        gameState = playState;
        // สามารถเพิ่มการ reset ค่าตัวละครตรงนี้ได้ถ้าต้องการเริ่มเกมใหม่
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() { /* Game Loop เหมือนเดิม */ 
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
            knight.update(keyH);
            if (knight.solidArea.intersects(slime.solidArea)) {
                gameState = battleState;
                battleSubState = playerTurn; // เริ่มที่เทิร์นผู้เล่น
            }
        }
        if (gameState == battleState) {
            handleBattle();
        }
    }

    public void handleBattle() {
        switch (battleSubState) {
            case playerTurn:
                handlePlayerTurn();
                break;
            case qteTurn:
                handleQTETurn();
                break;
            case enemyTurn:
                handleEnemyTurn();
                break;
            case messageTurn:
                handleMessageTurn();
                break;
        }
    }

    private void handlePlayerTurn() {
        if (keyH.upPressed) { ui.commandNum = 0; keyH.upPressed = false; }
        if (keyH.downPressed) { ui.commandNum = 1; keyH.downPressed = false; }
        if (keyH.enterPressed) {
            if (ui.commandNum == 0) { // Attack
                battleSubState = qteTurn;
                qteBarX = 400; // Reset ตำแหน่ง QTE bar
            }
            if (ui.commandNum == 1) { // Skip
                battleSubState = enemyTurn;
            }
            keyH.enterPressed = false;
        }
    }

    private void handleQTETurn() {
        qteBarX += qteBarSpeed;
        if (keyH.spacePressed) {
            // เช็คว่ากดโดน Success Zone (680-760) หรือไม่
            if (qteBarX >= 680 && qteBarX <= 760) {
                knight.attack(slime);
                ui.currentDialogue = "Success!";
            } else {
                ui.currentDialogue = "Miss!";
            }
            keyH.spacePressed = false;
            battleSubState = messageTurn;
            messageDisplayTime = System.nanoTime();
        }
        if (qteBarX > 900) { // ถ้าปล่อยเลยโซนไป
            ui.currentDialogue = "Miss!";
            battleSubState = messageTurn;
            messageDisplayTime = System.nanoTime();
        }
    }

    private void handleEnemyTurn() {
        if (slime.isAlive()) {
            slime.attack(knight);
            ui.currentDialogue = "Slime attacks!";
            if (!knight.isAlive()) { gameState = gameOverState; }
        } else {
            gameState = gameOverState; // ชนะถ้า Slime ตายไปแล้ว
        }
        battleSubState = messageTurn;
        messageDisplayTime = System.nanoTime();
    }
    
    private void handleMessageTurn() {
        // แสดงข้อความประมาณ 1.5 วินาที
        if(System.nanoTime() - messageDisplayTime > 1500000000) {
            // เช็คว่าใครตายหรือยัง
            if(!knight.isAlive() || !slime.isAlive()) {
                gameState = gameOverState;
            } else {
                // กลับไปที่เทิร์นผู้เล่นหรือเทิร์นศัตรู
                if(ui.currentDialogue.contains("Slime")) {
                     battleSubState = playerTurn;
                } else {
                     battleSubState = enemyTurn;
                }
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == playState) {
            // วาดฉากสำรวจ
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(knightImage, knight.x, knight.y, 150, 150, this);
            g2.drawImage(slimeImage, slime.x, slime.y, 120, 120, this);
        } else if (gameState == battleState) {
            // วาดฉากต่อสู้
            g2.drawImage(battleBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawString("Knight HP: " + knight.hp, 50, 50);
            g2.drawString("Slime HP: " + slime.hp, 900, 50);
            
            if(battleSubState == playerTurn) ui.draw(g2);
            if(battleSubState == qteTurn) ui.drawAttackQTE(g2, qteBarX);
            if(battleSubState == messageTurn) ui.drawBattleMessage(g2);
            
        } else if (gameState == gameOverState) {
            // วาดฉากจบเกม
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