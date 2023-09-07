package minigames.client.hangman;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.*;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * A very simple interface for a snake game.
 * 
 */
public class HangmanClient implements GameClient {
    private static final Logger logger = LogManager.getLogger(HangmanClient.class);

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Player name */
    String player;



    /**Buttons, JLabels and JPanels */
    JButton start, score, help, back, back1, exit, newGameButton, stopGameButton, exitButton;
    JLabel headerText, footerText, helpText, helpText1, helpText2, controlText, highScore, playerName, scoreText, playerNumber;
    JPanel headerPanel, footerPanel, mainMenuPanel, buttonPanel, helpPanel, scorePanel;
    private JLabel[] pointInses;
	private JLabel[] pointLabels;

    private static final String[] snakePlayerStrs = { "Player Name:" };

    Dimension buttonDimension;    //Maximum and preferred dimension of button

    Font customBigFont, customSmallFont ; //font
    Color borderColour = new Color(50, 50, 50);

    public HangmanClient() {

        //Button Dimension
        buttonDimension = new Dimension(150, 50);

        //Customized Font
        customBigFont = new Font("Monospaced", Font.PLAIN, 32);
        customSmallFont = new Font("Monospaced", Font.PLAIN, 24);

        /** Header Section with Menu*/
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(800,100));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        
        //Header Text
        headerText = new JLabel("Hangman Game");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(customBigFont);
        headerPanel.add(headerText, g);


