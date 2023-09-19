package minigames.client.krumgame;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JTextField;

public class KrumMenu {

    // buttonBounds should be changed if we move a button (not including the open game buttons, i.e. the clickable names that join a specific game)
    final static Rectangle[] buttonBounds = {   new Rectangle(485, 350, 277, 87),      // newgame (x, y, width, height)
                                                new Rectangle(50, 60, 142, 42),         // instructions (x, y, width, height)
                                                new Rectangle(200, 59, 200, 43),        // achievements (x, y, width, height)
                                                new Rectangle(104, 408, 293, 87),      // quickplay (x, y, width, height)
                                                new Rectangle(404, 408, 293, 87)   };  // options (x, y, width, height)

    // nameFieldBounds can be changed to move or resize the name input field
    final public static Rectangle nameFieldBoundsDefault = new Rectangle(373, 10, 54, 30); // x, y, width, height
    final public static Rectangle nameFieldBoundsTitle = new Rectangle(398, 518, 212, 27); // x, y, width, height
    final public static Rectangle nameFieldBoundsInstructions = new Rectangle(507, 484, 239, 35); // x, y, width, height
    
    // these can be changed to tweak the open game buttons, i.e. the clickable names that join a specific game
    public final static int gameListX = 520; // x position of the open game buttons
    public final static int gameListYStart = 165; // y position of the first open game button
    public final static int gameListYIncrement = 63; // vertical distance between top of one open game button and the next
    public final static int gameListWidth = 200; // width of the open game buttons
    public final static int gameListButtonHeight= 60; // height of each open game button
    public final static String gameListFontName = "Courier New";
    public final static int gameListFontStyle = Font.PLAIN; // can be plain, bold, or italic
    public final static int gameListFontSize = 24;
    public final static Color gameListFontColour = Color.RED;

    // end of tweakable constants

    public static JTextField nameField;

    static HashMap<String, BufferedImage> screens;
    static HashMap<String, Rectangle> bounds;

    final static String[] screenNames = {"title", "achievements", "instructions"}; // don't change these unless we're adding or removing a screen
    final static String[] buttonNames = {"newgame", "instructions", "achievements", "quickplay", "options"}; // don't change these unless we're adding or removing a button

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
        KrumSound.playSound("titletune");
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
        nameField.setBounds(nameFieldBoundsDefault);
    } 
}
