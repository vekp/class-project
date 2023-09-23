package minigames.client.snake;

import javax.swing.*;
import java.util.Objects;

/**
 * The ImageResource class encapsulates an image resource for the Snake game UI.
 * It provides easy access and manipulation of these resources in the game by
 * storing the image along with its dimensions.
 */
public class ImageResource {

    // The image associated with this resource
    private final ImageIcon resourceImage;

    // Width of the resource image
    private final int imageWidth;

    // Height of the resource image
    private final int imageHeight;

    /**
     * Constructs an ImageResource by loading an image from the provided path.
     * The path should be relative to the location of the image resource within the project.
     *
     * @param imagePath The relative path to the image resource.
     */
    public ImageResource(String imagePath) {
        this.resourceImage = new ImageIcon(
                Objects.requireNonNull(MultimediaManager.class.getResource(imagePath)));
        this.imageWidth = resourceImage.getIconWidth();
        this.imageHeight = resourceImage.getIconHeight();
    }

    /**
     * Returns the ImageIcon associated with this resource.
     *
     * @return The ImageIcon representing this resource.
     */
    public ImageIcon getImageResource() {
        return resourceImage;
    }

    /**
     * Returns the width of the resource image.
     *
     * @return The width of the image in pixels.
     */
    public int getImageResourceWidth() {
        return imageWidth;
    }

    /**
     * Returns the height of the resource image.
     *
     * @return The height of the image in pixels.
     */
    public int getImageResourceHeight() {
        return imageHeight;
    }
}
