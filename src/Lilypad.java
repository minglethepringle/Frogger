public class Lilypad extends Sprite {

    private Sprite frogOn;

    public Lilypad(int x, int y) {
        super(x, y, SOUTH, false);
        setPic("lilypad.png", SOUTH); //overrides the default "blank.png"
        frogOn = null;
    }

    @Override
    public void update() {
    }

    public Sprite getFrogOn() {
        return frogOn;
    }

    public void setFrogOn(Sprite frogOn) {
        this.frogOn = frogOn;
    }
}
