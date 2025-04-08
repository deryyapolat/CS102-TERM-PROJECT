import javax.swing.JFrame;

public class Main {
    public static void main (String[] args)
    {
        JFrame frame = new JFrame();
        PlayerA a = new PlayerA();
        PlayerB b = new PlayerB(); 
        GamePanel panel = new GamePanel(a,b);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Lost Beneath");
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
