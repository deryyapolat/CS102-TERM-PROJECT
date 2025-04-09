import java.awt.Color;
import java.awt.Graphics;

public class PlayerA {
    public int x;
    public int y;
    GamePanel panel;
    KeyHandlerA listenerA;

    public PlayerA(GamePanel panel, KeyHandlerA keyHandlerA)
    {
        this.panel = panel;
        this.listenerA = keyHandlerA;
        this.x = 100;
        this.y = 100;
    }

    public void moveUpdate()
    {
        if (listenerA.moveUp == true && listenerA.moveLeft == true)
        {updateY(-10);updateX(-10);}
        else if (listenerA.moveUp == true && listenerA.moveRight == true)
        {updateY(-10);updateX(10);}
        else if (listenerA.moveDown == true && listenerA.moveRight == true)
        {updateY(10);updateX(10);}
        else if (listenerA.moveDown == true && listenerA.moveRight == true)
        {updateY(10);updateX(10);}
        else if(listenerA.moveUp == true)
        {updateY(-10);}
        else if (listenerA.moveDown == true)
        {updateY(10);}
        else if (listenerA.moveLeft == true)
        {updateX(-10);}
        else if (listenerA.moveRight == true)
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
        g.setColor(Color.WHITE);
        g.fillRect(x,y,25,25);
    }
}
