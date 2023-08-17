package minigames.server.spacemaze;

import java.awt.Point;

/*
 *   Class for players
 *
 *   @author Nikolas Olins
 */

public class SpacePlayer extends SpaceEntity {
    
    private int numKeys;

    public SpacePlayer(Point startLocation) {
        super(startLocation);
        this.numKeys = 0;
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

