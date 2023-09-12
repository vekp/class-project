package minigames.client.krumgame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class KrumSound {

    static String[] soundNames = {"wahwah","applause","intro2","gunclick2", "grenadeclick3","laser1","ow","bazookaexplode","metal", "explode2", "fall","punch","bazooka", "grenadethrow","rope", "transition", "boing2", "rope2", "jump", "joeygiggle"};

    static HashMap<String, Clip> sounds;

    static int clientIndex;

    static boolean muted;

    public static void setMuted(boolean m) {
        muted = m;
    }

    public static void toggleMuted() {
        muted = !muted;
    }

    public static void setClientIndex(int i) {
        clientIndex = i;
        if (clientIndex > 0) {
            muted = true;
        }
    }

    public static void initializeSounds(){

        ArrayList<String> names = new ArrayList<>(Arrays.asList(soundNames));
        muted = false;
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
        
        if (muted) return;

        System.out.println("playing sound " + soundName);
        
        // stop the sound if it's currently playing
        sounds.get(soundName).stop();

        // rewind to beginning
        sounds.get(soundName).setFramePosition(0);

        // play
        sounds.get(soundName).start();
    }
}
    

