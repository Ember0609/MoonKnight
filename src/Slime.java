import java.awt.Rectangle;
// (อาจจะต้อง import BufferedImage ถ้าจะเพิ่ม animation)

public class Slime extends Character {
    public int originalX, originalY; // <-- เพิ่ม originalX, originalY
    public String currentAction = "idle"; // idle, attacking, returning

    public Slime() {
        super("Slime", 200, 50);
        this.originalX = 800; // <-- เก็บตำแหน่ง X เริ่มต้น
        this.originalY = 490; // <-- เก็บตำแหน่ง Y เริ่มต้น
        this.x = originalX;
        this.y = originalY;
        this.speed = 20; // <-- กำหนดความเร็วให้ Slime ด้วย
        this.solidArea = new Rectangle(x + 16, y + 16, 90, 90);
    }

    // (อาจจะเพิ่ม method updateForBattle() ถ้ามี animation)
}