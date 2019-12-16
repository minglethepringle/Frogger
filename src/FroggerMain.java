import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// Mingle Li

public class FroggerMain extends JPanel {

    public static JFrame window;
    public static FroggerMain panel;

    //instance fields for the general environment
    public static final int FRAMEWIDTH = 750, FRAMEHEIGHT = 750;
    private static Timer timer;
    private boolean[] keys;

    //instance fields for frogger.
    public static Sprite frog;
    public static int LIVES = 3,
                        SCORE = 0,
                        TIME = 30000,
                        TRAVELED_TO = FRAMEHEIGHT,
                        LEVEL = 1;
    private ArrayList<Sprite> obstacles;
    private ArrayList<Sprite> sittable;
    private ArrayList<Sprite> passiveFrogs;
    public static int numCarsPerRow = 4,
                    frogJumpFrames = 5,
                    turtleSwimFrames = 0;
    private BufferedImage startingTexture, middleTexture, roadTexture, riverTexture;

    public FroggerMain() {
        try {
            File f = new File("./res/bedrock.png");
            startingTexture = ImageIO.read(f);
            f = new File("./res/grass.jpeg");
            middleTexture = ImageIO.read(f);
            f = new File("./res/concrete.jpg");
            roadTexture = ImageIO.read(f);
            f = new File("./res/water.jpg");
            riverTexture = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        regenerate();

        timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TIME -= 40;
                if(TIME <= 0) loseLife();

                ((Frog)frog).checkIfOut();

                //move the frog
                try {
                    moveTheFrog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(frogJumpFrames < 5) {
                    if(frogJumpFrames == 0) {
                        frog.setPic("frog2.png", Sprite.NORTH);
                    }
                    if (frogJumpFrames == 1 || frogJumpFrames == 2 || frogJumpFrames == 3) {
                        frog.setPic("frog1.png", Sprite.NORTH);
                    }
                    if(frogJumpFrames == 4) {
                        frog.setPic("frog2.png", Sprite.NORTH);
                    }
                    frogJumpFrames++;
                }

                for(Sprite obstacle : obstacles) {
                    obstacle.update();
                    if(frog.intersects(obstacle)) {
                        loseLife();
                    }
                }

                boolean intersecting = false;
                for (Sprite object : sittable) {
                    object.update();
                    if(frog.intersects(object)) {
                        if(object instanceof Lilypad) {
                            if(((Lilypad)object).getFrogOn() != null && ((Lilypad)object).getFrogOn() != frog) {
                                loseLife();
                                break;
                            }
                            ((Frog)frog).moveToLilypad(object);
                            ((Lilypad)object).setFrogOn(frog);
                            passiveFrogs.add(frog);
                            respawnFrog();
                            int potentialScore = 50 + 10*(TIME / 500);
                            SCORE += (potentialScore > 0) ? potentialScore : 0;
                            TIME = 30000;
                            checkIfLilypadsFull();
                            break;
                        }
                        ((Frog) frog).setSittingOn(object);

                        intersecting = true;
                    }
                }
                if(!intersecting) ((Frog)frog).setSittingOn(null);

                if(frog.getLoc().y > 0 && frog.getLoc().y < 350) {
                    boolean dead = true; //assume it's dead
                    for(Sprite object : sittable) {
                        if(((Frog)frog).getSittingOn() == object) {
                            dead = false;
                            if(((Frog)frog).getSittingOn().getDir() == Sprite.EAST) {
                                frog.getLoc().translate(((Frog)frog).getSittingOn().getSpeed(), 0);
                            } else if (((Frog)frog).getSittingOn().getDir() == Sprite.WEST) {
                                frog.getLoc().translate(-((Frog)frog).getSittingOn().getSpeed(), 0);
                            }
                            break;
                        }
                    }
                    if(dead) loseLife();
                }

                repaint(); //always the last line.  after updating, refresh the graphics.
            }
        });
        timer.start();

