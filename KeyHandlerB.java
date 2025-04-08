import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandlerB implements KeyListener {

    public boolean moveUp; boolean moveDown; boolean moveLeft; boolean moveRight;

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP)
        {moveUp = true;}
        if(code == KeyEvent.VK_LEFT)
        {moveLeft = true;}
        if(code == KeyEvent.VK_DOWN)
        {moveDown = true;}
        if(code == KeyEvent.VK_RIGHT)
        {moveRight = true;}
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_UP)
        {moveUp = false;}
        if(code == KeyEvent.VK_LEFT)
        {moveLeft = false;}
        if(code == KeyEvent.VK_DOWN)
        {moveDown = false;}
        if(code == KeyEvent.VK_RIGHT)
        {moveRight = false;}
    }
    
}
