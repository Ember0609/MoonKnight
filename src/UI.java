import java.awt.*;

public class UI {
    Graphics2D g2;
    Font arial_40;
    public int commandNum = 0; // 0 = Attack, 1 = Skip
    public String currentDialogue = "";

    public UI() {
        arial_40 = new Font("Arial", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // วาดหน้าจอต่อสู้
        drawBattleScreen();
    }

    public void drawBattleScreen() {
        // --- วาดกล่องคำสั่ง ---
        g2.setColor(new Color(0, 0, 0, 150)); // สีดำโปร่งแสง
        g2.fillRoundRect(50, 500, 300, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(5));
        g2.drawRoundRect(50, 500, 300, 150, 35, 35);

        // --- วาดคำสั่ง ---
        g2.drawString("Attack", 75, 560);
        if (commandNum == 0) {
            g2.drawString(">", 50, 560); // แสดงลูกศรชี้
        }

        g2.drawString("Skip", 75, 610);
        if (commandNum == 1) {
            g2.drawString(">", 50, 610);
        }
    }

    public void drawAttackQTE(Graphics2D g2, int qteBarX) {
        // วาดกรอบนอก
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(400, 300, 500, 50);

        // วาดโซนเป้าหมาย (Success Zone)
        g2.setColor(Color.GREEN);
        g2.fillRect(680, 300, 80, 50);

        // วาดแถบที่เคลื่อนที่
        g2.setColor(Color.RED);
        g2.fillRect(qteBarX, 300, 15, 50);
    }

    public void drawDefenseQTE(Graphics2D g2, int qteBarX) {
        // วาดกรอบนอก
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(400, 300, 500, 50);

        // วาดโซน Dodge (ใหญ่ / สีฟ้า)
        g2.setColor(Color.CYAN);
        g2.fillRect(600, 300, 200, 50);

        // วาดโซน Parry (เล็ก / สีเหลือง)
        g2.setColor(Color.YELLOW);
        g2.fillRect(680, 300, 80, 50);

        // วาดแถบที่เคลื่อนที่
        g2.setColor(Color.BLUE);
        g2.fillRect(qteBarX, 300, 15, 50);
    }
    
    // --- Method ใหม่: สำหรับแสดงข้อความผลลัพธ์ ---
    public void drawBattleMessage(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(400, 200, 500, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
        int textLength = (int)g2.getFontMetrics().getStringBounds(currentDialogue, g2).getWidth();
        g2.drawString(currentDialogue, 640 - (textLength / 2), 290);
    }
}