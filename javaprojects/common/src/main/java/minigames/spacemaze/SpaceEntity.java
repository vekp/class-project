package minigames.spacemaze;

import java.awt.Point;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
/*
 * Base class for bots and players.
 *  @author Nikolas Olins
 *  Jpanel and related rendering methods taken from https://github.com/ntedgi/PacMan-Game-Java-Swing/blob/master/src/Character.java
 */

public abstract class SpaceEntity extends JPanel {
    protected Point location;
    protected ImageIcon image;

    public SpaceEntity(Point startLocation)
    {
        this.location = new Point(startLocation);
        //this.image = leftIcon();
    }
    /*
    protected ImageIcon leftIcon() {
        return null;
    }
*/
    /*
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
		image.paintIcon(this, g, location.x, location.y);
    }
*/

    // getter for location
    public Point getLocation() {
        return location;
    }
    // update the current location
    public void updateLocation(Point newLocation) {
        location = new Point(newLocation);
    }
}