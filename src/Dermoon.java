import java.awt.Rectangle;
import java.awt.image.BufferedImage; // เพิ่ม import นี้
import java.io.IOException;      // เพิ่ม import นี้
import javax.imageio.ImageIO;        // เพิ่ม import นี้

public class Dermoon extends Character {
    public BufferedImage image; // เพิ่มตัวแปรเก็บรูป
    public int originalX, originalY;
    public String currentAction = "idle"; // idle, etc. (เผื่อ animation ในอนาคต)

    public Dermoon() {
        super("Dermoon", 150, 35); // ตั้งค่า HP และ ATK สูงกว่า Slime
        this.originalX = 800; // ตำแหน่ง X เดียวกับ Slime
        this.originalY = 490; // ตำแหน่ง Y เดียวกับ Slime
        this.x = originalX;
        this.y = originalY;
        this.speed = 15; // อาจจะเร็วกว่า Slime?
        // ปรับ Hitbox ให้เหมาะกับ Dermoon (อาจจะต้องลองปรับค่า)
        this.solidArea = new Rectangle(x + 32, y + 16, 64, 112);

        // โหลดรูปภาพ Dermoon
        loadDermoonImage();
    }

    public void loadDermoonImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("Picture/DerMoon.png")); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateForBattle() {
        // (ยังว่างไว้ก่อน เผื่อ animation)
    }
}