
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

    public GamePanel(PlayerA a, PlayerB b)
    {
        this.playerA = a;
        this.playerB = b;
        this.map1 = new ImageIcon("map1.png").getImage();
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); //drawing from this component will be done in an offscreen
        this.addKeyListener(listenerA);
        this.addKeyListener(listenerB);
        this.setFocusable(true); //focused to key actions
    }

    public void startThread()
    {
        this.gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double intervalOfDrawing = 1000000000/60;  //nanosecond / 60
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
        if(listenerA.moveUp == true)
        {playerA.updateY(-10);}
        else if (listenerA.moveDown == true)
        {playerA.updateY(10);}
        else if (listenerA.moveLeft == true)
        {playerA.updateX(-10);}
        else if (listenerA.moveRight == true)
        {playerA.updateX(10);}
        else if (listenerB.moveUp == true)
        {playerB.updateY(-10);}
        else if (listenerB.moveDown == true)
        {playerB.updateY(10);}
        else if (listenerB.moveLeft == true)
        {playerB.updateX(-10);}
        else if (listenerB.moveRight == true)
        {playerB.updateX(10);}
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.drawImage(map1, 0, 0, getFocusCycleRootAncestor());
        
        g.setColor(Color.WHITE);
        g.fillRect(playerA.x,playerA.y,25,25);
        

        g.setColor(Color.RED);
        g.fillRect(playerB.x,playerB.y,25,25);

        
    }
}
