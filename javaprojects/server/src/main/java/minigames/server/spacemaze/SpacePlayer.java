package minigames.server.spacemaze;

/*
 *   Basic player class to represent the player.
 *
 *   @author Nikolas Olins
 */
public class SpacePlayer {
    private int currentX;
    private int currentY;
    private int numKeys;

    public SpacePlayer(int startingX, int startingY) {
        this.currentX = startingX;
        this.currentY = startingY;
        this.numKeys = 0;
    }
    // get the current location
    public int[] getLocation() {
        return new int[] {currentX,currentY};
    }
    // update the current location - assuming two integers for input
    public void updateLocation(int newX, int newY) {
        currentX = newX;
        currentY = newY;
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

