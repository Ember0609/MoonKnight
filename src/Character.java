import java.awt.Rectangle; // import Rectangle

public class Character {
    public String name;
    public int hp;
    public int maxHp; // <-- เพิ่มตัวแปรนี้
    public int atk;
    public int x, y; // เพิ่ม x, y มาไว้ที่คลาสแม่เลย
    public int speed; // เพิ่ม speed มาไว้ที่คลาสแม่ด้วย

    public Rectangle solidArea; // Hitbox ของตัวละคร

    public Character(String name, int hp, int atk) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
    }

    

    public boolean isAlive() {
        return this.hp > 0;
    }

    public void attack(Character target) {
        System.out.println(this.name + " attacks " + target.name);
        target.hp -= this.atk;
    }
}