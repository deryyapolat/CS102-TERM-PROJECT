public class PlayerA {
    public int x;
    public int y;
    GamePanel panel;
    KeyHandlerA keyHandlerA;

    public PlayerA(GamePanel panel, KeyHandlerA keyHandlerA)
    {
        this.panel = panel;
        this.keyHandlerA = keyHandlerA;
        this.x = 100;
        this.y = 100;
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
