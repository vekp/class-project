package minigames.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * A class to store miscellaneous pure static functions to be used anywhere in the project.
 */
public final class MinigameUtils {

    /**
     * Makes a scaled image of desired size.
     * Author: Vincent Ekpanyaskun
     * @param filepath String with filepath of the source image
     * @param targetSize int representing desired size to scale image to - it will be the longer dimension of height/width.
     * @return ImageIcon after it has been scaled.
     */
    public static ImageIcon scaledImage(String filepath, int targetSize) {
        ImageIcon imageIcon = new ImageIcon(filepath);
        float aspectRatio = (float) imageIcon.getIconWidth() / imageIcon.getIconHeight();
        int targetWidth;
        int targetHeight;
        if (aspectRatio > 1) {
            targetWidth = targetSize;
            targetHeight = (int) (targetSize / aspectRatio);
        } else {
            targetWidth = (int) (targetSize * aspectRatio);
            targetHeight = targetSize;
        }
        Image scaledImage = imageIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        imageIcon.setImage(scaledImage);
        return imageIcon;
    }

    /**
     * Create a vertical scroll pane of the given content
     * Author: Vincent Ekpanyaskun
     */
    public static JScrollPane generateScrollPane(Component content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }
}
