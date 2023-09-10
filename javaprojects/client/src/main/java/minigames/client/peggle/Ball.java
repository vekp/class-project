package minigames.client.peggle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ball {

    private float xDelta, yDelta, xVelocity, yVelocity;
    private int x, y, size;
    boolean active;
    private static BufferedImage ballImage;
    private static final float GRAVITY = 0.2f; // Gravity constant

    static {
        try {
            ballImage = ImageIO.read(new File("./javaprojects/client/src/main/java/minigames/client/peggle/assets/objects/ball.png"));
        } catch (IOException e) {
            System.out.println("Error loading ball image. \nError message: " + e.getMessage());
        }
    }

    public Ball(int x, int y, boolean active, int size) {
        this.x = x;
        this.y = y;
        this.xDelta = 0;
        this.yDelta = 0;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.active = active;
        this.size = size;
    }

    public void shoot(float angle, double speed) {
        this.xVelocity = (float) (speed * Math.cos(angle));
        this.yVelocity = (float) (speed * Math.sin(angle));
    }

    public void updateBall(int leftWall, int rightWall, int floorYValue) {
        if (active) {
            bounceOffWalls(leftWall, rightWall);
            yVelocity += GRAVITY; // Apply gravity to yVelocity
            moveBall();
            ballActive(floorYValue);
        }
    }

    private void moveBall() {
        this.x += xVelocity;
        this.y += yVelocity;
    }

    private void bounceOffWalls(int leftWall, int rightWall) {
        if (x < leftWall || x > rightWall - this.size) {
            xVelocity = -xVelocity;
        }
    }

    private void ballActive(int floorYValue) {
        if (this.y > floorYValue) {
            this.active = false;
        }
    }

    public void drawBall(Graphics g) {
        if (active) {
            g.drawImage(ballImage, x, y, size, size, null);
        }
    }

    public int getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void bounceOffObject() {
        yDelta *= -1;
        x -= xDelta; // Move back to the previous position
        y -= yDelta;
    }



    // Getter for xDelta and yDelta
    public float getXDelta() {
        return xDelta;
    }

    public float getYDelta() {
        return yDelta;
    }


    public Rectangle getFutureBounds() {
        return new Rectangle((int) (x + xDelta), (int) (y + yDelta), size, size);
    }




}
