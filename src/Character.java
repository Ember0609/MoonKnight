import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Character {
    
    private String name;
    private int hp;
    private int maxHp;
    private int atk;

    protected int x, y;
    protected int speed;
    protected Rectangle solidArea;
    protected int originalX, originalY;

    public Character(String name, int hp, int atk) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.atk = atk;
    }

    public String getName() { return this.name; }
    public int getHp() { return this.hp; }
    public int getMaxHp() { return this.maxHp; }
    public int getAtk() { return this.atk; }


    public void setHp(int value) {
        if (value < 0) {
            this.hp = 0;
        } else if (value > this.maxHp) {
            this.hp = this.maxHp;
        } else {
            this.hp = value;
        }
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public void attack(Character target) {
        System.out.println(this.name + " attacks " + target.name);
        

        target.takeDamage(this.atk);
    }

    public void updateForBattle() {
    }

    public void performTurn(GamePanel gp) {
        gp.battleSubState = BattleSubState.ENEMY_MOVING_TO_TARGET;
    }

  
    public abstract BufferedImage getCurrentImage();
}