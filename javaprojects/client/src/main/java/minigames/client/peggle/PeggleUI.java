import javax.swing.*;
import java.awt.*;

public class PeggleUI {
    // TODO: Decide on game colours
    private static final Color mainColour = new Color(1, 172, 252);
    private static final Color accentColour = new Color(255, 255, 255);
    private static final Color textColour = new Color(0, 0, 0);


    //Title screen components
    private static JPanel titleScreen = null;
    private static JPanel buttonGrid = null;
    private static final String titleFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/titlecard.png;



    public JPanel generateTitleScreen(){
        //TODO add outer graphic
        titleScreen = new JPanel(new GridLayout(0,1,10,10));
        titleScreen.setBackground(mainColour);
        titleScreen.add(generateTitleScreenButtons());
        return titleScreen;
    }


    private JPanel generateTitleScreenButtons(){

        //Basic properties of parent JPanel
        buttonGrid = new JPanel(new GridLayout(0,1,30,30));
        buttonGrid.setBackground(mainColour);

        //Generate titlecard
        JLabel titleCard = new JLabel(new ImageIcon(titleFilePath));
        buttonGrid.add(titleCard);

        //Get UI details from UI class depending on response
        JButton startGameButton = new JButton("Start Game");
        JButton leaderboardButton = new JButton("Leaderboard (In Development)");
        JButton achievementsButton = new JButton("Achievements (In Development)");
        JButton settingsButton = new JButton("Settings (In Development)");

        //Button colour (background) and button text (foreground)
        startGameButton.setBackground(accentColour);
        startGameButton.setForeground(textColour);
        startGameButton.addActionListener(e -> startGame());

        leaderboardButton.setBackground(accentColour);
        leaderboardButton.setForeground(textColour);
        leaderboardButton.addActionListener(e -> checkLeaderboard());

        achievementsButton.setBackground(accentColour);
        achievementsButton.setForeground(textColour);
        achievementsButton.addActionListener(e -> checkAchievements());

        settingsButton.setBackground(accentColour);
        settingsButton.setForeground(textColour);
        settingsButton.addActionListener(e -> checkSettings());

        //Add buttons
        buttonGrid.add(startGameButton);
        buttonGrid.add(leaderboardButton);
        buttonGrid.add(achievementsButton);
        buttonGrid.add(settingsButton);

        return buttonGrid;
    }

    private void startGame(){
        System.out.println("Starting game");
    }

    private void checkLeaderboard(){
        System.out.println("Checking Leaderboard");
    }

    private void checkAchievements() {
        System.out.println("Checking Achievements");
    }

    private void checkSettings(){
        System.out.println("Open Settings");
    }

}
