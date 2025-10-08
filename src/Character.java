public class Character {
    public String name;
    public int hp;
    public int atk;

    public Character(String name , int hp, int atk){
        this.name = name;
        this.hp = hp;
        this.atk = atk;
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    // Method: โจมตีเป้าหมาย
    public void attack(Character target) {
        System.out.println(this.name + " attacks " + target.name);
        target.hp -= this.atk;
    }
}
