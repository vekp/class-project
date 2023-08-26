package minigames.server.spacemaze;

/**
 * GameTimer class to track the elapsed time in a given game
 *
 * @author Andrew McKenzie
 */
/*
*  Currently cannot end the game from a paused state and recieve your total time.
*  I can adjust for that if needed but I think to recieve a score the game
*  should end from a running state.
* */
public class GameTimer {

    // Time when timer was started
    private long startTime;

    // Total ellapsed time before the game was last paused
    private long prePausedTime;

    // Time when timer was stopped, at end of game
    private long endTime;

    // Booleans for checks
    private boolean timerRunning;
    private boolean timerWasUsed;
    private boolean gameFinished;

    /**
     * Constructor for a timer
     */
    public GameTimer(){
        this.timerWasUsed = false;
        this.prePausedTime = 0;
        this.timerRunning = false;
    }

    /**
     * Starts the timer
     */
    public void startTimer() {
        if (!timerRunning) {
            startTime = System.currentTimeMillis();
            timerRunning = true;
            timerWasUsed = true;
        }
    }

    /**
     * Lets you check if the timer is running
     */
    public boolean getIsTimerRunning() {
        return timerRunning;
    }

    /**
     * Lets you pause the timer
     */
    public void pauseTimer() {
        if (timerRunning) {
            prePausedTime += System.currentTimeMillis() - startTime;
            timerRunning = false;
        }
    }

    /**
     * To be called when the game is over
     */
    public void stopTimer() {
        if (timerRunning) {
            endTime = System.currentTimeMillis();
            timerRunning = false;
            gameFinished = true;
        }
    }

    /**
     * @return The current time ellapsed between each level
     */
    public int getSubTotalTime() {
        return (int) (prePausedTime / 1000);
    }

    /**
     * Calculates the total time the game has been running
     * @return String of the time in minutes and seconds
     */
    public String getCurrentTime(){
        int currentTime = (int) (((System.currentTimeMillis() - startTime) + prePausedTime) / 1000);
        int minutes = currentTime / 60;
        int seconds = currentTime % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Getter checks timer was started and is currently not running,
     * to be used for calculating the score
     * @return Total time in seconds that the game ran for
     */
    public int getTimeTaken() {
        if(gameFinished && timerWasUsed) {
            return (int) (((endTime - startTime + prePausedTime) / 1000));
        } else {
            // Either timer is still running or timer was never started
            return 0;
        }
    }
}