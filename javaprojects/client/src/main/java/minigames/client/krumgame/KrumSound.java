package minigames.client.krumgame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class KrumSound {

    static String[] soundNames = {"sound2", "jump"};

    static HashMap<String, Clip> sounds;

    static int clientIndex;

    public static void setClientIndex(int i) {
        clientIndex = i;
    }

    public static void initializeSounds(){

        ArrayList<String> names = new ArrayList<>(Arrays.asList(soundNames));

        sounds = new HashMap<String, Clip>();
        for (String s : names) {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                InputStream is = cl.getResourceAsStream(KrumC.soundDir + s + ".wav");
                AudioInputStream a = AudioSystem.getAudioInputStream(is);
                Clip c = AudioSystem.getClip();
                c.open(a);
                sounds.put(s, c);
            } catch (Exception e) {
                System.out.println("error initializing sounds\n");
                e.printStackTrace();
            }            
        }
    }

    public static void playSound(String soundName) {
        // for the sake of our ears while testing, suppress sounds on the second client
        if (clientIndex == 1) return;

        System.out.println("playing sound " + soundName + "\n");
        
        // stop the sound if it's currently playing
        sounds.get(soundName).stop();

        // rewind to beginning
        sounds.get(soundName).setFramePosition(0);

        // play
        sounds.get(soundName).start();
    }
}
    

