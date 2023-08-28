import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Ball {

    private float xDelta, yDelta, xVelocity, yVelocity;
    private int x, y, size, ballNumber;
    private boolean active;

    public Ball(int x, int y, float deltax, float deltay, float xVelocity, float yVelocity, boolean active, int ballNumber, int size) {
        this.x = x;
        this.y = y;
        this.xDelta = deltax;
        this.yDelta = deltay;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.active = active;
        this.ballNumber = ballNumber;
        this.size = size;
    }

    public void updateBall(int leftWall, int rightWall, int floorYValue){
        if (this.active == true){
            bounceOffWalls(leftWall, rightWall);
            moveBall();
            ballActive(floorYValue);
        }
    }

    // Method for drawing a ball instance to screen
    public void drawBall(Graphics g){
        if (this.active == true) {
            BufferedImage ball = null;
            try {
                ball = ImageIO.read(new File("./javaprojects/client/src/main/java/minigames/client/peggle/assets/objects/ball.png"));
            } catch (IOException e) {
                System.out.println("Error loading file. \nError message: " + e.getMessage());
            }
            g.drawImage(ball, x, y, this.size, this.size, null);
        }
    }

    // Method that changes ball instances location
    private void moveBall(){
        if (this.xDelta < 0) {
            this.xDelta += this.xVelocity;
        } else if (this.xDelta > 0) {
            this.xDelta -= this.xVelocity;
        }
        this.yDelta += this.yVelocity;
        this.x += this.xDelta;
        this.y += this.yDelta;
    }

    // Method organises ball bouncing off edge of screen
    // TODO: Jarrod needs to align this with his image when implemented so the ball appears to bounce correctly
    private void bounceOffWalls(int leftWall, int rightWall){
        if (x < leftWall) {
            xDelta *= -1;
        }

        if (x > rightWall - this.size) {
            xDelta *= -1;
        }
    }

    private void ballActive(int floorYValue){
        if (this.y > floorYValue) {
            this.active = false;
        }
    }

    public int getSize(){
        return this.size;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}