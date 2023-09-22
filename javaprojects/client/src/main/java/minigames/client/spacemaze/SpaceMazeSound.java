package minigames.client.spacemaze;

import java.net.URL;
import javax.sound.sampled.*;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for the basic sound effects in the game
 *
 * @author Andrew McKenzie
 *
 * All sound files were created by myself in https://sfxr.me
 */
public class SpaceMazeSound {

    // Logger
    private static final Logger logger = LogManager.getLogger(SpaceMazeSound.class);

    // Clip objects for each sound effect
    private static Clip keyClip;
    private static Clip chestClip;
    private static Clip bombClip;
    private static Clip gameOverClip;
    private static Clip newLevelClip;
    private static Clip wormHoleClip;
    private static Clip lifeDownClip;

    // Now using URL objects for the sound file location because File was problematic
    private static URL key;
    private static URL chest;
    private static URL newLevel;
    private static URL gameOver;
    private static URL bomb;
    private static URL wormHole;
    private static URL lifeDown;

    // HashMap to store the loaded sound clips
    private static HashMap<String, Clip> sounds = new HashMap<>();

    /**
     * Constructor
     */
    public SpaceMazeSound() {
    }

    /**
     * Method to play a sound from the sounds HashMap
     *
     * @param sound a string key in the sounds HashMap
     */
    public static void play(String sound) {
        try {
            Clip clipRequested = sounds.get(sound);
            if (clipRequested != null) {
                if (!clipRequested.isRunning()) {
                    clipRequested.setFramePosition(0);
                    clipRequested.start();
                } else {
                    // If it is running and it's called again, this should restart the sound
                    clipRequested.stop();
                    clipRequested.setFramePosition(0);
                    clipRequested.start();
                }
            }
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }
    }

    /**
     * Method to load all sounds on object instantiation
     * Each sound is added to a HashMap for being played
     * These are all keeped open while SpaceMazeGame is running.
     *
     * Sound files were created by myself on https://sfxr.me
     */
    public void loadSounds() {

        try {
            key = getClass().getResource("/sounds/spacemaze/key.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(key);
            keyClip = AudioSystem.getClip();
            keyClip.open(audioInputStream);
            sounds.put("key", keyClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }

        try {
            newLevel = getClass().getResource("/sounds/spacemaze/levelUp.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(newLevel);
            newLevelClip = AudioSystem.getClip();
            newLevelClip.open(audioInputStream);
            sounds.put("newlevel", newLevelClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }

        try {
            chest = getClass().getResource("/sounds/spacemaze/chest.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(chest);
            chestClip = AudioSystem.getClip();
            chestClip.open(audioInputStream);
            sounds.put("chest", chestClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }

        try {
            gameOver = getClass().getResource("/sounds/spacemaze/gameOver.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(gameOver);
            gameOverClip = AudioSystem.getClip();
            gameOverClip.open(audioInputStream);
            sounds.put("gameover", gameOverClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }

        try {
            bomb = getClass().getResource("/sounds/spacemaze/explosion.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bomb);
            bombClip = AudioSystem.getClip();
            bombClip.open(audioInputStream);
            sounds.put("bomb", bombClip);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            wormHole = getClass().getResource("/sounds/spacemaze/wormHole.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wormHole);
            wormHoleClip = AudioSystem.getClip();
            wormHoleClip.open(audioInputStream);
            sounds.put("wormhole", wormHoleClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }

        try {
            lifeDown = getClass().getResource("/sounds/spacemaze/life.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(lifeDown);
            lifeDownClip = AudioSystem.getClip();
            lifeDownClip.open(audioInputStream);
            sounds.put("lifedown", lifeDownClip);
        } catch (Exception e) {
            logger.error("Sound loading failed: {} ", e);
        }
    }

    /**
     * Closing the clips, to be called when game over
     */
    public static void closeSounds() {
        keyClip.close();
        newLevelClip.close();
        chestClip.close();
        gameOverClip.close();
        bombClip.close();
        wormHoleClip.close();
        lifeDownClip.close();
    }
}