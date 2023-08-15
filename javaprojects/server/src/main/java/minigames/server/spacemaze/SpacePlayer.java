package minigames.server.spacemaze;

import java.awt.Point;

/*
 *   Basic player class to represent the player.
 *
 *   @author Nikolas Olins
 */
public class SpacePlayer {
    private Point location;
    private int numKeys;

    public SpacePlayer(Point startLocation) {
        this.location = new Point(startLocation);
        this.numKeys = 0;
    }
    // get the current location
    public Point getLocation() {
        return location;
    }
    // update the current location - assuming two integers for input
    public void updateLocation(Point newLocation) {
        location = new Point(newLocation);
    }
    // method for calculating the score, could update on the UI (reduce as game progesses) or be called at the end.
    public int calculateScore(long timeTaken, int initialSCore) {
        int reductionFactor = 50;
        return (int)(initialSCore - (reductionFactor*timeTaken));
    }

    public int checkNumberOfKeys() {
        return numKeys;
    }

    public void addKey() {
        numKeys++;
    }

}

