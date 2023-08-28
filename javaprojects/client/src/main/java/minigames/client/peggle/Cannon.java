import java.awt.Graphics;
import java.awt.Color;

public class Cannon {
    int x, y;  // position of the cannon
    double angle; // angle of the cannon

    public Cannon(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void draw(Graphics g) {
        int length = 70;  // Length of the cannon
        int endX = x + (int)(length * Math.cos(angle));
        int endY = y + (int)(length * Math.sin(angle));

        g.setColor(Color.RED);
        g.drawLine(x, y, endX, endY);
    }

    //To center the cannon based on window width
    public void setX(int x) {
        this.x = x;
    }
}
