public class PlayerB {
    public int x;
    public int y;

    public PlayerB()
    {
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
