package minigames.client.krumgame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class KrumSoundPlayer {




    //public static void main(String[] args) {
    //    playSound("sound4.wav");
    //}

    public static void playSound(String filePath) {
        File soundFile = new File(filePath);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // Wait for the sound to finish playing before exiting
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
    

