package minigames.server.spacemaze;

import java.awt.Point;
import minigames.spacemaze.SpaceEntity;

/**
 *   Class for players
 *
 *   @author Nikolas Olins
 */

public class SpacePlayer extends SpaceEntity {
    
    private int numKeys;
    private int playerScore;
    private int numLives;
    private boolean lostLives;

    public SpacePlayer(Point startLocation, int lives) {
        super(startLocation);
        this.numKeys = 0;
        this.playerScore = 0;
        this.numLives = lives;
        this.lostLives = false;
    }

    /**
     * Public method for calculating the player's score.
     * @param timeTaken the current time taken inside the maze level.
     * @param initialScore the initial score the player started the level with.
     * @return an int of the player's current score.
     */
    public int calculateScore(long timeTaken, int initialSCore) {
        int reductionFactor = 20;

        // To add to the score between each level.
        int subTotalScore = (int)(initialSCore - (reductionFactor*timeTaken));

        return playerScore += subTotalScore;
    }

    /**
     * Public method for reseting the players attributes between games
     */
    public void resetPlayer(){
        this.numKeys = 0;
        this.playerScore = 0;
        this.numLives = 5;
        this.lostLives = false;
    }

     /**
     * Public method for getting the number of keys the player has.
     * @return an int of the quantity of keys the player has.
     */
    public int checkNumberOfKeys() {
        return numKeys;
    }
     /**
     * Public method for adding a key to the player.
     */
    public void addKey() {
        numKeys++;
    }
    /**
     * Public method for resestting the number of keys back to zero.
     */
    public void resetKeys() {
        numKeys = 0;
    }
    /**
     * Public method for getting the player's current score.
     * @return an int of the current score.
     */
    public int getPlayerScore(){
        return this.playerScore;
    }
    /**
     * Public method for removing lives from the player.
     * @param numRemove number of lives to remove off the player.
     */
    public void removeLife(int numRemove) {
        lostLives = true;
        if(numLives - numRemove < 0) {
            throw new IllegalArgumentException("Taking away that many lives would result in a negative number of lives.");
        }
        numLives -= numRemove;
    }
     /**
     * Public method for getting the player's current number of lives
     * @return an int of the number of lives.
     */
    public int getLives() {
        return numLives;
    }
    /**
     * Public method for determining if the player has ever lost a life.
     * @return bool if the player has ever lost a life.
     */
    public boolean hasLostLives() {
        return lostLives;
    }
}

