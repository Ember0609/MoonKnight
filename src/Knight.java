public class Knight extends Character {

    public int x, y; // ตำแหน่งบนหน้าจอ
    public int speed; // ความเร็วในการเคลื่อนที่

    public Knight() {
        super("Knight", 100, 25);
        
        // กำหนดค่าเริ่มต้น
        this.x = 150; // ตำแหน่งเริ่มต้นแกน X
        this.y = 400; // ตำแหน่งเริ่มต้นแกน Y
        this.speed = 4; // ความเร็ว 4 pixels ต่อ frame
    }

    public void update(KeyHandler keyH) {
        // อัปเดตตำแหน่งตามปุ่มที่กด
        if (keyH.upPressed) {
            y -= speed;
        }
        if (keyH.downPressed) {
            y += speed;
        }
        if (keyH.leftPressed) {
            x -= speed;
        }
        if (keyH.rightPressed) {
            x += speed;
        }
    }
}