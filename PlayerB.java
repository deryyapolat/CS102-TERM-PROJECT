import java.awt.Color;
import java.awt.Graphics;

public class PlayerB {
    public int x;
    public int y;
    GamePanel panel;
    KeyHandlerB listenerB;

    public PlayerB(GamePanel panel, KeyHandlerB keyHandlerB)
    {
        this.panel = panel;
        this.listenerB = keyHandlerB;
        this.x = 500;
        this.y = 500;
    }

    public void moveUpdate()
    {
        if (listenerB.moveUp == true && listenerB.moveLeft == true)
        {updateY(-10);updateX(-10);}
        else if (listenerB.moveUp == true && listenerB.moveRight == true)
        {updateY(-10);updateX(10);}
        else if (listenerB.moveDown == true && listenerB.moveRight == true)
        {updateY(10);updateX(10);}
        else if (listenerB.moveDown == true && listenerB.moveRight == true)
        {updateY(10);updateX(10);}
        else if (listenerB.moveUp == true)
        {updateY(-10);}
        else if (listenerB.moveDown == true)
        {updateY(10);}
        else if (listenerB.moveLeft == true)
        {updateX(-10);}
        else if (listenerB.moveRight == true)
        {updateX(10);}
    }

    public void updateX(int value)
    {
        if (x < 1270 && x > 10)
        {
            x += value;
        }
        else if (x >= 1270)
        {
            x = 1269;
        }
        else if (x <= 10)
        {
            x = 11;
        }
    }

    public void updateY(int value)
    {
        if (y < 710 && y > 10)
        {
            y += value;
        } 
        else if (y >= 710)
        {
            y = 709;
        }
        else if (y <= 10)
        {
            y = 11;
        }
    }

    public void drawCharacter(Graphics g)
    {
        g.setColor(Color.RED);
        g.fillRect(x,y,25,25);
    }
}
