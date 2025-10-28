import java.awt.Rectangle;

public class Character {
    public String name;
    public int hp;
    public int maxHp;
    public int atk;
    public int x, y;
    public int speed;
    public Rectangle solidArea;
    public int originalX, originalY; // <-- **เพิ่มประกาศตรงนี้**

    public Character(String name, int hp, int atk) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
        // **ไม่ต้องกำหนดค่า originalX, originalY ที่นี่**
    }

    

    public boolean isAlive() {
        return this.hp > 0;
    }

    public void attack(Character target) {
        System.out.println(this.name + " attacks " + target.name);
        target.hp -= this.atk;
    }

    public void updateForBattle() {
        // คลาสลูกสามารถ override method นี้ได้
    }
}