package minigames.server.battleship;

/**
 * Enum representing different state of the battleship game
 */
public enum GameState {
    // TODO combine/remove waiting and pendings
    WAITING_JOIN, PENDING_READY, PENDING_START, IN_PROGRESS, GAME_OVER

}
