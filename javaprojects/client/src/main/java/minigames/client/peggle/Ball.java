package minigames.client.peggle;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Ball {

    private float x, y, deltax, deltay, speed;

    public Ball(float x, float y, float deltax, float deltay, float speed, float size) {
        this.x = x;
        this.y = y;
        this.deltax = deltax;
        this.deltay = deltay;
        this.speed = speed;
    }

    // Method for drawing a ball instance to screen
    public void drawToScreen(Graphics g){
        BufferedImage ball = null;
        try {
            ball = ImageIO.read(new File("assets/ball.png"));
        } catch (IOException e) {
            System.out.println("Error loading file. \nError message: " + e.getMessage());
        }
        g.drawImage(ball, (int) x, (int) y, null);
    }

    // Method that changes ball instances location
    public void moveBall(){
        x += deltax;
        y += deltay;
    }

    // Method organises ball bouncing off edge of screen
    // TODO: Jarrod needs to align this with his image when implemented so the ball appears to bounce correctly
    public void bounceOffEdges(int left, int right){
        if (x < left) {
            deltax *= -1;
        }

        if (x > right) {
            deltax *= -1;
        }
    }
}