import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandlerA implements KeyListener{

    public boolean moveUp; boolean moveDown; boolean moveLeft; boolean moveRight; 
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W) 
        {moveUp = true;}
        if(code == KeyEvent.VK_A)
        {moveLeft = true;}
        if(code == KeyEvent.VK_S)
        {moveDown= true;}
        if(code == KeyEvent.VK_D)
        {moveRight = true;}
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W)
        {moveUp = false;}
        if(code == KeyEvent.VK_A)
        {moveLeft = false;}
        if(code == KeyEvent.VK_S)
        {moveDown = false;}
        if(code == KeyEvent.VK_D)
        {moveRight = false;}

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
    
}
