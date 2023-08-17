package minigames.server.spacemaze;

import java.awt.Point;

/*
 * Base class for bots and players.
 *  @author Nikolas Olins
 */

public abstract class SpaceEntity {
    protected Point location;

    public SpaceEntity(Point startLocation)
    {
        this.location = new Point(startLocation);
    }
    // getter for location
    public Point getLocation() {
        return location;
    }
    // update the current location
    public void updateLocation(Point newLocation) {
        location = new Point(newLocation);
    }
}