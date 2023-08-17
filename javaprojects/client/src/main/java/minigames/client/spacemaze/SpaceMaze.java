package minigames.client.spacemaze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

import java.util.List;
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

    JPanel mazePanel;
    JPanel elementPanel;

    MazeDisplay maze;

    ClientControl controller;
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

    /**
     *
     * @param game
     * @param command
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        logger.info("my command: {}", command.getString("command"));
        switch(command.getString("command")){

            case "startGame" -> sendCommand("requestMaze");
            case "renderMaze" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                loadMaze(serialisedArray);
            }
            case "viewHighScore" -> headerText.setText("View High Score");
            case "mainMenu" -> headerText.setText("Go to Main Menu");
            case "exit" -> closeGame();
            
            //Update PLayer location in display maze.
            //case "movePlayerToHere" -> updatePlayerLocation(newLocation);

            //case "updateTime" -> updateTime(); //Dummy Timer
        }
    }

    //Method to update location of player in DisplayMaze? 
    public void UpdatePlayerLocation(){
        //Todo 
    }

    /**
     *
     */
    @Override
    public void closeGame() {
        // Nothing to do        
    }

    /**
     *
     * @param jsonArray
     */
    public void loadMaze(JsonArray jsonArray){
        //Start Dummy Timer
        //startTimer();

        mnClient.getMainWindow().clearAll();

        List<String> mazeList = jsonArray.getList();
        char[][] mazeMap = deserialiseJsonMaze(mazeList);

        Point playerStartPos = new Point(1,0);

        maze = new MazeDisplay(mazeMap, playerStartPos, this);
        //controller = new ClientControl(maze, this);

        mazePanel = maze.mazePanel(mazeMap);
        elementPanel = maze.elementPanel();

        mnClient.getMainWindow().addCenter(mazePanel);
        mnClient.getMainWindow().addSouth(elementPanel);
        mnClient.getMainWindow().pack();

        maze.requestFocusInPanel();
    }

    /**
     * Method to rebuild the char[][] array sent from the server
     * @param serialisedMaze
     * @return char[][] mazeArray
     */
    private char[][] deserialiseJsonMaze(List<String> serialisedMaze) {
        int rows = serialisedMaze.size();
        char[][] mazeArray = new char[rows][];
        for (int i = 0; i < rows; i++) {
            mazeArray[i] = serialisedMaze.get(i).toCharArray();
        }
        return mazeArray;
    }

    /*
    //Dummy Timer
    public void updateTime(){
        maze.updateTimer(time);
        time-= 1;
    }

    public void startTimer(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendCommand("gameTimer");
            }
        }, 0, 1000);
    }*/
    
}

