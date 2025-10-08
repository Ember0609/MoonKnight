import java.awt.Graphics;
import java.awt.Image;
import javax.swing.*;


public class Display extends JFrame {
    
    public Display(){
        setTitle("MoonKnight");
        setSize(1280,720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        BackgroundPanel B = new BackgroundPanel();
        setContentPane(B);
        setVisible(true);
    }

    public static void main(String[] arg){
        Display Dis = new Display();      
    }
}

class BackgroundPanel extends  JPanel {
    public Image background;
    public Image player,slime,dermoon;
    public BackgroundPanel(){
        background = new ImageIcon("Picture/Map1.png").getImage();
        player = new ImageIcon("Picture/MainFront.png").getImage();
        slime = new ImageIcon("Picture/Slimekung.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(),getHeight(), this);
        g.drawImage(player, 80, 406,250,250, this);
        g.drawImage(slime, 950, 410,250,250, this);

    }
}   