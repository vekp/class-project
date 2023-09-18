package minigames.client.snake;

import java.awt.*;

/**
 * GameConstants class holds common constants used across multiple classes in the Snake game.
 */
public final class GameConstants {
    // Dimensions
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 50;
    public static final int START_BUTTON_Y = 180;
    public static final int BUTTON_GAP = 10;
    public static final int LOGO_Y = 40;

    // Music
    public static final String MENU_MUSIC = "8Bit";
    public static final String GAME_PLAY_MUSIC = "Menu";
    public static final String GAME_PAUSE_MUSIC = "Smooth";
    public static final String EXIT_GAME = "Exit";

    // Colors
    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(18, 96, 98);
    public static final Color HOVER_BACKGROUND_COLOR = new Color(87, 26, 128);
    public static final Color DEFAULT_BORDER_COLOR = new Color(18, 144, 147);
    public static final Color HOVER_BORDER_COLOR = new Color(87, 26, 128);
    public static final Color INFO_TITLE_COLOR = new Color(18, 96, 98);
    public static final Color INFO_MESSAGE_COLOR = new Color(87, 26, 128);

    // Panel names
    public static final String MAIN_MENU_PANEL = "Main Menu";
    public static final String PLAY_PANEL = "Play";
    public static final String HELP_MENU_PANEL = "Help Menu";
    public static final String ABOUT_ME_PANEL = "About Me";
    public static final String ACHIEVEMENTS_PANEL = "Achievements";

    // Fonts
    public static final Font INFO_TITLE_FONT = new Font("SansSerif", Font.BOLD, 42);
    public static final Font INFO_MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 24);
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    // ButtonFactory constants
    public static final int BORDER_THICKNESS = 2;
    public static final int BORDER_PADDING = 10;

    // Button Text
    public static final String RETURN_BUTTON_TEXT = "Main Menu";

    // InformationPanel constants
    public static final int INFO_TITLE_Y = 190;
    public static final int INFO_TEXT_WIDTH = 500;
    public static final int INFO_TEXT_HEIGHT = 200;
    public static final int INFO_TEXT_Y = 250;
    public static final String GAME_RULES_TITLE = "Game Rules";
    public static final String ABOUT_ME_TITLE = "About";
    public static final String[] GAME_RULES_MESSAGES = {
            "Navigate the maze with arrow keys and lead the snake on an exciting adventure! Feast on delicious fruits to grow and boost your score!",
            "Stay sharp! Avoid walls and don't bite your tail or it's game over. Need a breather? Hit the space bar to take a pause and strategize."
    };
    public static final String[] ABOUT_ME_MESSAGES = {
            "Developed by Luke, Sean & Matt (Group 15) COSC220 A3, 2023.",
            "Credits",
            "Music: Fesliyan Studios (www.FesliyanStudios.com)",
            "Graphics: [Supplier Name and URL]"
    };

    public static final int RETURN_BUTTON_Y = 450;

    // Constructor - private to prevent instantiation
    private GameConstants() {}
}
