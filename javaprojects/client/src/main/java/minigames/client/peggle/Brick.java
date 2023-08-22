import java.awt.*;

public class Brick {
    private int x, y, width, height;
    private boolean isHit;

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
//Jarrod would need to make getters for the ball's dimensions and size
        // Check for collision between ball and brick
        return brickBounds.intersects(ballBounds);
    }

    public boolean isHit() {
        return isHit;
    }
}

