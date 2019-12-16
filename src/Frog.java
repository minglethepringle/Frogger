import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by michael_hopps on 2/13/17.
 */
public class Frog extends Sprite {

    public static final int SPEED = 50;
    private Sprite sittingOn;
    private boolean movable;

    public Frog(){
        super(0, 0, NORTH, true);
        setPic("frog2.png", NORTH); //overrides the default "blank.png"
        setSpeed(SPEED);
        setLoc(new Point((FroggerMain.FRAMEWIDTH / 2) - (getBoundingRectangle().width / 2), 665));
        movable = true;
    }

    @Override
    public void update() {
        if(movable) super.update();   //moves the Frog in the dir it's facing
        try {
            Helper.playSound("./res/hop.wav", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(getLoc().x < 5 || getLoc().x > FroggerMain.FRAMEWIDTH - 5 ||
            getLoc().y < 50 || getLoc().y > FroggerMain.FRAMEHEIGHT - 50) undoUpdate();
    }

    public void checkIfOut() {
        // Check if it's out
        if(sittingOn != null && (getLoc().x <= 0 || getLoc().x >= FroggerMain.FRAMEWIDTH - getBoundingRectangle().width)){
            FroggerMain.panel.loseLife();
        }
    }

    public void moveToLilypad(Sprite object) {
        setLoc(new Point(object.getLoc().x + 10, object.getLoc().y + 14));
        setDir(Sprite.SOUTH);
        movable = false;
    }

    public void setSittingOn(Sprite sittingOn) {
        this.sittingOn = sittingOn;
    }

    public Sprite getSittingOn() {
        return sittingOn;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }
}
