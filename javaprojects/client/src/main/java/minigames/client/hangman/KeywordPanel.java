package minigames.client.hangman;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import javax.swing.*;

public class KeywordPanel extends JPanel {
    JLabel[] letterLabels = new JLabel[26];
    boolean[] letterUsed = new boolean[26];
    Color borderColour = new Color(146, 106, 61);

    KeywordPanel() {
        Arrays.fill(letterUsed, false);
        setLayout(null);
        for (int i = 0; i < 26; i++) {
            letterLabels[i] =
                    new JLabel(Character.toString((char) ('A' + i)), SwingConstants.CENTER);
        }
        for (JLabel label : letterLabels) {
            add(label);
        }
        addComponentListener(new ResizeListener());
        this.setBorder(BorderFactory.createMatteBorder(10, 0, 10, 10, borderColour));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color start = new Color(61, 158, 203);
        Color end = new Color(63, 116, 138);
        GradientPaint gradient = new GradientPaint(0, 0, start, w, h, end);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, w, h);
    }

    /**
     * The resetLetters function resets the letterUsed array to false.
     *
     */
    void resetLetters() {
        Arrays.fill(letterUsed, false);
        updatePanel();
    }

    /**
     * The useLetter function accepts a character and sets the corresponding element of letterUsed
     * to true.
     *
     * @param letter Store the letter that is passed in
     */
    void useLetter(char letter) {
        letterUsed[letter - 'A'] = true;
        updatePanel();
    }

    /**
     * The updatePanel function updates the panel by changing the font size and position of each
     * letter label. It does this by calling getWidth() and getHeight() to find out how big the
     * panel is, then uses that information to calculate where each letter should be positioned. The
     * function also changes the color of letters that have been guessed correctly so they are no
     * longer greyed out.
     */
    void updatePanel() {
        int fontSize = getWidth() / 8;
        Font letterFont = new Font("Verdana", Font.PLAIN, fontSize);

        // Print black or greyed out letters in their positions
        for (int i = 0; i < 26; i++) {
            if (letterUsed[i]) {
                letterLabels[i].setForeground(new Color(0, 0, 0, 120));
            } else {
                letterLabels[i].setForeground(Color.WHITE);
            }
            letterLabels[i].setFont(letterFont);
            // x coords go from 0/7 to 6/7 (return to 0 every 7 chars)
            // y coords go from 0/4 to 3/4 (go down one row every 7 chars)
            letterLabels[i].setBounds(
                    (getWidth() - 10) * (i % 6) / 6,
                    getHeight() * (i / 6) / 5,
                    (getWidth() - 10) / 5,
                    getHeight() / 5);
        }
    }

    class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            updatePanel();
        }
    }
}
