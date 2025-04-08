import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public boolean moveUpA; boolean moveDownA; boolean moveLeftA; boolean moveRightA; boolean moveUpB; boolean moveDownB; boolean moveLeftB; boolean moveRightB;
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W) 
        {moveUpA = true;}
        if(code == KeyEvent.VK_A)
        {moveLeftA = true;}
        if(code == KeyEvent.VK_S)
        {moveDownA= true;}
        if(code == KeyEvent.VK_D)
        {moveRightA = true;}
        if(code == KeyEvent.VK_PAGE_UP)
        {moveUpB = true;}
        if(code == KeyEvent.VK_LEFT)
        {moveLeftB = true;}
        if(code == KeyEvent.VK_PAGE_DOWN)
        {moveDownB = true;}
        if(code == KeyEvent.VK_RIGHT)
        {moveRightB = true;}
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W)
        {moveUpA = false;}
        if(code == KeyEvent.VK_A)
        {moveLeftA = false;}
        if(code == KeyEvent.VK_S)
        {moveDownA = false;}
        if(code == KeyEvent.VK_D)
        {moveRightA = false;}
        if(code == KeyEvent.VK_PAGE_UP)
        {moveUpB = false;}
        if(code == KeyEvent.VK_LEFT)
        {moveLeftB = false;}
        if(code == KeyEvent.VK_PAGE_DOWN)
        {moveDownB = false;}
        if(code == KeyEvent.VK_RIGHT)
        {moveRightB = false;}
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
    
}
