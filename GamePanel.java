
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    final int screenWidth = 1280;
    final int screenHeight = 720;
    
    PlayerA playerA;
    PlayerB playerB;
    Thread gameThread;
    KeyHandler listener = new KeyHandler();

    public GamePanel(PlayerA a, PlayerB b)
    {
        this.playerA = a;
        this.playerB = b;
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); //drawing from this component will be done in an offscreen
        this.addKeyListener(listener);
    }

    public void startThread()
    {
        this.gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null)
        {
            updateInfo();
            repaint();
        }
    }

    public void updateInfo()
    {
        if(listener.moveUpA == true)
        {playerA.updateY(-10);}
        else if (listener.moveDownA == true)
        {playerA.updateY(10);}
        else if (listener.moveLeftA == true)
        {playerA.updateX(-10);}
        else if (listener.moveRightA == true)
        {playerA.updateX(10);}
        else if (listener.moveUpB == true)
        {playerB.updateY(-10);}
        else if (listener.moveDownB == true)
        {playerB.updateY(10);}
        else if (listener.moveLeftB == true)
        {playerB.updateX(-10);}
        else if (listener.moveRightA == true)
        {playerB.updateX(-10);}
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(playerA.x,playerA.y,25,25);
        g.dispose();

        g.setColor(Color.RED);
        g.fillRect(playerB.x,playerB.y,25,25);
        g.dispose();
    }
}
