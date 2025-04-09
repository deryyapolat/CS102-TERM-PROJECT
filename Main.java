import javax.swing.JFrame;

public class Main {
    public static void main (String[] args)
    {
        JFrame frame = new JFrame();
        GamePanel panel = new GamePanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Lost Beneath");
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startThread();

    }
}
