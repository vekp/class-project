package minigames.server.spacemaze;

import java.awt.Point;

/*
 *   Class for players
 *
 *   @author Nikolas Olins
 */

public class SpacePlayer extends SpaceEntity {
    
    private int numKeys;

    private int playerScore;

    public SpacePlayer(Point startLocation) {
        super(startLocation);
        this.numKeys = 0;
        this.playerScore = 0;
    }

    // method for calculating the score, could update on the UI (reduce as game progesses) or be called at the end.
    public int calculateScore(long timeTaken, int initialSCore) {
        int reductionFactor = 20;

        // To add to the score between each level.
        int subTotalScore = (int)(initialSCore - (reductionFactor*timeTaken));

        return playerScore += subTotalScore;
    }

    public int checkNumberOfKeys() {
        return numKeys;
    }

    public void addKey() {
        numKeys++;
    }

    public int getPlayerScore(){
        return this.playerScore;
    }
}

