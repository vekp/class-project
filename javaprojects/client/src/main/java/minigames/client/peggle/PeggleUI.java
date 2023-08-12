import javax.swing.*;
import java.awt.*;

public class PeggleUI {
    // TODO: Decide on game colours
    private static final Color mainColour = new Color(1, 172, 252);
    private static final Color accentColour = new Color(255, 255, 255);
    private static final Color textColour = new Color(0, 0, 0);
    private static JPanel titleScreen = null;


    public JPanel generateTitleScreenButtons(){

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

    private void startGame(){
        System.out.println("Starting game");
    }

    private void checkSettings(){
        System.out.println("Open Settings");
    }

    private void checkLeaderboard(){
        System.out.println("Checking Leaderboard");
    }


}
