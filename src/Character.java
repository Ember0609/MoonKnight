
public class Character {
    public String name;
    public int hp,atk;

    public Character(String n,int h,int a) {
        name = n;
        hp = h;
        atk = a;
    }

    public boolean isAlive(){
        return hp > 0;
    }
}

interface Attack{
    public void attack(Character target){
    }
}

public class Knight extends Character implements Attack{

    public Knight() {
        super("Knight", 100, 30);
    }

    public void attack(Character target){
        targert.hp -= this.atk;
    }
}

public class Slime extends Character implements Attack{

    public Slime() {
        super("Slime", 300, 50);
    }

    public void attack(int ehp){
        targert.hp -= this.atk;
    }
}

public class Dermoon extends Character implements Attack{

    public Dermoon() {
        super("Dermoon", 300, 50);
    }

    public void attack(Character target){
        target.hp -= this.atk;
    }
}