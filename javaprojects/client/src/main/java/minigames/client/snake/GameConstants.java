package minigames.client.snake;

import java.awt.*;

/**
 * GameConstants class holds common constants used across multiple classes in the Snake game.
 */
public final class GameConstants {

    // Dimensions
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 50;
    public static final int START_BUTTON_Y = 250;
    public static final int BUTTON_GAP = 10;
    public static final int LOGO_Y = 10;
    public static final int MINIMUM_FOOD_GENERATION_DELAY = 1;
    public static final int MAXIMUM_FOOD_GENERATION_DELAY = 8;
    public static final int SQUARE_SIZE = 20;
    public static final int GAME_PLAY_WIDTH = 850;
    public static final int GAME_PLAY_HEIGHT = 590;
    public static final int GAME_PLAY_AREA_Y = 145;
    public static final int LABEL_WIDTH = 125;
    public static final int STANDARD_LABEL_HEIGHT = 30;
    public static final int VALUE_LABEL_HEIGHT = 50;
    public static final int UNIT_SQUARES = 40;
    public static final int SCORE_TEXT_POSITION_X = 75;
    public static final int STATUS_LABEL_VALUES_Y = GAME_PLAY_AREA_Y - VALUE_LABEL_HEIGHT;
    public static final int STATUS_LABEL_KEYS_Y = STATUS_LABEL_VALUES_Y - STANDARD_LABEL_HEIGHT;
    public static final int LIVES_TEXT_POSITION_X =
            GAME_PLAY_WIDTH + SCORE_TEXT_POSITION_X - (2 * LABEL_WIDTH);
    public static final int TIME_TEXT_POSITION_X = SCORE_TEXT_POSITION_X + LABEL_WIDTH;
    public static final int LEVEL_TEXT_POSITION_X = LIVES_TEXT_POSITION_X + LABEL_WIDTH;
    public static final int INFO_TITLE_Y = 200;
    public static final int INFO_TEXT_WIDTH = 550;
    public static final int INFO_TEXT_Y = 280;
    public static final int RETURN_BUTTON_Y = 670;
    public static final int INFO_TEXT_HEIGHT = RETURN_BUTTON_Y - INFO_TEXT_Y;

    // Colors
    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(18, 96, 98);
    public static final Color HOVER_BACKGROUND_COLOR = new Color(87, 26, 128);
    public static final Color SCORE_KEY_TEXT_COLOR = new Color(87, 26, 128);
    public static final Color SCORE_VALUE_TEXT_COLOR = new Color(87, 26, 128);
    public static final Color SCORE_VALUE_ALTERNATE_TEXT_COLOR = new Color(87, 26, 128);
    public static final Color DEFAULT_BORDER_COLOR = new Color(18, 144, 147);
    public static final Color HOVER_BORDER_COLOR = new Color(87, 26, 128);
    public static final Color INFO_TITLE_COLOR = new Color(18, 96, 98);
    public static final Color INFO_MESSAGE_COLOR = new Color(87, 26, 128);

    // Panel names
    public static final String MAIN_MENU_PANEL = "Main Menu";
    public static final String PLAY_PANEL = "Play";
    public static final String HELP_MENU_PANEL = "Help";
    public static final String ABOUT_ME_PANEL = "About Me";
    public static final String ACHIEVEMENTS_PANEL = "Achievements";

    // Fonts
    public static final Font INFO_TITLE_FONT = new Font("SansSerif", Font.BOLD, 42);
    public static final Font SCORE_TEXT_KEY_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font SCORE_TEXT_VALUE_FONT = new Font("SansSerif", Font.BOLD, 32);
    public static final Font INFO_MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 24);
    public static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    // ButtonFactory constants
    public static final int BORDER_THICKNESS = 2;
    public static final int BORDER_PADDING = 10;

    // Button Text
    public static final String RETURN_BUTTON_TEXT = "Main Menu";
    public static final int MAXIMUM_FOOD_ON_SCREEN = 5;
    public static final int SPOILED_FOOD_THRESHOLD = 15;
    public static final int SPOILED_FOOD_REMOVE_DELAY = 15;

    // Game Panel constants
    public static final int INITIAL_SNAKE_PARTS = 4;
    public static final int SPOILED_FOOD_SCORE = 20;

    public static final int APPLE_EATEN_SCORE = 1;
    public static final int ORANGE_EATEN_SCORE = 2;
    public static final int CHERRY_EATEN_SCORE = 5;
    public static final int WATERMELON_EATEN_SCORE = 10;
    public static final int GAME_LOOP_DELAY = 200;
    public static final int LEVEL_CHANGE_THRESHOLD = 10;
    public static final int INITIAL_SNAKE_X = 15;
    public static final int INITIAL_SNAKE_Y = 8;
    public static final String EXIT_GAME = "Exit";

    // InformationPanel constants
    public static final String GAME_RULES_TITLE = "How to Play";
    public static final String ABOUT_ME_TITLE = "About";
    public static final String[] GAME_RULES_MESSAGES = {
            "Use the arrow keys to steer your snake on an epic quest!",
            "Collect tasty fruits to increase your score!",
            "Be cautious of spoiled food, as it deducts " + Math.abs(
                    SPOILED_FOOD_SCORE) + " points.",
            "Eating watermelons grants a time bonus.", "",
            "Point System", "Apple: " + APPLE_EATEN_SCORE, "Orange: " + ORANGE_EATEN_SCORE,
            "Cherry: " + CHERRY_EATEN_SCORE, "Watermelon: " + WATERMELON_EATEN_SCORE, "",
            "Be alert!", "Colliding with walls or your own tail spells game over.", "",
            "Need a break?", "Press the space bar to pause and plan your next move."
    };

    public static final String[] ABOUT_ME_MESSAGES = {
            "Developed by",
            "Luke Bowen (lbowen6@myune.edu.au)",
            "Sean Clark (sclark94@myune.edu.au)",
            "Matthew Picone (mp629829@gmail.com/mpicone2@myune.edu.au)", "",
            "(Group 15) COSC220 A3, 2023.", "",
            "Credits",
            "Sounds: Fesliyan Studios (www.FesliyanStudios.com)",
            "Logo: Sean 2023",
            "Background: Matthew Picone 2023",
            "Icon: Icons for Free (icons-for-free.com/authors/)"
    };

    // Constructor - private to prevent instantiation
    private GameConstants() {
    }
}
