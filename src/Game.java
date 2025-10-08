import javax.swing.JFrame;

public class Game {
    public static void main(String[] args) {
        JFrame window = new JFrame("MoonKnight");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack(); // pack() ต้องอยู่ก่อน setLocation และ setVisible
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // --- เพิ่มบรรทัดนี้ ---
        gamePanel.startGameThread(); // เริ่มการทำงานของเกม!
    }
}