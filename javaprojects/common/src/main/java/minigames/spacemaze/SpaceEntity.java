package minigames.spacemaze;
import java.awt.Point;

/**
 * Abstract base class for bots and players.
 * @author Nikolas Olins
 *  
 */

public abstract class SpaceEntity {
    protected Point location;

    /**
     * Constructor
     * @param startLocation a point representing the starting location.
     */
    public SpaceEntity(Point startLocation) {
        this.location = new Point(startLocation);

    }
    /**
     * Public method to return the starting location as a Point.
     * @param startLocation a point representing the starting location.
     * @return a Point of the start location.
     */
    public Point getLocation() {
        return location;

    }

    /**
     * Public method to update the current location
     * @param newLocation a point representing the new location.
     */
    public void updateLocation(Point newLocation) {
        location = new Point(newLocation);
        
    }
}