
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    final int screenWidth = 1280;
    final int screenHeight = 720;
    
    PlayerA playerA;
    PlayerB playerB;
    Thread gameThread;
    KeyHandlerA listenerA = new KeyHandlerA();
    KeyHandlerB listenerB = new KeyHandlerB();
    Image map1;

    public GamePanel()
    {
        this.map1 = new ImageIcon("map1.png").getImage();
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); //drawing from this component will be done in an offscreen
        this.addKeyListener(listenerA);
        this.addKeyListener(listenerB);
        this.setFocusable(true); //focused to key actions
        this.playerA = new PlayerA(this, listenerA);
        this.playerB = new PlayerB(this, listenerB);
    }

    public void startThread()
    {
        this.gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double intervalOfDrawing = 1000000000/60;  // nanosecond / 60
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null)
        {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / intervalOfDrawing;
            lastTime = currentTime;

            if (delta >= 1)
            {
                updateInfo();
                repaint();
                delta--;
            }
        }
    }

    public void updateInfo()
    {
        playerA.moveUpdate();
        playerB.moveUpdate();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.drawImage(map1, 0, 0, getFocusCycleRootAncestor());
        playerA.drawCharacter(g);
        playerB.drawCharacter(g);
    }
}
