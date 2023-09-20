package minigames.client.snake;

import javax.sound.sampled.*;
import java.util.Objects;

/**
 * The MultimediaManager class is responsible for managing and providing multimedia resources
 * such as images and sounds for the Snake game UI.
 */
public class MultimediaManager {

    // ImageResources for all the images
    private static final ImageResource PHONE_BACKGROUND = new ImageResource(
            GameConstants.PHONE_BACKGROUND);
    private static final ImageResource BACKGROUND_COLOR_BLOCK = new ImageResource(
            GameConstants.BACKGROUND_COLOR_BLOCK);
    private static final ImageResource SNAKE_LOGO = new ImageResource(
            GameConstants.SNAKE_LOGO);
    private static final ImageResource IMAGE_APPLE = new ImageResource(
            GameConstants.IMAGE_APPLE);
    private static final ImageResource IMAGE_CHERRY = new ImageResource(
            GameConstants.IMAGE_CHERRY);
    private static final ImageResource IMAGE_ORANGE = new ImageResource(
            GameConstants.IMAGE_ORANGE);
    private static final ImageResource IMAGE_WATERMELON = new ImageResource(
            GameConstants.IMAGE_WATERMELON);
    // Paths to sounds
    private static final String SOUND_8_BIT = GameConstants.SOUND_8_BIT;
    private static final String SOUND_POSTITIVE = GameConstants.SOUND_POSTITIVE;
    private static final String SOUND_NEGATIVE = GameConstants.SOUND_NEGATIVE;
    private static final String SOUND_MENU = GameConstants.SOUND_MENU;
    private static final String SOUND_RETRO = GameConstants.SOUND_RETRO;
    private static final String SOUND_SMOOTH = GameConstants.SOUND_SMOOTH;
    private static final String SOUND_SURF = GameConstants.SOUND_SURF;
    // Image collection
    private static final ImageResource[] FOOD_IMAGES = {IMAGE_APPLE, IMAGE_CHERRY,
            IMAGE_WATERMELON, IMAGE_ORANGE};
    // Background sound clip
    private static Clip backgroundSound;

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
            case "Positive":
                soundPath = SOUND_POSTITIVE;
                break;
            case "Negative":
                soundPath = SOUND_NEGATIVE;
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
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(MultimediaManager.class.getResource(soundPath)));
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
            backgroundSound.close();
            backgroundSound = null;
        }
    }

    /**
     * Plays a custom sound effect once with a specified volume gain.
     *
     * @param soundName   The name of the sound effect to be played.
     */
    public static void playSoundEffect(String soundName) {
        String soundPath;

        // Determine the sound file path based on the provided sound name
        switch (soundName) {
            case "Positive":
                soundPath = SOUND_POSTITIVE;
                break;
            case "Negative":
                soundPath = SOUND_NEGATIVE;
                break;
            default:
                return; // Exit if the sound name doesn't match any predefined names
        }
        try {
            // Load the audio file based on the provided sound path
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(MultimediaManager.class.getResource(soundPath)));
            Clip soundEffect = AudioSystem.getClip();
            soundEffect.open(audioStream);

            // Adjust the volume (gain) of the sound
            if (soundEffect.isControlSupported(FloatControl. Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) soundEffect.getControl(FloatControl.Type.MASTER_GAIN);
                float gain = 1.5f;
                gainControl.setValue(gain);
            }

            // Start playing the sound once
            soundEffect.start();

            // Wait for the sound to finish playing
            while (soundEffect.isRunning()) {
                Thread.sleep(100); // Adjust the sleep duration as needed
            }

            // Close the sound clip
            soundEffect.close();
        } catch (Exception e) {
            e.printStackTrace(); // Log any exceptions encountered during sound playback
        }
    }

    /**
     * Gets the array of distinct food images.
     *
     * @return An array of ImageResource containing the food images.
     */
    public static ImageResource[] getFoodImages() {
        return FOOD_IMAGES.clone(); // Return a clone to prevent external modification
    }

    /**
     * Gets the number of distinct food images.
     *
     * @return The size of the FOOD_IMAGES array.
     */
    public static int getFoodImagesSize() {
        return FOOD_IMAGES.length;
    }

    /**
     * Gets a food image based on the provided index.
     *
     * @param index The index of the desired food image.
     * @return The ImageResource at the specified index in the FOOD_IMAGES array.
     */
    public static ImageResource getFoodImageByIndex(int index) {
        if (index >= 0 && index < FOOD_IMAGES.length) {
            return FOOD_IMAGES[index];
        }
        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for FOOD_IMAGES");
    }

}
