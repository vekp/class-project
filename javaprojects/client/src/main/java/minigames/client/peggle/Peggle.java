import javax.swing.*;
import java.awt.*;

public class Peggle {
    private static final String gameName = "Peggle";
    private static JFrame mainWindow = null;
    private static JPanel titleScreen = null;
    private static ImageIcon icon = null;
    private static final String iconFilePath = "./assets/gameicon.png";


    // TODO: Decide on game colours
    private static final Color mainColour = new Color(1, 172, 252);
    private static final Color accentColour = new Color(255, 255, 255);
    private static final Color textColour = new Color(0, 0, 0);



    public static void main(String[] args) {


        //Create JFrame window: set title, size and colour
        mainWindow = new JFrame(gameName);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setMinimumSize(new Dimension(500,500));
        mainWindow.setBackground(mainColour);

        //Set icon for main window
        icon = new ImageIcon(iconFilePath);
        mainWindow.setIconImage(icon.getImage());

        //Initialise game with start screen
        titleScreen = displayTitleScreen();
        mainWindow.setContentPane(titleScreen);
        mainWindow.pack();
        mainWindow.setVisible(true);

    }


    public static JPanel displayTitleScreen(){

        //Basic properties of parent JPanel
        titleScreen = new JPanel(new GridLayout(0,1,30,30));
        titleScreen.setBackground(mainColour);

        //Get UI details from UI class depending on response
        JButton startGameButton = new JButton("Start Game");
        JButton leaderboardButton = new JButton("Leaderboard (In Development)");
        JButton settingsButton = new JButton("Settings (In Development)");

        //Button colour (background) and button text (foreground)
        startGameButton.setBackground(accentColour);
        startGameButton.setForeground(textColour);
        startGameButton.addActionListener(e -> startGame());

        leaderboardButton.setBackground(accentColour);
        leaderboardButton.setForeground(textColour);
        leaderboardButton.addActionListener(e -> checkLeaderboard());

        settingsButton.setBackground(accentColour);
        settingsButton.setForeground(textColour);
        settingsButton.addActionListener(e -> checkSettings());

        //Add buttons
        titleScreen.add(startGameButton);
        titleScreen.add(leaderboardButton);
        titleScreen.add(settingsButton);

        return titleScreen;
    }


    public static void startGame(){
        System.out.println("Starting game");
    }

    public static void checkSettings(){
        System.out.println("Open Settings");
    }

    public static void checkLeaderboard(){
        System.out.println("Checking Leaderboard");
    }

}