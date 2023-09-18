package minigames.client.peggle;


import java.awt.Graphics;
import java.awt.Color;

public class Cannon {
    private int x, y;  // position of the cannon
    private double angle; // angle of the cannon



    private static final int cannonLength = 90;

    public Cannon(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void draw(Graphics g) {
        int endX = x + (int)(cannonLength * Math.cos(angle));
        int endY = y + (int)(cannonLength * Math.sin(angle));

        g.setColor(Color.RED);
        g.drawLine(x, y, endX, endY);
    }

    //To center the cannon based on window width
    public void setX(int x) {
        this.x = x;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }


}
