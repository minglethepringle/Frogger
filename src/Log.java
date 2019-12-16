import java.awt.*;

public class Log extends Sprite {

    private int width;

    public Log(int x, int y, int dir, int speed) {
        super(x, y, dir, false);
        int type = Helper.random(1, 3);
        switch (type) {
            case 1:
                setPic("logLarge.png", WEST); //overrides the default "blank.png"
                width = 250;
                break;
            case 2:
                setPic("logMedium.png", WEST); //overrides the default "blank.png"
                width = 170;
                break;
            case 3:
                setPic("logShort.png", WEST); //overrides the default "blank.png"
                width = 120;
                break;
        }

        setSpeed(speed);
    }

    @Override
    public void update() {
        super.update();
        if (getLoc().x > FroggerMain.FRAMEWIDTH + 10 && getDir() == Sprite.EAST) {
            setLoc(new Point(-300, getLoc().y));
        } else if (getLoc().x < -300 && getDir() == Sprite.WEST) {
            setLoc(new Point(FroggerMain.FRAMEWIDTH + 10, getLoc().y));
        }
    }

    public int getWidth() {
        return width;
    }
}
