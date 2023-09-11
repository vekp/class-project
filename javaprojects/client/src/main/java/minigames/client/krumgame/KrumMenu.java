package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JTextField;

public class KrumMenu {
    final static String[] screenNames = {"title", "achievements", "instructions"};
    final static String[] buttonNames = {"newgame", "instructions", "achievements", "quickplay", "options"};
    final static Rectangle[] buttonBounds = {   new Rectangle(485, 414, 277, 108),      // newgame (x, y, width, height)
                                                new Rectangle(50, 60, 142, 42),         // instructions (x, y, width, height)
                                                new Rectangle(200, 59, 200, 43),        // achievements (x, y, width, height)
                                                new Rectangle(100, 416, 293, 123),      // quickplay (x, y, width, height)
                                                new Rectangle(400, 416, 293, 123)   };  // options (x, y, width, height)

    final static Rectangle nameFieldBounds = new Rectangle(373, 10, 54, 30); // x, y, width, height
    
    public static JTextField nameField;
    
    public final static int gameListX = 525;
    public final static int gameListYStart = 175;
    public final static int gameListYIncrement = 75;
    public final static int gameListWidth = 200;
    public final static int gameListButtonHeight= 60;
    public final static String gameListFontName = "Courier New";
    public final static int gameListFontStyle = Font.PLAIN;
    public final static int gameListFontSize = 24;
    public final static Color gameListFontColour = Color.RED;

    static HashMap<String, BufferedImage> screens;
    static HashMap<String, Rectangle> bounds;

    public static BufferedImage getScreen(String name) {
        return screens.get(name);
    }

    public static JButton invisibleButton(String buttonName) {                 
        JButton button = new JButton();
        button.setLayout(null);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);        
        button.setFocusable(false);
        button.setBounds(bounds.get(buttonName));
        return button;
    }

    public static void initialise() {
        screens = new HashMap<String, BufferedImage>();
        for (String n : screenNames) {
            screens.put(n, KrumHelpers.readSprite("TitlePages/" + n + ".png"));
        }
        bounds = new HashMap<String, Rectangle>();
        for (int i = 0; i < buttonNames.length; i++) {
            bounds.put(buttonNames[i], buttonBounds[i]);
        }
        nameField = new JTextField(20);
        nameField.setText("Algernon");
        nameField.setLayout(null);
        nameField.setBounds(nameFieldBounds);
    } 
}
