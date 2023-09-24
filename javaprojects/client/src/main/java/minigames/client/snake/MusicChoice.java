/**
 * The MusicChoice enum represents different types of music or sound effects
 * that can be played in a snake mini-game client.
 */
package minigames.client.snake;

public enum MusicChoice {
    MENU_MUSIC, // Represents the background music played in the main menu.
    GAME_PLAY_MUSIC, // Represents the background music played during gameplay.
    GAME_PAUSE_MUSIC, // Represents the background music played when the game is paused.
    POSITIVE_REACTION, // Represents a positive reaction sound effect.
    NEGATIVE_REACTION // Represents a negative reaction sound effect, such as colliding with a
    // wall or obstacle.
}
