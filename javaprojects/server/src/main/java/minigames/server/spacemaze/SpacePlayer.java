package minigames.server.spacemaze;

/*
 *   Basic player class
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
    public double calculateScore(int timeTaken, int initialSCore) {
        double reductionFactor = 50;

        return initialSCore - (reductionFactor*timeTaken);
    }

    public int checkNumberOfKeys(){
        return numKeys;
    }

    public void addKey()
    {
        numKeys++;
    }

}

