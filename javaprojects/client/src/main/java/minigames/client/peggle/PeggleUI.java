package minigames.client.peggle;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;

public class PeggleUI {
    private static final String gameName = "Peggle";
    private static JFrame mainWindow = null;
    private static final String titleFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/titlecard.png";
    private static final String iconFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/gameicon.png";


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
        ImageIcon icon = new ImageIcon(iconFilePath);
        mainWindow.setIconImage(icon.getImage());

        //Initialise game with start screen
        JPanel titleScreen = generateTitleScreen();
        mainWindow.setContentPane(titleScreen);
        mainWindow.pack();
        mainWindow.setVisible(true);

    }

    public static JPanel generateTitleScreen(){
        //TODO add outer graphic

        JPanel titleScreen = new JPanel(new GridLayout(0,3,10,10));
        titleScreen.setBackground(mainColour);

        //Padding at bottom of buttons showing title
        titleScreen.add(generateMenuHeader());

        //Menu interaction buttons plus top and bottom headers
        titleScreen.add(generateMenuButtons());

        //Padding at bottom of buttons showing title
        titleScreen.add(generateMenuHeader());

        return titleScreen;
    }


    private static JPanel generateMenuButtons(){

        //Basic properties of parent JPanel
        JPanel buttonGrid = new JPanel(new GridLayout(0,1,0,10));
        buttonGrid.setBackground(mainColour);

        //Get UI details from UI class depending on response
        JButton startGameButton = new JButton("Start Game");
        JButton instructionsButton = new JButton("How to Play");
        JButton leaderboardButton = new JButton("Leaderboard (In Development)");
        JButton achievementsButton = new JButton("Achievements (In Development)");
        JButton settingsButton = new JButton("Settings (In Development)");

        //Button colour (background) and button text (foreground)
        startGameButton.setBackground(accentColour);
        startGameButton.setForeground(textColour);
        startGameButton.addActionListener(e -> startGame());

        instructionsButton.setBackground(accentColour);
        instructionsButton.setForeground(textColour);
        instructionsButton.addActionListener(e -> listInstructions());

        leaderboardButton.setBackground(accentColour);
        leaderboardButton.setForeground(textColour);
        leaderboardButton.addActionListener(e -> checkLeaderboard());

        achievementsButton.setBackground(accentColour);
        achievementsButton.setForeground(textColour);
        achievementsButton.addActionListener(e -> checkAchievements());

        settingsButton.setBackground(accentColour);
        settingsButton.setForeground(textColour);
        settingsButton.addActionListener(e -> checkSettings());


        //Padding at top of buttons showing title
        buttonGrid.add(generateMenuHeader());

        //Add buttons
        buttonGrid.add(startGameButton);
        buttonGrid.add(instructionsButton);
        buttonGrid.add(leaderboardButton);
        buttonGrid.add(achievementsButton);
        buttonGrid.add(settingsButton);

        //Padding at bottom of buttons showing title
        buttonGrid.add(generateMenuHeader());

        return buttonGrid;
    }

    private static JPanel generateMenuHeader() {

        //Basic properties of parent JPanel
        JPanel menuHeader = new JPanel(new GridLayout(0,1,0,10));
        menuHeader.setBackground(mainColour);

        //Generate title
        JLabel titleCard = new JLabel(new ImageIcon(titleFilePath));
        menuHeader.add(titleCard);

        return menuHeader;
    }


    private static void startGame(){
        InGameUI gameSession = new InGameUI();
        mainWindow.setContentPane(gameSession);
        mainWindow.revalidate();
    }

    private static void listInstructions() {
        InstructionsUI instructionsSession = new InstructionsUI();
        mainWindow.setContentPane(instructionsSession);
        mainWindow.revalidate();
    }

    private static void checkLeaderboard(){
        System.out.println("Checking Leaderboard");
    }

    private static void checkAchievements() {
        System.out.println("Checking Achievements");
    }

    private static void checkSettings(){
        System.out.println("Open Settings");
    }

    private void pauseMenu(){
        System.out.println("Pause Menu");
    }


}