import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Cannon {
    int x, y;  // position of the cannon
    double angle; // angle of the cannon
    private BufferedImage cannonImage; //Add the cannon image

    public Cannon(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        try {
            this.cannonImage = ImageIO.read(new File("C:/Users/Paula/Desktop/peggle/assets/cannon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Min and Max angles that the cannon can face
    public void setAngle(double angle) {
        // Convert degrees to radians
        double minAngle = Math.toRadians(30); // 30 degrees
        double maxAngle = Math.toRadians(150);  // 150 degrees

        // Limit the angle to the specified range
        this.angle = Math.max(minAngle, Math.min(maxAngle, angle));
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the cannon image with scaling and rotation
        AffineTransform at = new AffineTransform();
        double scale = 0.4; // Change this to the desired scale factor
        at.scale(scale, scale);
        at.translate(x / scale, (y - cannonImage.getHeight(null) / 2) / scale);
        at.rotate(angle + Math.PI / 2, 0, cannonImage.getHeight(null) / 2);
        at.translate(-cannonImage.getWidth(null) / 2, -cannonImage.getHeight(null) / 2);
        g2d.drawImage(cannonImage, at, null);
    }

    public void setX(int x) {
        this.x = x;
    }
}
