
import java.awt.*;

public class UI {

    Font arial_40;
    public int commandNum = 0; 
    public String currentDialogue = "";

    public UI() {
        arial_40 = new Font("Arial", Font.PLAIN, 40);
    }

    public void drawTitleScreen(Graphics2D g2, Image titleImage) {
        g2.drawImage(titleImage, 0, 0, 1280, 720, null); 
        g2.setFont(arial_40.deriveFont(Font.BOLD, 80F));
        g2.setColor(Color.WHITE);
        g2.drawString("MoonKnight", 450, 170);
        g2.setFont(arial_40);
        
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.fillOval(530, 300, 220, 100);
        g2.fillOval(530, 440, 220, 100);
        g2.setColor(Color.WHITE);
        g2.drawString("Start", 600, 360); // ลองปรับ X, Y ดู
        if (commandNum == 0) {
            g2.drawString(">", 560, 370); // ตัวเลือก '>'
        }

        g2.drawString("Exit", 600, 500); // ลองปรับ X, Y ดู
        if (commandNum == 1) {
            g2.drawString(">", 560, 500); // ตัวเลือก '>'
        }
    }

    private int getXforCenteredText(Graphics2D g2, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = 1280 / 2 - length / 2; // 1280 คือความกว้างจอ
        return x;
    }

    public void drawHPBar(Graphics2D g2, int x, int y, int width, int height, int currentHP, int maxHP) {
        double hpPercentage = (double) currentHP / maxHP;
        if (hpPercentage < 0) {
            hpPercentage = 0;
        
        }int currentHPBarWidth = (int) (width * hpPercentage);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.RED);
        g2.fillRect(x, y, currentHPBarWidth, height);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width, height);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
        String hpText = currentHP + "/" + maxHP;
        int textX = x + width / 2 - (int) (g2.getFontMetrics().getStringBounds(hpText, g2).getWidth() / 2);
        int textY = y + height / 2 + 7;
        g2.setColor(Color.WHITE);
        g2.drawString(hpText, textX, textY);
    }

    public void drawBattleScreen(Graphics2D g2, Knight knight, Character enemy) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(50, 300, 300, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(50, 300, 300, 150, 35, 35);
        g2.setFont(arial_40);
        g2.setColor(Color.white);
        g2.drawString("Attack", 75, 360);
        if (commandNum == 0) {
            g2.drawString(">", 50, 360);
        
        }g2.drawString("Skip", 75, 410);
        if (commandNum == 1) {
            g2.drawString(">", 50, 410);
        }
    }

    public void drawAttackQTE(Graphics2D g2, int qteBarX, int successZoneX, int successZoneWidth) {
        int bgX = 400, bgWidth = 500, bgY = 300, bgHeight = 50;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(bgX, bgY, bgWidth, bgHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(successZoneX, bgY, successZoneWidth, bgHeight);
        g2.setColor(Color.RED);
        g2.fillRect(qteBarX, bgY, 15, bgHeight);
    }

    public void drawDefenseQTE(Graphics2D g2, int qteBarX, int dodgeZoneX, int dodgeZoneWidth, int parryZoneX, int parryZoneWidth) {
        int bgX = 400, bgWidth = 500, bgY = 300, bgHeight = 50;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(bgX, bgY, bgWidth, bgHeight);
        g2.setColor(Color.CYAN);
        g2.fillRect(dodgeZoneX, bgY, dodgeZoneWidth, bgHeight);
        g2.setColor(Color.YELLOW);
        g2.fillRect(parryZoneX, bgY, parryZoneWidth, bgHeight);
        g2.setColor(Color.BLUE);
        g2.fillRect(qteBarX, bgY, 15, bgHeight);
    }

    public void drawBattleMessage(Graphics2D g2) {
        int boxX = 400, boxWidth = 500;
        if (currentDialogue.equals("Dermoon's Last Stand!")) {
            boxWidth = 600;
            boxX = 340;
        }
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(boxX, 200, boxWidth, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
        int textLength = (int) g2.getFontMetrics().getStringBounds(currentDialogue, g2).getWidth();
        g2.drawString(currentDialogue, 640 - (textLength / 2), 290);
    }

} 
