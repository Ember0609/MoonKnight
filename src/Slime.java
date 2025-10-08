import java.awt.Rectangle;

public class Slime extends Character {

    public Slime() {
        super("Slime", 100, 10);

        // กำหนดตำแหน่งเริ่มต้น
        this.x = 800;
        this.y = 490;

        // กำหนดค่า Hitbox
        this.solidArea = new Rectangle(x + 16, y + 16, 90, 90);
    }
}