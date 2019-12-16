import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static void playSound(String soundFile, boolean loop) throws Exception{
        File f = new File("./" + soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();

        clip.open(audioIn);
        if(loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
}
