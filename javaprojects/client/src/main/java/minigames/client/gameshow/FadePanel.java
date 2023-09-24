package minigames.client.gameshow;

import javax.swing.*;

import java.awt.*;

public class FadePanel extends JPanel {

    private double alpha = 1;

    public FadePanel() {
        setOpaque(false);
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double num) {
        alpha = num;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive((float) getAlpha()));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g2d);
        g2d.dispose();
    }
}
