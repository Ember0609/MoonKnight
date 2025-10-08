import java.awt.Rectangle;

public class Knight extends Character {

    public Knight() {
        super("Knight", 100, 25);
        
        // กำหนดค่าเริ่มต้น
        this.x = 150;
        this.y = 400;
        this.speed = 4;

        // กำหนดค่า Hitbox (x, y, width, height)
        this.solidArea = new Rectangle(x + 48, y + 48, 50, 50);
    }

    public void update(KeyHandler keyH) {
        if (keyH.upPressed) y -= speed;
        if (keyH.downPressed) y += speed;
        if (keyH.leftPressed) x -= speed;
        if (keyH.rightPressed) x += speed;

        // อัปเดตตำแหน่ง Hitbox ให้เดินตามตัวละคร
        solidArea.x = x + 48;
        solidArea.y = y + 48;
    }
}