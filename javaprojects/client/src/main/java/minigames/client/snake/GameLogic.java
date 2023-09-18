package minigames.client.snake;

public class GameLogic {

    private boolean isGamePaused;

    public GameLogic() {
        isGamePaused = false;
    }

    /**
     * Pause the game.
     */
    public void pauseGame() {
        if (!isGamePaused) {
            isGamePaused = true;
            MultimediaManager.playBackgroundSound(GameConstants.GAME_PAUSE_MUSIC);
        }
    }

    /**
     * Resume the game.
     */
    public void resumeGame() {
        if (isGamePaused) {
            isGamePaused = false;
            MultimediaManager.playBackgroundSound(GameConstants.GAME_PLAY_MUSIC);
        }
    }

    /**
     * Check if the game is paused.
     *
     * @return True if the game is paused, false otherwise.
     */
    public boolean isGamePaused() {
        return isGamePaused;
    }

    // Add more game logic methods as needed...
}
