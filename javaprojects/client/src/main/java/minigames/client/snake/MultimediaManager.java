package minigames.client.snake;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.util.Objects;

/**
 * The MultimediaManager class is responsible for managing and providing multimedia resources
 * such as images and sounds for the Snake game UI.
 */
public class MultimediaManager {

    // Background sound clip
    private Clip backgroundSound;

    // Path to the snake game's start menu image
    private static final String snakeStartMenuPath = "/snake/snake_image.png";

    /**
     * Default constructor for MultimediaManager.
     */
    public MultimediaManager() {
        // Currently empty, can be used for future initialization if necessary.
    }

    /**
     * Retrieves the background image icon for the snake game's start menu.
     *
     * @return ImageIcon representing the start menu's background image.
     */
    static ImageIcon getImage() {
        return new ImageIcon(Objects.requireNonNull(SnakeUI.class.getResource(snakeStartMenuPath)));
    }

    /**
     * Returns the width of the start menu image.
     *
     * @return width of the start menu image.
     */
    public static int getStartMenuWidth() {
        return getImage().getIconWidth();
    }

    /**
     * Returns the height of the start menu image.
     *
     * @return height of the start menu image.
     */
    public static int getStartMenuHeight() {
        return getImage().getIconHeight();
    }

    /**
     * Plays a looping background sound from a specified file path.
     *
     * @param filePath The path to the sound file to be played.
     */
    void playBackgroundSound(String filePath) {
        try {
            // Load the audio file from the provided directory
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(getClass().getResource(filePath)));
            backgroundSound = AudioSystem.getClip();
            backgroundSound.open(audioStream);

            // Add a listener to loop the sound clip continuously
            backgroundSound.addLineListener(event -> {
                if (LineEvent.Type.STOP.equals(event.getType())) {
                    backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
                }
            });

            // Start playing the sound in a loop
            backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the currently playing background sound.
     */
    public void stopBackgroundSound() {
        if (backgroundSound != null && backgroundSound.isRunning()) {
            backgroundSound.stop();
        }
    }
}
