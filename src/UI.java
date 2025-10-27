import java.awt.*;

public class UI {
    Font arial_40;
    public int commandNum = 0; // 0 = Attack, 1 = Skip
    public String currentDialogue = "";

    public UI() {
        arial_40 = new Font("Arial", Font.PLAIN, 40);
    }

    // --- drawHPBar ยังคงอยู่เหมือนเดิม ---
    public void drawHPBar(Graphics2D g2, int x, int y, int width, int height, int currentHP, int maxHP) {
        // คำนวณความกว้างของแถบเลือดปัจจุบัน
        double hpPercentage = (double) currentHP / maxHP;
        // ป้องกันกรณี currentHP เป็น 0 หรือติดลบ ทำให้ hpPercentage ไม่ถูกต้อง
        if (hpPercentage < 0) hpPercentage = 0;
        int currentHPBarWidth = (int) (width * hpPercentage);

        // วาดกรอบนอก (สีเทาเข้ม)
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);

        // วาดแถบเลือดปัจจุบัน (สีแดง)
        g2.setColor(Color.RED);
        g2.fillRect(x, y, currentHPBarWidth, height);

        // วาดกรอบเส้น (สีขาว)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2)); // เส้นหนา 2 pixels
        g2.drawRect(x, y, width, height);

        // (ทางเลือก) แสดงตัวเลข HP บนหลอดเลือด
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
        String hpText = currentHP + "/" + maxHP;
        int textX = x + width / 2 - (int)(g2.getFontMetrics().getStringBounds(hpText, g2).getWidth() / 2);
        int textY = y + height / 2 + 7; // ปรับตำแหน่ง Y ให้ดูดี
        g2.setColor(Color.WHITE);
        g2.drawString(hpText, textX, textY);
    }

    // --- drawBattleScreen แก้ไขให้วาดเฉพาะกล่องคำสั่ง ---
    public void drawBattleScreen(Graphics2D g2, Knight knight, Slime slime) { // <--- เพิ่ม g2 ตรงนี้
        // --- ลบบรรทัดนี้ออก (ถ้ายังมี) ---
        // this.g2 = g2;

        // --- โค้ดที่เหลือเหมือนเดิม แต่ใช้ g2 ที่รับเข้ามา ---
        g2.setColor(new Color(0, 0, 0, 150)); // ใช้ g2 ตรงๆ
        g2.fillRoundRect(50, 500, 300, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(50, 500, 300, 150, 35, 35);
        g2.setFont(arial_40);
        g2.setColor(Color.white);
        g2.drawString("Attack", 75, 560);
        if (commandNum == 0) g2.drawString(">", 50, 560);
        g2.drawString("Skip", 75, 610);
        if (commandNum == 1) g2.drawString(">", 50, 610);
    }

    // --- drawAttackQTE เหมือนเดิม ---
    public void drawAttackQTE(Graphics2D g2, int qteBarX) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(400, 300, 500, 50);
        g2.setColor(Color.GREEN);
        g2.fillRect(680, 300, 80, 50);
        g2.setColor(Color.RED);
        g2.fillRect(qteBarX, 300, 15, 50);
    }

    // --- drawDefenseQTE เหมือนเดิม ---
    public void drawDefenseQTE(Graphics2D g2, int qteBarX) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(400, 300, 500, 50);
        g2.setColor(Color.CYAN);
        g2.fillRect(600, 300, 200, 50);
        g2.setColor(Color.YELLOW);
        g2.fillRect(680, 300, 80, 50);
        g2.setColor(Color.BLUE);
        g2.fillRect(qteBarX, 300, 15, 50);
    }

    // --- drawBattleMessage เหมือนเดิม ---
    public void drawBattleMessage(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(400, 200, 500, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50F));
        int textLength = (int)g2.getFontMetrics().getStringBounds(currentDialogue, g2).getWidth();
        g2.drawString(currentDialogue, 640 - (textLength / 2), 290);
    }
}