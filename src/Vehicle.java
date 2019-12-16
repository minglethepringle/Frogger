import java.awt.*;

/**
 * Created by michael_hopps on 2/5/18.
 */
public class Vehicle extends Sprite {

    private int type;

    public Vehicle(int x, int y, int direction, int speed) {
        super(x, y, direction, true);
        int type = Helper.random(1, 5);
        this.type = type;
        switch (type) {
            case 1:
                setPic("car1.png", WEST);
                break;
            case 2:
                setPic("car2.png", WEST);
                break;
            case 3:
                setPic("car3.png", WEST);
                break;
            case 4:
                setPic("car4.png", WEST);
                break;
            case 5:
                setPic("truck.png", WEST);
                break;
        }

        setSpeed(speed); //GUESS?!
    }

    public Vehicle(int x, int y, int direction, int speed, int type) {
        super(x, y, direction, (type != 6));
        this.type = type;
        if(type == 6) {
            setPic("lawnmower.png", WEST);
        } else {

        }
        setSpeed(speed);
    }

    @Override
    public void update() {
        super.update();
        if (getLoc().x > FroggerMain.FRAMEWIDTH + 10 && getDir() == Sprite.EAST) {
            setLoc(new Point((type == 6) ? Helper.random(-50, -10) : -getWidth() - 10, getLoc().y));
        } else if (getLoc().x < -getWidth() - 10 && getDir() == Sprite.WEST) {
            setLoc(new Point((type == 6) ? Helper.random(FroggerMain.FRAMEWIDTH, FroggerMain.FRAMEWIDTH + 150) : FroggerMain.FRAMEWIDTH + 10, getLoc().y));
        }
    }
}
