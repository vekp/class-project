package minigames.client.hangman;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class RiddlePanel extends JPanel {
    boolean gameLost = false;
    boolean gameWon = true;
    JLabel riddleLabel = new JLabel("Default text", SwingConstants.CENTER);

    RiddlePanel(String riddleString) {
        add(riddleLabel);
        setLabelText(riddleString);
        myResize();
    }

    RiddlePanel() {
        add(riddleLabel);
        addComponentListener(new ResizeListener());
        myResize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color start = new Color(230, 233, 240);
        Color end = new Color(238, 241, 245);
        GradientPaint gradient = new GradientPaint(0, 0, start, w, h, end);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, w, h);
    }

    /**
     * The setLabelText function sets the text of the label to a given String.
     *
     * @param puzzleString Set the text of the label
     */
    void setLabelText(String puzzleString) {
        riddleLabel.setText(puzzleString);
    }

    /**
     * The setLabelColour function sets the colour of the label.
     *
     * @param col Set the label colour
     */
    void setLabelColour(Color col) {
        riddleLabel.setForeground(col);
    }

    /**
     * The myResize function is called whenever the window is resized. It sets the font size of
     * puzzleLabel to be proportional to the width and height of this JFrame, but no larger than 20
     * characters wide or 2 lines high.
     *
     */
    void myResize() {
        int stringFontSize = getWidth() * 3 / 2 / Math.min(20, riddleLabel.getText().length());
        stringFontSize = Math.min(stringFontSize, getHeight() / 2);
        Font stringFont = new Font(riddleLabel.getFont().getName(), Font.PLAIN, stringFontSize);
        riddleLabel.setFont(stringFont);
        riddleLabel.setBounds(0, 0, getWidth(), getHeight());
    }

    class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            myResize();
        }
    }
}