        /** Footer Panel*/
        footerPanel = new JPanel();
        footerPanel.setPreferredSize(new Dimension(800,30));
        footerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());


        /**Footer Section */
        footerText = new JLabel("Developer: Sushil Kandel");
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
        buttonPanel.setPreferredSize(new Dimension(800,800));
        buttonPanel.setBackground(Color.black);
        

        /**Button Section*/
        start = new JButton("Start Game");
        start.addActionListener((evt) -> sendCommand("GAME"));

        score = new JButton("Score");
        score.addActionListener((evt) -> sendCommand("SCORE"));

        help = new JButton("Help");
        help.addActionListener((evt) -> sendCommand("HELP"));

        back = new JButton("Back");
        back.addActionListener((evt) -> sendCommand("BACK"));

        back1 = new JButton("Back");
        back1.addActionListener((evt) -> sendCommand("BACK"));

        /**setting back buttons dimension */
        back.add(Box.createRigidArea(new Dimension(15,15)));
        back.setPreferredSize(buttonDimension);
        back.setMaximumSize(buttonDimension);

        back1.add(Box.createRigidArea(new Dimension(15,15)));
        back1.setPreferredSize(buttonDimension);
        back1.setMaximumSize(buttonDimension);

        exit = new JButton("Exit");
        exit.addActionListener((evt) -> sendCommand("EXIT"));


        /** Adding buttons into button panel*/
        for (Component c : new Component[] { start, score, help, exit }) {
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


        /**Adding Extra Panels for main menu */
        

        /**Help Panel */
        helpPanel = new JPanel();
        helpPanel.setBackground(Color.BLACK);
        helpPanel.setPreferredSize(new Dimension(800, 800));
        helpPanel.setLayout(new GridBagLayout());

        /**Help Text */
        helpText = new JLabel("INSTRUCTIONS");
        helpText.setFont(customBigFont);
        helpText.setForeground(Color.white);

        g.insets = new Insets(-450, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 0;
        helpPanel.add(helpText, g);

        //control text
        controlText = new JLabel("Control:");
        controlText.setFont(customSmallFont);
        controlText.setForeground(Color.white);

        g.insets = new Insets(-550, -650, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(controlText, g);


        /**help text */
        helpText = new JLabel("Use all keywords to provide the guess word");
        helpText.setFont(customSmallFont);
        helpText.setForeground(Color.white);

        g.insets = new Insets(-550, 120, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(helpText, g);

        helpText1 = new JLabel("- Collect lives for better game play");
        helpText1.setFont(customSmallFont);
        helpText1.setForeground(Color.white);

        g.insets = new Insets(-450, -160, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(helpText1, g);

        helpText2 = new JLabel("- Guess more words to score high");
        helpText2.setFont(customSmallFont);
        helpText2.setForeground(Color.white);

        g.insets = new Insets(-450, -160, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(helpText2, g);


        g.insets = new Insets(200, 10, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        helpPanel.add(back1, g);


        /**Score Panel */
        scorePanel = new JPanel();
        scorePanel.setBackground(Color.BLACK);
        scorePanel.setPreferredSize(new Dimension(800, 800));
        scorePanel.setLayout(new GridBagLayout());

       /**Score Text */
        highScore = new JLabel("High Score");
        highScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScore.setFont(customSmallFont);
        highScore.setForeground(Color.WHITE);
        
        g.insets = new Insets(-400, 100, 0, 0);
        g.gridx = 0;
        g.gridy = 0;
        scorePanel.add(highScore, g);


        playerName = new JLabel("Player Name: ");
        playerName.setFont(customSmallFont);
        playerName.setForeground(Color.white);

        g.insets = new Insets(-400, -300, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        scorePanel.add(playerName, g);

        scoreText= new JLabel("Score: ");
        scoreText.setFont(customSmallFont);
        scoreText.setForeground(Color.white);

        g.insets = new Insets(-400, 80, 0, 0);
        g.gridx = 1;
        g.gridy = 1;
        scorePanel.add(scoreText, g);

        g.insets = new Insets(200, 100, 0, 0);
        g.gridx = 0;
        g.gridy = 1;
        scorePanel.add(back, g); 
       
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
            case "game" -> displayHangmanGame();
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
        mnClient.getMainWindow().addCenter(mainMenuPanel.add(buttonPanel));
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

    public void displayHangmanGame(){
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(displayGame());
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().addSouth(footerPanel);
        mnClient.getMainWindow().pack();
    }

    public JPanel displayGame(){
        mnClient.getMainWindow().clearAll();

        JPanel gameArea = new HangmanPanel();
        gameArea.setPreferredSize(new Dimension(800, 570));
        gameArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColour));

        newGameButton = new JButton("Play!");

        return gameArea;
        

        
        
    }
 
    // public JPanel displayGame(){
    //     mnClient.getMainWindow().clearAll(); 

    //     JPanel mainPanel = new JPanel();
    //     mainPanel.setPreferredSize(new Dimension(800, 700));
    //     mainPanel.setBackground(Color.BLACK);
    //     mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        

    //     JPanel gamePanel = new JPanel();
    //     gamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	// 	gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
    //     gamePanel.setPreferredSize(new Dimension(200, 650));
    //     gamePanel.setBackground(Color.GRAY);
    //     gamePanel.setAlignmentY(Component.LEFT_ALIGNMENT);

    //     mainPanel.add(gamePanel);

        

    //     playerNumber = new JLabel("Start your game.");
    //     playerNumber.setBorder(new EmptyBorder(10, 10, 10, 10));
	// 	playerNumber.setFont(new Font("TimesRoman", Font.PLAIN, 16));
	// 	playerNumber.setForeground(Color.BLUE);
	// 	gamePanel.add(playerNumber);

    //     playerNumber.add(Box.createVerticalGlue());

    //     JLabel pointIns = new JLabel("Point of snakes:");
	// 	pointIns.setFont(new Font("TimesRoman", Font.BOLD, 16));
	// 	playerNumber.add(pointIns);

    //     Font fontPlain = new Font("TimesRoman", Font.PLAIN, 14);
	// 	Font fontBold = new Font("TimesRoman", Font.BOLD, 14);
	// 	pointInses = new JLabel[1];
	// 	pointLabels = new JLabel[1];
    //     for (int i = 0; i < 1; i++) {
	// 		pointIns = new JLabel(snakePlayerStrs[i] + " "+ player);
	// 		pointIns.setFont(fontPlain);
    //         pointIns.setForeground(Color.BLACK);
	// 		pointInses[i] = pointIns;

	// 		JLabel point = new JLabel("0");
    //         point.setBorder(new EmptyBorder(5, 5, 5, 5));
	// 		point.setFont(fontBold);
    //         point.setForeground(Color.BLACK);
	// 		pointLabels[i] = point;

	// 		gamePanel.add(pointIns);
	// 		gamePanel.add(point);
	// 	}

    //     gamePanel.add(Box.createVerticalGlue());

    //     Font btnFont = new Font("TimesRoman", Font.PLAIN, 14);
	// 	newGameButton = new JButton("New game");
	// 	newGameButton.setFont(btnFont);
    //     newGameButton.addActionListener((evt) -> sendCommand("NEW"));
	// 	gamePanel.add(newGameButton);
	// 	gamePanel.add(Box.createVerticalGlue());

    //     stopGameButton = new JButton("Stop game");
	// 	stopGameButton.setFont(btnFont);
	// 	stopGameButton.addActionListener((evt) -> sendCommand("STOP"));
	// 	gamePanel.add(stopGameButton);
	// 	gamePanel.add(Box.createVerticalGlue());

    //     exitButton = new JButton("Exit");
	// 	exitButton.setFont(btnFont);
	// 	exitButton.addActionListener((evt) -> sendCommand("EXIT"));
	// 	gamePanel.add(exitButton);
	// 	gamePanel.add(Box.createVerticalGlue());

	// 	return mainPanel; 

    // }
}
