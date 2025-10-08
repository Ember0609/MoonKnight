import javax.swing.JFrame;

public class Game {
    public static void main(String[] args) {
        // สร้างหน้าต่างหลักของเกม (JFrame)
        JFrame window = new JFrame("MoonKnight");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ตั้งค่าให้โปรแกรมปิดเมื่อกดปุ่ม X
        window.setResizable(false); // ไม่ให้ปรับขนาดหน้าจอ

        // สร้าง GamePanel (กระดานวาดภาพ) ของเรา
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel); // นำกระดานวาดภาพใส่เข้าไปในหน้าต่าง

        window.pack(); // คำสั่งให้หน้าต่างปรับขนาดตัวเองให้พอดีกับ Panel ข้างใน
        window.setSize(1280, 720); // กำหนดขนาดหน้าต่าง
        window.setLocationRelativeTo(null); // แสดงหน้าต่างขึ้นมากลางจอ
        window.setVisible(true); // ทำให้หน้าต่างมองเห็นได้
    }
}