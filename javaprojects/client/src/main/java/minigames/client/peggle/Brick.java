package minigames.client.peggle;

import java.awt.*;

public class Brick {
    private int x, y, width, height;
    public boolean isHit;



    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isHit = false;
    }

    public void draw(Graphics g) {
        if (!isHit) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean checkCollision(Ball ball) {
        // Calculate the bounds of the brick and ball
        Rectangle brickBounds = new Rectangle(x, y, width, height);
        Rectangle ballBounds = new Rectangle(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());
        // Check for collision between ball and brick
        return brickBounds.intersects(ballBounds);
    }

    public boolean isHit() {
        return isHit;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}

