package minigames.client.snake;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.Objects;

/**
 * The MultimediaManager class is responsible for managing and providing multimedia resources
 * such as images and sounds for the Snake game UI.
 */
public class MultimediaManager {

    // ImageResources for all the images
    private static final ImageResource PHONE_BACKGROUND = new ImageResource("/images/snake/gameBackgroundNokia.png");
    private static final ImageResource BACKGROUND_COLOR_BLOCK = new ImageResource("/images/snake/backgroundColorBlock.png");
    private static final ImageResource SNAKE_LOGO = new ImageResource("/images/snake/snakeLogo.png");
    private static final ImageResource IMAGE_APPLE = new ImageResource("/images/snake/apple.png");
    private static final ImageResource IMAGE_CHERRY = new ImageResource("/images/snake/cherry.png");
    private static final ImageResource IMAGE_ORANGE = new ImageResource("/images/snake/orange.png");
    private static final ImageResource IMAGE_WATERMELON = new ImageResource("/images/snake/watermelon.png");

    // Background sound clip
    private static Clip backgroundSound;

    // Paths to sounds
    private static final String SOUND_8_BIT = "/sounds/8Bit.wav";
    private static final String SOUND_EAT_APPLE = "/sounds/eatApple.wav";
    private static final String SOUND_MENU = "/sounds/menu.wav";
    private static final String SOUND_RETRO = "/sounds/retro1.wav";
    private static final String SOUND_SMOOTH = "/sounds/smooth.wav";
    private static final String SOUND_SURF = "/sounds/surf.wav";

    /**
     * Constructs a MultimediaManager.
     */
    private MultimediaManager() {
        // Empty constructor
    }

    // Getters for ImageResource

    /**
     * Gets the ImageResource for the phone background image.
     *
     * @return ImageResource for the phone background image.
     */
    public static ImageResource getPhoneBackground() {
        return PHONE_BACKGROUND;
    }

    /**
     * Gets the ImageResource for the background color block image.
     *
     * @return ImageResource for the background color block image.
     */
    public static ImageResource getBackgroundColorBlockResource() {
        return BACKGROUND_COLOR_BLOCK;
    }

    /**
     * Gets the ImageResource for the snake logo image.
     *
     * @return ImageResource for the snake logo image.
     */
    public static ImageResource getSnakeLogoResource() {
        return SNAKE_LOGO;
    }

    /**
     * Gets the ImageResource for the apple image.
     *
     * @return ImageResource for the apple image.
     */
    public static ImageResource getAppleResource() {
        return IMAGE_APPLE;
    }

    /**
     * Gets the ImageResource for the cherry image.
     *
     * @return ImageResource for the cherry image.
     */
    public static ImageResource getCherryResource() {
        return IMAGE_CHERRY;
    }

    /**
     * Gets the ImageResource for the orange image.
     *
     * @return ImageResource for the orange image.
     */
    public static ImageResource getOrangeResource() {
        return IMAGE_ORANGE;
    }

    /**
     * Gets the ImageResource for the watermelon image.
     *
     * @return ImageResource for the watermelon image.
     */
    public static ImageResource getWatermelonResource() {
        return IMAGE_WATERMELON;
    }

    // Methods related to sound

    /**
     * Plays a looping background sound based on the provided sound name.
     * The sound is selected from a predefined set of sounds, and if the name
     * doesn't match any of the predefined sounds, the method will exit without
     * playing any sound.
     *
     * @param soundName The name of the sound to be played. The name should correspond
     *                  to one of the predefined sound names (e.g., "8Bit", "EatApple").
     */
    public static void playBackgroundSound(String soundName) {
        // Stop any currently playing background sound
        stopBackgroundSound();

        String soundPath;

        // Determine the sound file path based on the provided sound name
        switch (soundName) {
            case "8Bit":
                soundPath = SOUND_8_BIT;
                break;
            case "EatApple":
                soundPath = SOUND_EAT_APPLE;
                break;
            case "Menu":
                soundPath = SOUND_MENU;
                break;
            case "Retro":
                soundPath = SOUND_RETRO;
                break;
            case "Smooth":
                soundPath = SOUND_SMOOTH;
                break;
            case "Surf":
                soundPath = SOUND_SURF;
                break;
            default:
                return; // Exit if the sound name doesn't match any predefined names
        }

        try {
            // Load the audio file using the determined sound path
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(MultimediaManager.class.getResource(soundPath)));
            backgroundSound = AudioSystem.getClip();
            backgroundSound.open(audioStream);

            // Add a listener to ensure the sound clip loops continuously once it finishes playing
            backgroundSound.addLineListener(event -> {
                if (LineEvent.Type.STOP.equals(event.getType())) {
                    backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
                }
            });

            // Start playing the sound in a continuous loop
            backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions encountered during the sound playback process
        }
    }


    /**
     * Stops the currently playing background sound if there's any sound playing.
     * This method ensures that the sound is stopped gracefully without causing any
     * disruptions or errors.
     */
    public static void stopBackgroundSound() {
        if (backgroundSound != null && backgroundSound.isRunning()) {
            // TODO : Test this over normal close - issue with stop method before
            backgroundSound.close();
            backgroundSound = null;
        }
    }
}
