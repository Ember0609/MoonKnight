import java.awt.Rectangle;
import java.awt.image.BufferedImage; // +++ เพิ่ม Import +++

public class Character {
    public String name;
    public int hp;
    public int maxHp;
    public int atk;
    public int x, y;
    public int speed;
    public Rectangle solidArea;
    public int originalX, originalY;

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

    public void updateForBattle() {
        // คลาสลูกสามารถ override method นี้ได้
    }

    // +++ เพิ่มเมธอดสำหรับ Polymorphism (การพ้องรูป) +++
    // 1. เมธอดสำหรับ AI
    // GamePanel จะถูกส่งเข้ามา เพื่อให้คลาสลูกสามารถเปลี่ยน state ของเกมได้
    public void performTurn(GamePanel gp) {
        // นี่คือท่าโจมตีพื้นฐาน (สำหรับ Slime)
        gp.battleSubState = BattleSubState.ENEMY_MOVING_TO_TARGET;
    }

    // +++ เพิ่มเมธอดสำหรับ Polymorphism (การพ้องรูป) +++
    // 2. เมธอดสำหรับวาด
    // คืนค่า null เป็น default (คลาสลูกต้อง override)
    public BufferedImage getCurrentImage() {
        return null;
    }
}