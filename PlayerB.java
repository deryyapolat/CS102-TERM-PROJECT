public class PlayerB {
    public int x;
    public int y;
    GamePanel panel;
    KeyHandlerB keyHandlerB;

    public PlayerB(GamePanel panel, KeyHandlerB keyHandlerB)
    {
        this.panel = panel;
        this.keyHandlerB = keyHandlerB;
        this.x = 500;
        this.y = 500;
    }

    public void updateX(int value)
    {
        x += value;
    }

    public void updateY(int value)
    {
        y += value;
    }
}
