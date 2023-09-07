package minigames.client.hangman;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePanel extends JPanel {
    final BufferedImage[] hangman = new BufferedImage[5];
    int errors;

    ImagePanel() {
        loadImages();
        setErrors(0);
    }

    /**
     * The loadImages function loads the images for the snowman and snow background.
     *
     */
    void loadImages() {
        URL[] hangmanImage = new URL[5];
        try {
            for (int i = 0; i < 5; i++) {
                
                hangmanImage[i] =
                        this.getClass()
                                .getResource("/images/hangman/hangman%d.jpg".formatted(i + 1));
                assert hangmanImage[i] != null;
                this.hangman[i] = ImageIO.read(hangmanImage[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The setErrors function sets the number of errors to a new value.
     *
     * @param errors Set the number of errors in the game
     */
    public void setErrors(int errors) {
        this.errors = errors;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int hangmanIndex = (int) Math.floor((double) errors / 2);
        if (errors == 8) {
            hangmanIndex = 4;
        }
        if (errors <= 7) {
            g.drawImage(
                    hangman[hangmanIndex],
                    getWidth() / 2 - hangman[hangmanIndex].getWidth() / 6,
                    14 * getHeight() / 15 - hangman[hangmanIndex].getHeight() / 3,
                    hangman[hangmanIndex].getWidth() / 3,
                    hangman[hangmanIndex].getHeight() / 1,
                    null);
        }
        g.setFont(new Font("Verdana", Font.BOLD, 18));
        g.setColor(Color.BLACK);
        g.drawString("Guesses Remaining: " + (8 - errors), 340, 30);
    }
}