        Timer animations = new Timer(250, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Sprite object : sittable) {
                    if(object instanceof Turtle) {
                        if(turtleSwimFrames < 5) {
                            if(turtleSwimFrames == 0) {
                                object.setPic("turtle1.png", Sprite.WEST);
                            }
                            if (turtleSwimFrames == 1 || turtleSwimFrames == 2 || turtleSwimFrames == 3) {
                                object.setPic("turtle2.png", Sprite.WEST);
                            }
                            if(turtleSwimFrames == 4) {
                                object.setPic("turtle1.png", Sprite.WEST);
                            }
                        } else {
                            turtleSwimFrames = 0;
                        }
                    }
                }
                turtleSwimFrames++;
            }
        });
        animations.start();

        setKeyListener();

        try {
            Helper.playSound("res/background.wav", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkIfLilypadsFull() {
        boolean filled = true;
        for (Sprite object : sittable) {
            if(object instanceof Lilypad) {
                if(((Lilypad)object).getFrogOn() == null) filled = false;
            }
        }
        if(filled) {
            LEVEL++;
            SCORE += 1000;
            LIVES = 3;
            passiveFrogs = new ArrayList<>();
            updateLevelSpeeds();
        }
    }

    public void updateLevelSpeeds() {
        for (Sprite object : sittable) {
            if(object instanceof Lilypad) {
                ((Lilypad)object).setFrogOn(null);
            } else {
                object.setSpeed(object.getSpeed() + 3);
            }
        }
        for(Sprite object : obstacles) {
            object.setSpeed(object.getSpeed() + 3);
        }
    }

    //Our paint method.
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Top pads
        g2.setColor(Color.green);
//        g2.fillRect(0, 0, FRAMEWIDTH, 100);

        // River
        for (int y = 0; y < 350; y+=30) {
            for (int x = 0; x < FRAMEWIDTH; x += 30) {
                g2.drawImage(riverTexture, x, y, null);
            }
        }

        // Middle segment
        for (int x = 0; x < FRAMEWIDTH; x += 30) {
            g2.drawImage(middleTexture, x, 350, null);
        }
//        g2.fillRect(0, 350, FRAMEWIDTH, 50);

        // Road
//        g2.fillRect(0, 400, FRAMEWIDTH, 250);
        for (int y = 400; y < 700; y+=30) {
            for (int x = 0; x < FRAMEWIDTH; x += 30) {
                g2.drawImage(roadTexture, x, y, null);
            }
        }

        // Starting segment
        for (int x = 0; x < FRAMEWIDTH; x += 30) {
            g2.drawImage(startingTexture, x, 650, null);
        }

        // Info box
        g2.setColor(Color.black);
        g2.fillRect(0, 700, FRAMEWIDTH, FRAMEHEIGHT);

        // Score box
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Helvetica", Font.BOLD, 30));
        g2.drawString("SCORE:", 5, 735);
        g2.setColor(Color.RED);
        g2.drawString(String.valueOf(SCORE), 130, 735);

        // Lives

        g2.drawString("LIVES: ", 285, 735);
        g2.setColor((LIVES <= 1) ? Color.RED : Color.GREEN);
        g2.drawString(String.valueOf(LIVES), 390, 735);

        // Time
        g2.setColor(Color.GREEN);
        int offset = (int) ((TIME / 30000.0) * 200);
        g2.fillRect(460 + (200-offset), 710, offset, 30);
        g2.setColor(Color.YELLOW);
        g2.drawString("TIME", 670, 735);

        for(Sprite obstacle : obstacles) {
            obstacle.draw(g2);
        }

        for(Sprite object : sittable) {
            object.draw(g2);
        }

        for(Sprite frog : passiveFrogs) {
            frog.draw(g2);
        }

        frog.draw(g2);
    }

    public void moveTheFrog() throws Exception {
        if(!((Frog)frog).isMovable()) return;
        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
            frog.setDir(Sprite.NORTH);
            frog.update();
            keys[KeyEvent.VK_W] = false;
            keys[KeyEvent.VK_UP] = false;
            frogJumpFrames = 0;
            if(frog.getLoc().y < TRAVELED_TO) {
                SCORE += 10;
                TRAVELED_TO = frog.getLoc().y;
            }

        }
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
            frog.setDir(Sprite.WEST);
            frog.update();
            keys[KeyEvent.VK_A] = false;
            keys[KeyEvent.VK_LEFT] = false;
            frogJumpFrames = 0;
        }
        if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
            frog.setDir(Sprite.SOUTH);
            frog.update();
            keys[KeyEvent.VK_S] = false;
            keys[KeyEvent.VK_DOWN] = false;
            frogJumpFrames = 0;
        }
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
            frog.setDir(Sprite.EAST);
            frog.update();
            keys[KeyEvent.VK_D] = false;
            keys[KeyEvent.VK_RIGHT] = false;
            frogJumpFrames = 0;
        }
    }

    /*
      You probably don't need to modify this keyListener code.
       */
    public void setKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                /*intentionally left blank*/
                if(keyEvent.getKeyChar() == 'r') regenerate();
            }

            //when a key is pressed, its boolean is switch to true.
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                keys[keyEvent.getKeyCode()] = true;
            }

            //when a key is released, its boolean is switched to false.
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                keys[keyEvent.getKeyCode()] = false;
            }
        });
    }

    public void loseLife() {
        try {
            Helper.playSound("res/death.wav", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LIVES--;
        SCORE -= (SCORE <= 0) ? 0 : 10;
        TIME = 30000;
        if(LIVES == 0) {
            gameOver();
        } else {
            respawnFrog();
            TRAVELED_TO = frog.getLoc().y;
        }
    }

    public void gameOver() {
        timer.stop();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Helper.playSound("res/sad_trombone.wav", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        JOptionPane.showMessageDialog(window,
                "You died. As a consequence, you must give me a 20/20 on this project.\n\nPress R to restart.",
                "RIP",
                JOptionPane.ERROR_MESSAGE);
    }

    public void respawnFrog() {
        frog = new Frog();
    }

    public void regenerate() {
        keys = new boolean[512]; //should be enough to hold any key code.
        frog = new Frog();
        obstacles = new ArrayList<>();
        sittable = new ArrayList<>();
        passiveFrogs = new ArrayList<>();
        TRAVELED_TO = frog.getLoc().y;

        LIVES = 3;
        SCORE = 0;
        TIME = 30000;
        LEVEL = 1;

        // Cars
        for (int i = 0; i < 5; i++) {
            int numCars = Helper.random(1, numCarsPerRow);
            int direction = (Helper.random(0, 1) == 0) ? Sprite.EAST : Sprite.WEST;
            int xOffset = Helper.random(0, FRAMEWIDTH / numCars);
            int yOffset = 10;
            int speed = Helper.random(2, 9);
            for (int j = 0; j < numCars; j++) {
                obstacles.add(new Vehicle(xOffset + (FRAMEWIDTH / numCars)*j, yOffset + 400 + 50*i, direction, speed));
            }
        }

        // Lawnmower (1);
        obstacles.add(new Vehicle(Helper.random(0, FRAMEWIDTH), 10 + 350, Sprite.WEST, Helper.random(2, 5), 6));

        // Logs/Turtles
        for (int i = 0; i < 5; i++) {
            int direction = (Helper.random(0, 1) == 0) ? Sprite.EAST : Sprite.WEST;
            int xOffset = Helper.random(0, (int)(FRAMEWIDTH / 2.5));
            int yOffset = 10;
            int speed = Helper.random(3, 9);

            if(Helper.random(0, 2) <= 1) {
                sittable.add(new Log(xOffset, yOffset + 100 + 50*i, direction, speed));
                int currentX = xOffset + sittable.get(sittable.size() - 1).getWidth(), j = 1;

                while(currentX < FRAMEWIDTH) {
                    int xRelativeOffset = Helper.random(125, 200);
                    sittable.add(new Log(currentX + xRelativeOffset, yOffset + 100 + 50*i, direction, speed));
                    currentX += xRelativeOffset + sittable.get(j-1).getWidth();
                    j++;
                }
            } else {
//                System.out.println("turtle");
                for (int j = 0; j < 3; j++) {
                    sittable.add(new Turtle(xOffset + 35*j, yOffset + 100 + 50*i, direction, speed));
                }

                int currentX = xOffset + sittable.get(sittable.size() - 1).getWidth();

                while(currentX < FRAMEWIDTH) {
                    int xRelativeOffset = Helper.random(200, 350);
                    for (int j = 0; j < 3; j++) {
                        sittable.add(new Turtle(currentX + xRelativeOffset + 35*j, yOffset + 100 + 50*i, direction, speed));
                    }
                    currentX = sittable.get(sittable.size() - 1).getLoc().x + 35;
                }

            }

        }

        // Lilypads
        for (int i = 0; i < 5; i++) {
            sittable.add(new Lilypad((FRAMEWIDTH*i/5) + 50, 45));
        }

        if(timer != null) timer.start();
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        window = new JFrame("Mingle's Frogger!");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, FRAMEWIDTH, FRAMEHEIGHT + 22); //(x, y, w, h) 22 due to title bar.

        panel = new FroggerMain();
        panel.setSize(FRAMEWIDTH, FRAMEHEIGHT);

        panel.setFocusable(true);
        panel.grabFocus();

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }
}