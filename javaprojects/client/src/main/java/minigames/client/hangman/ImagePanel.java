package minigames.client.hangman;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePanel extends JPanel {
    final BufferedImage[] hangman = new BufferedImage[8];
    final BufferedImage[] background = new BufferedImage[5];
    int errors;
    ImagePanel() {
        loadImages();
        setErrors(0);
    }
    /**
     * The loadImages function loads the images for the hangman.
     *
     */
    void loadImages() {
        URL[] hangmanImage = new URL[8];
        URL[] backgroundImage = new URL[5];
        try {
            for (int i = 0; i < 8; i++){
                hangmanImage[i] =
                        this.getClass()
                                .getResource("/images/hangman/hangman%d.jpg".formatted(i + 1));
                assert hangmanImage[i] != null;
                this.hangman[i] = ImageIO.read(hangmanImage[i]);
            }

            for (int i = 0; i < 5; i++){
                backgroundImage[i] = this.getClass().getResource("/images/backgrounds/hangman/back%d.png".formatted(i + 1));
                assert backgroundImage[i] != null;
                this.background[i] = ImageIO.read(backgroundImage[i]);
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
        int backgroundIndex = (int) Math.ceil((double) errors /2);
        int hangmanIndex = (int) Math.floor((double) errors / 1);
        if (errors == 8) {
            hangmanIndex = 1;
        }
        if (errors <= 8) {
            g.drawImage(background[backgroundIndex], 0,0, getWidth(), getHeight(), null);
            g.drawImage(
                    hangman[hangmanIndex],
                    getWidth() / 2 - hangman[hangmanIndex].getWidth() / 6,
                    14 * getHeight() / 25 - hangman[hangmanIndex].getHeight() / 3,
                    hangman[hangmanIndex].getWidth() / 2,
                    hangman[hangmanIndex].getHeight() / 1,
                    null);
        }
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.BLACK);
        g.drawString("Lives Remaining: " + (8 - errors), 50, 30);
    }
}
