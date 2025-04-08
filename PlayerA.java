public class PlayerA {
    public int x;
    public int y;

    public PlayerA()
    {
        this.x = 100;
        this.y = 100;
    }

    public void updateX(int value)
    {
        this.x += value;
    }

    public void updateY(int value)
    {
        this.y += value;
    }
}
