package minigames.client.snakeGameClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Loader;

import java.awt.*;
import java.util.Collections;
import javax.swing.*;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * A very simple interface for a snake game.
 * 
 */
public class SnakeGameText implements GameClient {
    private static final Logger logger = LogManager.getLogger(SnakeGameText.class);

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Player name */
    String player;

    /**Buttons, JLabels and JPanels */
    JButton start, score, help, back, exit;
    JLabel headerText, footerText, helpText, helpText1, controlText, highScore, playerName, scoreText;
    JPanel headerPanel, footerPanel, mainMenuPanel, buttonPanel, helpPanel, scorePanel;

    Dimension buttonDimension;    //Maximum and preferred dimension of button

    Font customBigFont, customSmallFont ; //font
    public SnakeGameText() {

        //Button Dimension
        buttonDimension = new Dimension(150, 50);

        customBigFont = new Font("Monospaced", Font.PLAIN, 32);
        customSmallFont = new Font("Monospaced", Font.PLAIN, 24);

        /** Header Section with Menu*/
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(800,100));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        
        //Header Text
        headerText = new JLabel("Snake Game");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(customBigFont);
        headerPanel.add(headerText, g);


        /**Help Panel */
        helpPanel = new JPanel();
        helpPanel.setLayout(new GridBagLayout());
        helpPanel.setPreferredSize(new Dimension(800, 500));
        helpPanel.setBackground(Color.YELLOW);
        helpText = new JLabel("INSTRUCTIONS");
        helpText.setFont(customBigFont);
        helpText.setForeground(Color.BLACK);

        g.insets = new Insets(-200, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 0;
        helpPanel.add(helpText, g);

        //control text
        controlText = new JLabel("Control:");
        controlText.setFont(customSmallFont);
        controlText.setForeground(Color.BLACK);

        g.insets = new Insets(50, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(controlText, g);


        /**help text */
        helpText = new JLabel("W, S, A, D / Arrow Keys to control the player");
        helpText.setFont(customSmallFont);
        helpText.setForeground(Color.BLACK);

        g.insets = new Insets(20, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 2;
        helpPanel.add(helpText, g);

        helpText1 = new JLabel("- Collect food to score High");
        helpText1.setFont(customSmallFont);
        helpText1.setForeground(Color.BLACK);

        g.insets = new Insets(20, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 3;
        helpPanel.add(helpText1, g);


        /**Score Panel */
        scorePanel = new JPanel();
        scorePanel.setLayout(new GridBagLayout());
        scorePanel.setPreferredSize(new Dimension(800, 500));
        scorePanel.setBackground(Color.YELLOW);

        highScore = new JLabel("High Score:");
        highScore.setPreferredSize(new Dimension(600, 200));
        highScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScore.setFont(customSmallFont);
        highScore.setForeground(Color.BLACK);
        
        g.insets = new Insets(-200, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 0;
        scorePanel.add(highScore, g);


        playerName = new JLabel("Player Name");
        playerName.setFont(customSmallFont);
        playerName.setForeground(Color.BLACK);

        g.insets = new Insets(20, 20, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        scorePanel.add(playerName, g);

        scoreText= new JLabel("Score");
        scoreText.setFont(customSmallFont);
        scoreText.setForeground(Color.BLACK);

        g.insets = new Insets(20, 20, 0, 0);
        g.gridx = 1;
        g.gridy = 1;
        scorePanel.add(scoreText, g);

         /** Footer Panel*/
        footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(800,30));
        footerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());


        /**Footer Section */
        footerText = new JLabel("Developer: Sushil, Sean, Luke, Matthew ");
        footerText.setForeground(Color.WHITE);
        footerText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        footerPanel.add(footerText,g);
        

        /** Menu section */
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setPreferredSize(new Dimension(800,800));
        mainMenuPanel.setBackground(Color.BLACK);
    

        /** Button Panel inside menu section*/
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(800,700));
        buttonPanel.setBackground(Color.BLACK);


        /**Button Section*/

        start = new JButton("Start Game");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener((evt) -> sendCommand("START"));

        score = new JButton("Score");
        score.setAlignmentX(Component.CENTER_ALIGNMENT);
        score.addActionListener((evt) -> sendCommand("SCORE"));

        help = new JButton("Help");
        help.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.addActionListener((evt) -> sendCommand("HELP"));

        back = new JButton("Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.addActionListener((evt) -> sendCommand("BACK"));


        exit = new JButton("Exit");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.addActionListener((evt) -> sendCommand("EXIT"));




        /** Adding buttons into button panel*/
        for (Component c : new Component[] { start, score, help, back, exit }) {
            buttonPanel.add(c);
            buttonPanel.add(Box.createRigidArea(new Dimension(15,15)));
            c.setPreferredSize(buttonDimension);
            c.setMaximumSize(buttonDimension);
            ((AbstractButton) c).setAlignmentX(Component.CENTER_ALIGNMENT);
        
    
        }

        /** Using GridBagLayout to position button panel inside menu panel*/
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(200, 0, 0, 0);
        mainMenuPanel.add(buttonPanel, g);

    }

    /**
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our gameserver will interpret.
     * We're sending these as
     * { "command": command }
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }


    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().addCenter(buttonPanel);
        mnClient.getMainWindow().addSouth(footerPanel);
        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
        mnClient.getNotificationManager().setMargins(15, 10, 10);
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("my command: {}", command.getString("command"));
        switch(command.getString("command")){
            case "startGame" -> sendCommand("requestGame");
            case "viewHighScore" -> displayHighScore();
            case "howToPlay" -> displayHelpPanel();
            case "backToMenu" -> displayMainMenu();
            case "exit" -> closeGame();



        }

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)


    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

    public void displayHelpPanel(){
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(helpPanel);
        mnClient.getMainWindow().addSouth(footerPanel); 
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    public void displayMainMenu(){
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(mainMenuPanel);
        mnClient.getMainWindow().addSouth(footerPanel);
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    public void displayHighScore(){
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(scorePanel);
        mnClient.getMainWindow().addSouth(footerPanel);
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }



}
