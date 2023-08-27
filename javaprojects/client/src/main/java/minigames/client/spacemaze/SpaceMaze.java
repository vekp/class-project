package minigames.client.spacemaze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import io.vertx.core.json.JsonArray;
import java.awt.Point;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.Box;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.spacemaze.MazeDisplay;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * Class to set up the window, communicate with server and
 * handle client-side processes
 *
 * @author Niraj Rana Bhat
 */
public class SpaceMaze implements GameClient {
    private static final Logger logger = LogManager.getLogger(SpaceMaze.class);

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */
    String player;

    //Header Section
    JPanel headerPanel;
    JLabel headerText;

    //Menu Section
    JPanel mainMenuPanel; 
    JPanel buttonsPanel;  
    JButton startGameButton; //Start Game Button
    JButton highScoreButton; //High Score Button
    JButton mainMenuButton;  //Main Menu Button
    JButton exitButton;      //Exit Button
    
    //Main Container
    JLabel developerCredits;

    // HUD
    StatusBar statusBar;

    // Maze
    MazeDisplay maze;

    int time = 120;

    /**
     * Constructor
     */
    public SpaceMaze() {
        
        //Menu Header Section
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(600, 200));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        headerText = new JLabel("Space Maze");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(new Font("Monospaced", Font.PLAIN, 32));
        headerPanel.add(headerText, gbc);

        //Menu Section
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setPreferredSize(new Dimension(600, 600));
        mainMenuPanel.setBackground(Color.BLACK);

        //Buttons panel inside menu section
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setPreferredSize(new Dimension(200,200));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)); //Box layout with items arranged vertically.

        startGameButton = new JButton("Start Game");
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.addActionListener((evt) -> sendCommand("START"));

        highScoreButton = new JButton("High Score");
        highScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreButton.addActionListener((evt) -> sendCommand("SCORE"));

        mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainMenuButton.addActionListener((evt) -> sendCommand("MENU"));

        exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener((evt) -> sendCommand("EXIT"));

        //Adding buttons to the buttons panel
        for (Component c : new Component[] { startGameButton, highScoreButton, mainMenuButton, exitButton }) {
            buttonsPanel.add(c);
            buttonsPanel.add(Box.createRigidArea(new Dimension(10,10)));

        }

        //Using GridBagLayout to position buttons panel inside menu section
        gbc.gridx = 0;
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(-200, 0, 0, 0);
        mainMenuPanel.add(buttonsPanel, gbc);

        //Credit Section
        developerCredits = new JLabel("Developed by: Andy, Nik, Natasha, Niraj");

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
     * @param mnClient
     * @param game
     * @param player
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        mnClient.getMainWindow().addCenter(mainMenuPanel);
        mnClient.getMainWindow().addSouth(developerCredits); 
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("my command: {}", command.getString("command"));
        switch(command.getString("command")){

            case "startGame" -> sendCommand("requestGame");
            case "firstLevel" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                // Get bot start locations
                JsonArray botStartLocations = command.getJsonArray("botStartLocations");
                if (!serialisedArray.isEmpty()) {
                    // Only call this once, or we get multiple timer tasks running
                    loadMaze(serialisedArray, botStartLocations);
                }
            }
            case "nextLevel" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                JsonArray botStartLocations = command.getJsonArray("botStartLocations");
                if (!serialisedArray.isEmpty()) {
                    List<String> mazeList = serialisedArray.getList();
                    char[][] mazeMap = deserialiseJsonMaze(mazeList);
                    //ArrayList<SpaceBot> bots = loadBots(botStartLocations, mazeMap);
                    ArrayList<SpaceBot> bots = loadBots(botStartLocations);
                    maze.newLevel(mazeMap, bots);
                }
                String totalScore = command.getString("totalScore");
                statusBar.updateScore(totalScore);
                String levelNumber = command.getString("level");
                statusBar.updateLevel(levelNumber);
            }
            case "updateMaze" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                if (!serialisedArray.isEmpty()) {
                    //Json array of strings to Java array of strings
                    List<String> mazeList = serialisedArray.getList();
                    char[][] mazeMap = deserialiseJsonMaze(mazeList);
                    maze.updateMaze(mazeMap);
                }
            }
            case "timer" -> {
                String timeValue = command.getString("time");
                logger.info("Received timer command with value: " + timeValue);
                statusBar.updateTimer(command.getString("time"));
            }
            case "gameOver" -> {
                statusBar.stopTimer();
                String totalScore = command.getString("totalScore");
                statusBar.updateScore(totalScore);
            }
            case "viewHighScore" -> headerText.setText("View High Score");
            case "mainMenu" -> headerText.setText("Go to Main Menu");
            case "exit" -> closeGame();
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do        
    }

    /* 
     * Sets up the client side bots as per level design.
     * @param  jsonArray of bot locations. mazeMap can be added back in if required.
     * @return ArrayList<Spacebot>
     */
    public ArrayList<SpaceBot> loadBots(JsonArray botStartLocations) {
        ArrayList<SpaceBot> bots = new ArrayList<>();
        
        int numBots = botStartLocations.size();
        for(int i = 0;i<numBots;i++)
        {
            JsonObject thisLoc = botStartLocations.getJsonObject(i);
            //logger.info("JsonObject thisLoc {}", thisLoc);

            int x = thisLoc.getInteger("x");
            int y = thisLoc.getInteger("y");
            Point startLoc = new Point(x,y);
            //SpaceBot newBot = new SpaceBot(startLoc, maze);
             SpaceBot newBot = new SpaceBot(startLoc);
            bots.add(newBot);
        }

        return bots;
    }


    /**
     * Called for the intial maze setup
     * @param jsonArray the maze sent from the server as a Json Object
     */
    public void loadMaze(JsonArray jsonArray, JsonArray botStartLocations){
        mnClient.getMainWindow().clearAll();
        

        //Json array of strings to Java array of strings
        List<String> mazeList = jsonArray.getList();

        char[][] mazeMap = deserialiseJsonMaze(mazeList);


        // get bots
        //ArrayList<SpaceBot> bots = loadBots(botStartLocations, mazeMap);
        ArrayList<SpaceBot> bots = loadBots(botStartLocations);
        maze = new MazeDisplay(mazeMap, this, bots);
        statusBar = new StatusBar(this);

        mnClient.getMainWindow().addCenter(maze);
        mnClient.getMainWindow().addSouth(statusBar.getStatusBar());
        mnClient.getMainWindow().pack();

        statusBar.startTimer();

        maze.requestFocusInPanel();
    }

    /**
     * Method to rebuild the char[][] array sent from the server
     * @param serialisedMaze
     * @return char[][] mazeArray
     */
    private char[][] deserialiseJsonMaze(List<String> serialisedMaze) {
        // Rows in the original array is size of the list of Strings
        int rows = serialisedMaze.size();
        char[][] mazeArray = new char[rows][];
        // Converting each string into an array of chars
        for (int i = 0; i < rows; i++) {
            mazeArray[i] = serialisedMaze.get(i).toCharArray();
        }
        return mazeArray;
    }
}

