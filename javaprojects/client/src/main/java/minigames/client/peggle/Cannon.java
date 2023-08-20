package minigames.client.peggle;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * Represents a cannon that can be rotated and drawn on the screen.
 */
public class Cannon {
    int x, y;  // Position of the cannon
    double angle; // Angle at which the cannon is pointing
    private BufferedImage cannonImage; //Image of the cannon

    /*
     * Constructor that initializes the cannon with the specified position.
     *
     * @param x The x-coordinate of the cannon's position.
     * @param y The y-coordinate of the cannon's position.
     */
    public Cannon(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        try {
            this.cannonImage = ImageIO.read(new File("./javaprojects/client/src/main/java/minigames/client/peggle/assets/objects/cannon02.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sets the angle of the cannon to the specified value (in radians),
     * limited to a range of 30 to 150 degrees.
     *
     * @param angle The angle to set the cannon to, in radians.
     */
    public void setAngle(double angle) {
        // Convert degrees to radians
        double minAngle = Math.toRadians(30); // 30 degrees
        double maxAngle = Math.toRadians(150);  // 150 degrees

        // Limit the angle to the specified range
        this.angle = Math.max(minAngle, Math.min(maxAngle, angle));
    }

    /*
     * Draws the cannon on the screen with scaling and rotation.
     *
     * @param g The graphics object used for drawing.
     */
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

    /*
     * Sets the x-coordinate of the cannon's position.
     *
     * @param x The new x-coordinate for the cannon's position.
     */
    public void setX(int x) {
        this.x = x;
    }
}
