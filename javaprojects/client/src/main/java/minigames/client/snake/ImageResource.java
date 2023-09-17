package minigames.client.snake;

import javax.swing.*;
import java.util.Objects;

/**
 * The ImageResource class represents a multimedia resource (specifically an image)
 * for the Snake game UI. This class encapsulates the image along with its dimensions
 * to provide easy access and manipulation of these resources in the game.
 */
class ImageResource {

    // Image for this resource
    private final ImageIcon image;

    // Dimensions of the image
    private final int width;
    private final int height;

    /**
     * Constructs an ImageResource based on the provided path. The path should point to
     * the location of the image resource within the project.
     *
     * @param path The path to the image resource.
     */
    public ImageResource(String path) {
        this.image = new ImageIcon(Objects.requireNonNull(MultimediaManager.class.getResource(path)));
        this.width = image.getIconWidth();
        this.height = image.getIconHeight();
    }

    /**
     * Retrieves the ImageIcon for this resource.
     *
     * @return ImageIcon representing the image of this resource.
     */
    public ImageIcon getImage() {
        return image;
    }

    /**
     * Retrieves the width of the image.
     *
     * @return Width of the image in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the image.
     *
     * @return Height of the image in pixels.
     */
    public int getHeight() {
        return height;
    }
}
