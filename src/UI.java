import java.awt.*;

public class UI {
    Graphics2D g2;
    Font arial_40;
    public int commandNum = 0; // 0 = Attack, 1 = Skip
    public String currentDialogue = "";

    public UI() {
        arial_40 = new Font("Arial", Font.PLAIN, 40);
    }

    public void drawHPBar(Graphics2D g2, int x, int y, int width, int height, int currentHP, int maxHP) {
        // คำนวณความกว้างของแถบเลือดปัจจุบัน
        double hpPercentage = (double) currentHP / maxHP;
        int currentHPBarWidth = (int) (width * hpPercentage);

        // วาดกรอบนอก (สีเทาเข้ม)
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);

        // วาดแถบเลือดปัจจุบัน (สีแดง)
        g2.setColor(Color.RED);
        // ทำให้เลือดลดจากขวาไปซ้าย หรือ ซ้ายไปขวา ตามต้องการ
        // ตัวอย่าง: ลดจากขวาไปซ้าย
        // g2.fillRect(x + (width - currentHPBarWidth), y, currentHPBarWidth, height);
        // ตัวอย่าง: ลดจากซ้ายไปขวา (แบบปกติ)
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


    public void draw(Graphics2D g2, Knight knight, Slime slime) { // <-- **แก้ไข: รับ object Knight และ Slime เข้ามา**
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // วาดหน้าจอต่อสู้หลัก
        drawBattleScreen(knight, slime); // <-- ส่งต่อไป
    }

    public void drawBattleScreen(Knight knight, Slime slime) { // <-- **แก้ไข: รับ object Knight และ Slime เข้ามา**
        // --- วาดหลอดเลือด Knight ---
        drawHPBar(g2, 50, 50, 400, 40, knight.hp, knight.maxHp);
        g2.setFont(arial_40); // Reset font
        g2.setColor(Color.white); // Reset color
        g2.drawString(knight.name, 50, 40); // แสดงชื่อเหนือหลอดเลือด

        // --- วาดหลอดเลือด Slime ---
        drawHPBar(g2, 830, 50, 400, 40, slime.hp, slime.maxHp);
        g2.setFont(arial_40); // Reset font
        g2.setColor(Color.white); // Reset color
        g2.drawString(slime.name, 830, 40);

        // --- วาดกล่องคำสั่ง (เหมือนเดิม) ---
        // ... (โค้ดวาด fillRoundRect, drawRoundRect, drawString Attack/Skip) ...
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(50, 500, 300, 150, 35, 35);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(50, 500, 300, 150, 35, 35);
        g2.setFont(arial_40); // Reset font
        g2.drawString("Attack", 75, 560);
        if (commandNum == 0) g2.drawString(">", 50, 560);
        g2.drawString("Skip", 75, 610);
        if (commandNum == 1) g2.drawString(">", 50, 610);

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