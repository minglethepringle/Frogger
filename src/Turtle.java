import java.awt.*;

public class Turtle extends Sprite{

    private int width;

    public Turtle(int x, int y, int dir, int speed) {
        super(x, y, dir, true);
        setPic("turtle1.png", WEST);

        setSpeed(speed);
    }

    @Override
    public void update() {
        super.update();
        if (getLoc().x > FroggerMain.FRAMEWIDTH + 10 && getDir() == Sprite.EAST) {
            setLoc(new Point(-50, getLoc().y));
        } else if (getLoc().x < -50 && getDir() == Sprite.WEST) {
            setLoc(new Point(FroggerMain.FRAMEWIDTH + 10, getLoc().y));
        }
    }

    public int getWidth() {
        return width;
    }
}
