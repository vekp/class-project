package minigames.client.spacemaze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import io.vertx.core.json.JsonArray;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;


import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Timer;
import javax.swing.border.Border;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
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

    //Class to hold all images for client side use
    Images images;

    //Class to hold all sounds for client side use
    SpaceMazeSound sound;

    //Border
    private Border whiteBorder;

    //Header Section
    private JPanel headerPanel;
    private JLabel headerText;
    private HashMap<Integer, BufferedImage> titleImages;
    private int currentImageIndex;
    private Timer timer;
    private ImageIcon headerImage1;
    
    //Game Over Header Section
    private JPanel gameOverHeaderPanel;

    //Menu Section
    private JPanel mainMenuPanel; 
    private JPanel buttonsPanel;
    private JButton startGameButton; //Start Game Button
    private JButton highScoreButton; //High Score Button
    private JButton helpButton;      //Help window Button
    private JButton mainMenuButton;  //Main Menu Button
    private Dimension buttonDimension; //Button maximum and preferred dimensions

    //Help Panel Section
    private JPanel helpPanel;
    private JButton backFromHelpButton;
    //Text instructions inside help panel.
    private JLabel welcomeMessage;
    private JLabel helpText;
    private JLabel helpText1;
    private JLabel helpText2;
    private JLabel helpText3;
    private JLabel helpText4;
    private JLabel helpText5;
    private JLabel helpText6;
    private JLabel helpText7;
    private JLabel helpText8;
    //Images for help panel
    private BufferedImage bombImage;
    private BufferedImage treasureImage;
    private BufferedImage keyImage;
    private BufferedImage wormHoleImage;
    //Scaled Version of the images
    private Image scaledBomb;
    private Image scaledKey;
    private Image scaledTreasure;
    private Image scaledWormHole;
    //Final Image Icons
    private ImageIcon bombIcon;
    private ImageIcon keyIcon;
    private ImageIcon treasureIcon;
    private ImageIcon wormHoleIcon;

    //HighScore Panel
    private JPanel highScorePanel;
    private JLabel highScoreLabel;
    private JButton backFromHighScoreButton;

    //Game Over Panel
    private JPanel gameOverPanel;
    private JLabel greetingLabel;
    private JLabel totalScoreLabel;
    private JLabel timeTakenLabel;
    private JLabel pressToExitLabel;
    
    //Custom Fonts used in the program
    private Font customFont;
    private Font aquireFont;
    
    //Main Container
    private JLabel developerCredits;

    // HUD
    StatusBar statusBar;

    // Maze
    MazeDisplay maze;

    int time = 120;

    /**
     * Constructor
     */
    public SpaceMaze() {

        // Getting the single instance of the Images class
        images = Images.getInstance();

        //loading custom fonts for the game
        loadCustomFont();
        
        buttonDimension = new Dimension(300, 70);  //preferred buttons dimensions
        whiteBorder = BorderFactory.createLineBorder(Color.WHITE); //White line border used throughout the menu sections.
        
        //Menu Header Section
        headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(600, 200));
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //Header Animation
        loadMainMenuHeader(); 
        headerPanel.add(headerText, gbc);

        //Help Panel
        loadHelpPanel();

        //HighScore Section
        loadHighScorePanel();

        //Main Menu
        loadMainMenu();

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
            //Command to send if the user selects "Start Game" Button.
            case "startGame" -> sendCommand("requestGame");
            //Acknowledge server response for starting the game. Starting Level 1.
            case "firstLevel" -> {
                sound = new SpaceMazeSound();
                sound.loadSounds();
                stopTimer();
                String interactiveResponse = command.getString("interactiveResponse");
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                // Get bot start locations
                JsonArray botStartLocations = command.getJsonArray("botStartLocations");
                int playerLives = command.getInteger("playerLives");
                if (!serialisedArray.isEmpty()) {
                    // Only call this once, or we get multiple timer tasks running
                    loadMaze(serialisedArray, botStartLocations);
                    statusBar.updatePlayerLives(playerLives);
                    statusBar.setInteractiveText(interactiveResponse);
                }

            }
            //Acknowledge server response for starting the next level. level++.
            case "nextLevel" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                String interactiveResponse = command.getString("interactiveResponse");
                JsonArray botStartLocations = command.getJsonArray("botStartLocations");
                if (!serialisedArray.isEmpty()) {
                    List<String> mazeList = serialisedArray.getList();
                    char[][] mazeMap = deserialiseJsonMaze(mazeList);
                    // ArrayList<SpaceBot> bots = loadBots(botStartLocations, mazeMap);
                    ArrayList<SpaceBot> bots = loadBots(botStartLocations);
                    maze.newLevel(mazeMap, bots);
                }
                String totalScore = command.getString("totalScore");
                statusBar.updateScore(totalScore);
                String levelNumber = command.getString("level");
                statusBar.updateLevel(levelNumber);
                statusBar.setInteractiveText(interactiveResponse);
            }
            //Acknowledge server response for to update the client maze.
            case "updateMaze" -> {
                JsonArray serialisedArray = command.getJsonArray("mazeArray");
                String interactiveResponse = command.getString("interactiveResponse");
                if (!serialisedArray.isEmpty()) {
                    // Json array of strings to Java array of strings
                    List<String> mazeList = serialisedArray.getList();
                    char[][] mazeMap = deserialiseJsonMaze(mazeList);
                    statusBar.setInteractiveText(interactiveResponse);
                    maze.updateMaze(mazeMap);
                }
            }
            //Acknowledge server response for updating the time.
            case "timer" -> {
                String timeValue = command.getString("time");
                logger.info("Received timer command with value: " + timeValue);
                statusBar.updateTimer(command.getString("time"));
            }
            //Acknowledge server response for when the game is won (Game Finished Case.)
            case "gameOver" -> {
                statusBar.stopTimer();
                String totalScore = command.getString("totalScore");
                String totalTime = command.getString("timeTaken");
                statusBar.updateScore(totalScore);
                displayGameOver(totalScore, totalTime, true); //if game won, pass total score
            }
            //Acknowledge server response for when the game is over (Player Died Case.)
            case "playerDead" -> {
                SpaceMazeSound.play("gameover");
                String totalTime = command.getString("timeTaken");
                String levelNumber = command.getString("level");
                statusBar.stopTimer();
                maze.stopTimer();
                displayGameOver(levelNumber, totalTime, false); //If game over, pass level number
            }
            //Acknowledge server response to update player lives.
            case "playerLives" -> {
                String playerLives = command.getString("lives");
                String interactiveResponse = command.getString("interactiveResponse");
                try {
                    int livesRemaining = Integer.parseInt(playerLives);
                    statusBar.updatePlayerLives(livesRemaining);
                    statusBar.setInteractiveText(interactiveResponse);
                    logger.info("Player lives remaining: " + livesRemaining);
                } catch (NumberFormatException e) {
                    logger.error("Cannot convert playerLives to Integer");
                }
            }

            //Acknowledge server navigation responses.
            case "viewHighScore" -> displayHighScore(); //displays HighScore panel
            case "howToPlay" -> displayHelpPanel();     //displays Help panel
            case "backToMenu" -> displayMainMenu();     //displays Main Menu
            case "mainMenu" -> mnClient.runMainMenuSequence();  //Goes back to main client window
            case "exit" -> closeGame(); //Does nothing.
        }
    }

    @Override
    public void closeGame() {
        
    }

    /**
     * Method that returns custom font.
     * @return Font
     */
    public Font getCustomFont() {
        return customFont;
    }

    public Font getAquireFont() {
        return aquireFont;
    }

    /**
     * Method that loads custom fonts from the resources.
     */
    public void loadCustomFont() {
        //Loading custom font - PublicPixelFont
        try {
            InputStream fontStream = this.getClass().getResourceAsStream("/fonts/PublicPixelFont.ttf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            
        } catch (FontFormatException | IOException e){
            e.printStackTrace();
            customFont = new Font("ArcadeClassics", Font.BOLD, 20);
        }
        customFont = customFont.deriveFont(40f);

        //Loading custom font - Aquire
        try {
            InputStream fontStream = this.getClass().getResourceAsStream("/fonts/Aquire-BW0ox.otf");
            aquireFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            aquireFont = new Font("ArcadeClassics", Font.BOLD, 20);
        }
        aquireFont = aquireFont.deriveFont(13f);
    }

    /**
     * Methods thats clears the current window and loads help panel.
     */
    public void displayHelpPanel() {
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(helpPanel);
        mnClient.getMainWindow().addSouth(developerCredits); 
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    /**
     * Methods thats clears the current window and loads Main Menu panel.
     */
    public void displayMainMenu() {
        startTimer(); //Start timer for header animations.
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(mainMenuPanel);
        mnClient.getMainWindow().addSouth(developerCredits);
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }
    /**
     * Methods thats clears the current window and loads High Score panel.
     */
    public void displayHighScore() {
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(highScorePanel);
        mnClient.getMainWindow().addSouth(developerCredits);
        mnClient.getMainWindow().addNorth(headerPanel);
        mnClient.getMainWindow().pack();
    }

    /**
     * Method that displays game over screen if the player runs out of life or wins the game.
     * @param totalScore - String value of total score the player has achieved, if game state is game finished, it is passed with level number.
     * @param totalTime - String value of total time played by the player
     * @param gameFinished - Boolean value representing state of the game, Game Over or Game Finished.
     */
    public void displayGameOver(String totalScore, String totalTime, Boolean gameFinished) {

        String levelNumber = "0"; //Initialize a empty string that will be based on game's current state
        if (gameFinished == false) {
            levelNumber = totalScore;   //If game was not finished, the levelNumber is passed as totalScore. 
        }
        // Releasing sounds for garbage collection
        SpaceMazeSound.closeSounds();

        String timeTaken = totalTime;
        
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new GridBagLayout());
        gameOverPanel.setPreferredSize(new Dimension(600, 600));
        gameOverPanel.setBackground(Color.BLACK);
        gameOverPanel.setBorder(whiteBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        Font gameOverFonts = customFont;
        gameOverFonts = gameOverFonts.deriveFont(16f);

        if (gameFinished == true) {
            //Messages to display if the game is finished, i.e player won the game
            greetingLabel = new JLabel("THANK YOU FOR PLAYING ");  

        } else {
            //Messages to display if the game is not finished, i.e player ran out of lives.
            greetingLabel = new JLabel("YOU RAN OUT OF LIVES COMMANDER! "); 
        }

        greetingLabel.setFont(gameOverFonts);
        greetingLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(-200, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gameOverPanel.add(greetingLabel, gbc);

        gameOverFonts = gameOverFonts.deriveFont(14f);

        if (gameFinished == true) {
            //Display total score if the game is finished.
            totalScoreLabel = new JLabel("TOTAL SCORE : " + totalScore);
        } else {
            //Display level reached if the game is not finished.
            totalScoreLabel = new JLabel("LEVEL REACHED : " + levelNumber);
        }
        
        totalScoreLabel.setFont(gameOverFonts);
        totalScoreLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gameOverPanel.add(totalScoreLabel, gbc);

        if (gameFinished == true) {
            //Display total time played if the game is finished.
            timeTakenLabel = new JLabel("TIME TAKEN : " + timeTaken );
        } else {
            //Display message if the game is not finished.
            timeTakenLabel = new JLabel("WELL PLAYED! ");
        }

        timeTakenLabel.setFont(gameOverFonts);
        timeTakenLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gameOverPanel.add(timeTakenLabel, gbc);

        //Instruction to exit out of the screen.
        pressToExitLabel = new JLabel("PRESS ESC TO EXIT TO MAIN MENU ");
        pressToExitLabel.setFont(gameOverFonts);
        pressToExitLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gameOverPanel.add(pressToExitLabel, gbc);

        gameOverHeaderPanel = new JPanel();
        gameOverHeaderPanel.setPreferredSize(new Dimension(600, 200));
        gameOverHeaderPanel.setBackground(Color.BLACK);
        gameOverHeaderPanel.setLayout(new GridBagLayout());
        gameOverHeaderPanel.setBorder(whiteBorder);
        GridBagConstraints c = new GridBagConstraints();

        JLabel gameOverHeaderText;
        customFont = customFont.deriveFont(40f);
        if (gameFinished == true) {
            //Messages to display in the headerPanel, if the game is finished.
            gameOverHeaderText = new JLabel("VICTORY!!");
        } else {
            //Messages to display in the headerPanel, if the game is not finished.
            gameOverHeaderText = new JLabel("GAME OVER");
        }
        
        gameOverHeaderText.setFont(customFont);
        gameOverHeaderText.setForeground(Color.WHITE);
        gameOverHeaderPanel.add(gameOverHeaderText, c);

        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(gameOverPanel);
        mnClient.getMainWindow().addSouth(developerCredits);
        mnClient.getMainWindow().addNorth(gameOverHeaderPanel);
        mnClient.getMainWindow().pack();

        headerPanel.repaint();
        gameOverPanel.setFocusable(true);
        gameOverPanel.requestFocusInWindow();  //Bring focus to the panel (Key press implementation purposes)

        gameOverPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }

    /** 
     * Sets up the client side bots as per level design.
     * @param  jsonArray of bot locations. mazeMap can be added back in if required.
     * @return ArrayList<Spacebot>
     */
    public ArrayList<SpaceBot> loadBots(JsonArray botStartLocations) {
        ArrayList<SpaceBot> bots = new ArrayList<>();
        
        int numBots = botStartLocations.size();
        for(int i = 0;i<numBots;i++) {
            JsonObject thisLoc = botStartLocations.getJsonObject(i);
            int x = thisLoc.getInteger("x");
            int y = thisLoc.getInteger("y");
            Point startLoc = new Point(x,y);
            SpaceBot newBot = new SpaceBot(startLoc);
            bots.add(newBot);
        }
        return bots;

    }


    /**
     * Called for the intial maze setup
     * @param jsonArray the maze sent from the server as a Json Object
     */
    public void loadMaze(JsonArray jsonArray, JsonArray botStartLocations) {
        mnClient.getMainWindow().clearAll();
        
        //Json array of strings to Java array of strings
        List<String> mazeList = jsonArray.getList();

        char[][] mazeMap = deserialiseJsonMaze(mazeList);

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

    /**
     * Methods that handles keypress events in the game over screen.
     * @param e - KeyEvent 
     */
    public void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                sendCommand("backToMenu");
                break;
        }
    }

    /**
     * Method that renders the main menu simple animation gif in the header panel
     */
    public void loadMainMenuHeader() {
        try {
            //Get TitleImages hashmap from images class.
            titleImages = Images.getImageHashMap();
            headerImage1 = new ImageIcon(titleImages.get(0)); //Set one image as default starter image.
            currentImageIndex = 0; 

            headerText = new JLabel(new ImageIcon(titleImages.get(currentImageIndex)));
            timer = new Timer(120, new ActionListener() { //Timer to switch image at every 120ms.

                @Override
                public void actionPerformed(ActionEvent e) {
                    currentImageIndex++;
                    if (currentImageIndex >= titleImages.size()) {
                        currentImageIndex = 0;
                    }
                    headerText.setIcon(new ImageIcon(titleImages.get(currentImageIndex)));
                    headerPanel.repaint();
                }
            });
            timer.start();

        } catch (Exception e) {
            logger.error("Image loading error?");
        }
    }

    /**
     * Method to stop Header Panel animation timer.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Method to start Header Panel animation timer.
     */
    public void startTimer() {
        if (timer != null) {
            timer.start();
        }
    }

    /**
     * Method that loads Help Panel.
     */
    public void loadHelpPanel() {
        //Getting Bomb Image, scaling it and using it as ImageIcon
        bombImage = Images.getImage("bombImages", 1);
        scaledBomb = bombImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        bombIcon = new ImageIcon(scaledBomb);

        //Getting Treasure Image, scaling it and using it as ImageIcon
        treasureImage = Images.getImage("chestImage");
        scaledTreasure = treasureImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        treasureIcon = new ImageIcon(scaledTreasure);

        //Getting Key Image, scaling it and using it as ImageIcon
        keyImage = Images.getImage("keyImages", 1);
        scaledKey = keyImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        keyIcon = new ImageIcon(scaledKey);

        //Getting WormHole Image, scaling it and using it as ImageIcon
        wormHoleImage = Images.getImage("wormHoleImage");
        scaledWormHole = wormHoleImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        wormHoleIcon = new ImageIcon(scaledWormHole);

        helpPanel = new JPanel();
        helpPanel.setLayout(new GridBagLayout());
        helpPanel.setPreferredSize(new Dimension(600, 600));
        helpPanel.setBackground(Color.BLACK);
        helpPanel.setBorder(whiteBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        aquireFont = aquireFont.deriveFont(13f);
        welcomeMessage = new JLabel("WELCOME COMMANDER, ");
        welcomeMessage.setFont(aquireFont);
        welcomeMessage.setForeground(Color.YELLOW);

        gbc.insets = new Insets(-100, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        helpPanel.add(welcomeMessage, gbc);

        helpText = new JLabel("YOUR MISSION IS TO COMPLETE ALL LEVELS, IN AS LITTLE TIME AS POSSIBLE  ");
        helpText1 = new JLabel("ONCE YOU HAVE COLLECTED THE KEYS, RACE TO THE GREEN EXIT PORTAL  ");
        helpText2 = new JLabel("WITHOUT GETTING CAUGHT BY AN ENEMY BOT ");
        helpText3 = new JLabel("YOU'VE ONLY GOT A FEW LIVES, SO DON'T RUN OUT. ");
        helpText4 = new JLabel(" - KEYS TO UNLOCK THE PORTAL ", keyIcon, JLabel.LEFT);
        helpText5 = new JLabel(" - DESTROY SOME WALLS ", bombIcon, JLabel.LEFT);
        helpText6 = new JLabel(" - REDUCE YOUR TIME BY 8 SECONDS. ", treasureIcon, JLabel.LEFT);
        helpText7 = new JLabel("BUT BE WARY OF THE WORMHOLE -");
        helpText7.setIcon(wormHoleIcon);
        helpText7.setHorizontalTextPosition(JLabel.LEFT);
        helpText.setHorizontalAlignment(JLabel.LEFT);
        helpText8 = new JLabel("IT TRANSPORTS YOU SOMEWHERE RANDOM IN THE MAZE. ");

        int i = 2; //Int value to set as gbc constraints gridy.
        for (Component c : new Component[] { 
            helpText, 
            helpText1, 
            helpText2, 
            helpText3,
        }) {
            c.setFont(aquireFont);
            c.setForeground(Color.YELLOW);
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.gridy = i;
            i++;
            helpPanel.add(c, gbc);
        }

        helpText4.setFont(aquireFont);
        helpText4.setForeground(Color.YELLOW);
        helpText4.setFont(aquireFont);
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = i;
        helpPanel.add(helpText4, gbc);
        i++;

        helpText5.setFont(aquireFont);
        helpText5.setForeground(Color.YELLOW);
        helpText5.setFont(aquireFont);
        gbc.insets = new Insets(10, -3, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = i;
        helpPanel.add(helpText5, gbc);
        i++;

        helpText6.setFont(aquireFont);
        helpText6.setForeground(Color.YELLOW);
        helpText6.setFont(aquireFont);
        gbc.insets = new Insets(10, -3, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = i;
        helpPanel.add(helpText6, gbc);
        i++;

        helpText7.setFont(aquireFont);
        helpText7.setForeground(Color.YELLOW);
        helpText7.setFont(aquireFont);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.gridy = i;
        helpPanel.add(helpText7, gbc);
        i++;

        helpText8.setFont(aquireFont);
        helpText8.setForeground(Color.YELLOW);
        helpText8.setFont(aquireFont);
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.gridy = i;
        helpPanel.add(helpText8, gbc);
        i++;

        backFromHelpButton = new JButton("Back");
        backFromHelpButton.addActionListener((evt) -> {
            sendCommand("backToMenu");
                customFont = customFont.deriveFont(16f);
                backFromHelpButton.setFont(customFont);
                backFromHelpButton.setForeground(Color.WHITE);
        });
        customFont = customFont.deriveFont(18f);
        backFromHelpButton.setPreferredSize(buttonDimension);
        backFromHelpButton.setMaximumSize(buttonDimension);
        backFromHelpButton.setFont(customFont);
        backFromHelpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backFromHelpButton.setForeground(Color.WHITE);
        backFromHelpButton.setContentAreaFilled(false);
        backFromHelpButton.setOpaque(false);
        backFromHelpButton.setBorderPainted(false);
        backFromHelpButton.setFocusPainted(false);

        backFromHelpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                customFont = customFont.deriveFont(18f);
                backFromHelpButton.setFont(customFont);
                backFromHelpButton.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                customFont = customFont.deriveFont(16f);
                backFromHelpButton.setFont(customFont);
                backFromHelpButton.setForeground(Color.WHITE);
            }
        });

        gbc.insets = new Insets(50, 0, 0, 0);
        gbc.gridy = i;
        helpPanel.add(backFromHelpButton, gbc);
    }

    /**
     * Method that loads HighScore Panel.
     */
    public void loadHighScorePanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        highScorePanel = new JPanel();
        highScorePanel.setLayout(new GridBagLayout());
        highScorePanel.setPreferredSize(new Dimension(600, 600));
        highScorePanel.setBackground(Color.BLACK);
        highScorePanel.setBorder(whiteBorder);

        highScoreLabel = new JLabel("Feature Discarded!", SwingConstants.CENTER);
        highScoreLabel.setPreferredSize(new Dimension(600, 200));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        customFont = customFont.deriveFont(20f);
        highScoreLabel.setFont(customFont);
        highScoreLabel.setForeground(Color.WHITE);
        
        gbc.insets = new Insets(100, 20, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        highScorePanel.add(highScoreLabel, gbc);

        backFromHighScoreButton = new JButton("Back");
        gbc.insets = new Insets(70, 20, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        highScorePanel.add(backFromHighScoreButton, gbc);
        backFromHighScoreButton.addActionListener((evt) -> sendCommand("backToMenu"));
    }

    /**
     * Method that loads Main Menu Panel.
     */
    public void loadMainMenu() {
        GridBagConstraints gbc = new GridBagConstraints();
        //Menu Section
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setPreferredSize(new Dimension(600, 600));
        mainMenuPanel.setBackground(Color.BLACK);
        mainMenuPanel.setBorder(whiteBorder);

        //Buttons panel inside menu section
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setPreferredSize(new Dimension(300,250));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS)); //Box layout with items arranged vertically.

        //Start Game Button
        startGameButton = new JButton("START GAME");
        startGameButton.addActionListener((evt) -> {
            customFont = customFont.deriveFont(24f);
            startGameButton.setFont(customFont);
            startGameButton.setForeground(Color.WHITE);
            sendCommand("START");

        });

        //High Score Button
        highScoreButton = new JButton("HIGH SCORE");
        highScoreButton.addActionListener((evt) -> {
            customFont = customFont.deriveFont(14f);
            highScoreButton.setFont(customFont);
            highScoreButton.setForeground(Color.WHITE);
            sendCommand("SCORE");
        });

        //How to play/Help Button
        helpButton = new JButton("HOW TO PLAY");
        helpButton.addActionListener((evt) -> {
            customFont = customFont.deriveFont(14f);
            helpButton.setFont(customFont);
            helpButton.setForeground(Color.WHITE);
            sendCommand("HELP");
        });

        //Main Menu Button
        mainMenuButton = new JButton("MAIN MENU");
        mainMenuButton.addActionListener((evt) -> {
            customFont = customFont.deriveFont(14f);
            mainMenuButton.setFont(customFont);
            mainMenuButton.setForeground(Color.WHITE);
            sendCommand("MENU");
            
        });

        //Button visual settings
        for (Component c : new Component[] { startGameButton, highScoreButton, helpButton, mainMenuButton }) {
            if (c == startGameButton) {
                customFont = customFont.deriveFont(24f);
            } else {
                customFont = customFont.deriveFont(14f);
            }
            c.setPreferredSize(buttonDimension);
            c.setMaximumSize(buttonDimension);
            c.setFont(customFont);
            ((AbstractButton) c).setAlignmentX(Component.CENTER_ALIGNMENT);
            c.setForeground(Color.WHITE);
            ((AbstractButton) c).setContentAreaFilled(false);
            ((AbstractButton) c).setOpaque(false);
            ((AbstractButton) c).setBorderPainted(false);
            ((AbstractButton) c).setFocusPainted(false);
        }

        //Hover effect for button - Start Game button
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                customFont = customFont.deriveFont(25f);
                startGameButton.setFont(customFont);
                startGameButton.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                customFont = customFont.deriveFont(24f);
                startGameButton.setFont(customFont);
                startGameButton.setForeground(Color.WHITE);
            }
        });

        //Hover effect for buttons - High Score Button, Help Button, Main Menu Button, Exit Button
        for (Component c : new Component[] { highScoreButton, helpButton, mainMenuButton }) {
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    customFont = customFont.deriveFont(16f);
                    c.setFont(customFont);
                    c.setForeground(Color.YELLOW);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    customFont = customFont.deriveFont(14f);
                    c.setFont(customFont);
                    c.setForeground(Color.WHITE);
                }
            });
        }

        //Adding buttons to the buttons panel
        for (Component c : new Component[] { startGameButton, highScoreButton, helpButton, mainMenuButton }) {
            buttonsPanel.add(c);
            buttonsPanel.add(Box.createRigidArea(new Dimension(5,5)));

        }

        //Using GridBagLayout to position buttons panel inside menu section
        gbc.gridx = 0;
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(-200, 0, 0, 0);
        mainMenuPanel.add(buttonsPanel, gbc);

        //Credit Section
        developerCredits = new JLabel("Developed by: Andy, Nik, Natasha, Niraj");
        Color light_grey = new Color(153, 153, 153);
        developerCredits.setForeground(light_grey);
    }
}

