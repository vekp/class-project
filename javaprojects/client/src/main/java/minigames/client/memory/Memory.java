package minigames.client.memory;

import io.vertx.core.json.JsonObject;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.client.Tickable;
import minigames.client.Animator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;




import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.Timer;

/**
 * COSC220 Assessment 3
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 * Memory Card Game
 */
public class Memory implements GameClient, ActionListener, Tickable { //, MouseListener {

    // Client instance
    MinigameNetworkClient mnClient;

    // Send commands to server
    GameMetadata gm;
    Animator animator;

    // Player
    String player;

    /** Swing components */
    private JPanel mainPanel, gameMenuPanel, commandPanel, headingPanel, playerPanel, gameOptionsPanel, cardGridPanel;
    private JLabel title, playerName, matches, stopwatch, difficulty;
    private JButton newGameButton, restartLevelButton, exitButton, achievementsButton;
    private Border margin;
    private int rows = 3;
    private int columns = 6;
    JButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18;
    JPanel buttonPanel;
    int btnContainerWidth;
    int btnContainerHeight;

    

    ImageIcon cardBackImage = new ImageIcon(getClass().getResource("/memory/images/playing_cards/back/card_back_black.png"));
    // Path to card images directory (card front images only)
    String cardImagesDirectory = getClass().getResource("/memory/images/playing_cards/front/").getPath();

    // this needs to be changed again so that the image matches the relevant card suit/rank 
    ImageIcon cardFrontImage = new ImageIcon(getClass().getResource("/memory/images/playing_cards/front/_2_of_clubs.png"));


    JTextArea textArea;

    // Game variable
    boolean gameStarted = false;
    int matchesCounter = 0;
    int [] timeElapsed = {1, 0}; // {mins, seconds}
    Timer timer;
    

    //May use this later to keep track of the flipped/unflipped cards, but i dont think we need it anymore...
    //boolean [] cardState = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    
    JPanel GUI;
    JFrame MemoryWindow;

    /** Initialize Swing components */
    public Memory() {
        GUI = new JPanel();
        MemoryGUI();
        MemoryWindow = new JFrame("Memory");
        MemoryWindow.setSize(800,800);
        MemoryWindow.add(GUI);
    }


    public void MemoryGUI() {
        margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        
        setPlayerPanelDisplay();
        setGameOptionsPanelDisplay();
        setButtonPanelDisplay();
        setGameMenuPanelDisplay();
        setMainPanelDisplay();
        GUI.add(mainPanel, BorderLayout.CENTER);
    }

   
    private void setMainPanelDisplay(){
        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMenuPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }


    private void setGameMenuPanelDisplay(){

        // Create the heading panel
        headingPanel = new JPanel();
        headingPanel.setLayout(new GridLayout(1, 1));
        title = new JLabel("Pair Up - A Memory Card Game");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        title.setHorizontalAlignment(JLabel.CENTER);
        headingPanel.add(title);

        // Create the game menu panel
        gameMenuPanel = new JPanel();
        gameMenuPanel.setLayout(new GridLayout(3, 0));
        gameMenuPanel.setBorder((new EmptyBorder(0,0,15,0)));
        gameMenuPanel.add(headingPanel); // Add heading panel to the game menu panel
        gameMenuPanel.add(playerPanel); // Add playerPanel to gameMenuPanel
        gameMenuPanel.add(gameOptionsPanel); // Add gameOptionsPanel to gameMenuPanel

    }

    private void setPlayerPanelDisplay() {
        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 4));
        playerPanel.setPreferredSize(new Dimension(800, 30));

        playerName = new JLabel("Player: " + player); //player); // Placeholder text for player name
        playerName.setFont(new Font("Arial", Font.BOLD, 12));
        playerName.setHorizontalAlignment(JLabel.CENTER);

        difficulty = new JLabel("Difficulty: NEED TO FIX");
        difficulty.setFont(new Font("Arial", Font.BOLD, 12));
        difficulty.setHorizontalAlignment(JLabel.CENTER);

        matches = new JLabel("Pairs matched: " + "NEED TO FIX"); // + matchesCounter + "/8"); // Placeholder text for matched pairs
        matches.setFont(new Font("Arial", Font.BOLD, 12));
        matches.setHorizontalAlignment(JLabel.CENTER);

        //stopwatch = new JLabel(String.format("Time elapsed: %02d:%02d", timeElapsed[0], timeElapsed[1])); // Placeholder text for Timer
        stopwatch = new JLabel("Time elapsed: NEED TO FIX");
        stopwatch.setFont(new Font("Arial", Font.BOLD, 12));
        stopwatch.setHorizontalAlignment(JLabel.CENTER);

        // Add the player information to the player panel
        playerPanel.add(playerName);
        playerPanel.add(difficulty);
        playerPanel.add(matches);
        playerPanel.add(stopwatch);
    }


    private void setGameOptionsPanelDisplay() {
        // Create the game options panel which hosts new game, restart level, and exit buttons
        gameOptionsPanel = new JPanel();
        gameOptionsPanel.setLayout(new GridLayout(1, 5));

        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));

        restartLevelButton = new JButton("Restart Level");
        restartLevelButton.setFont(new Font("Arial", Font.BOLD, 16));

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(e -> mnClient.runMainMenuSequence());

        // Create a JComboBox to select difficulty level. This updates the cardGridPanel.
        //JComboBox<String> difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        //difficultyComboBox.setFont(new Font("Arial", Font.BOLD, 16));
        //difficultyComboBox.setSelectedItem("Medium");

        // Create achievements button that talks to the API
        achievementsButton = new JButton("Achievements");
        achievementsButton.setFont(new Font("Arial", Font.BOLD, 16));
        //achievementsButton.addActionListener(e -> mnClient.getGameAchievements(player, gm.gameServer()));

        // Add game options to gameOptionsPanel
        //gameOptionsPanel.add(difficultyComboBox);
        gameOptionsPanel.add(achievementsButton);
        gameOptionsPanel.add(newGameButton);
        gameOptionsPanel.add(restartLevelButton);
        gameOptionsPanel.add(exitButton);
    }

    public void updatePlayerName(String player) {
        playerName.setText("Player: " + player);
    }

    public JButton exitButton() {
        return exitButton;
    }


    private void setButtonPanelDisplay(){
        btnContainerWidth = 500;
        btnContainerHeight = 465;

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, columns, 0, 0));
        buttonPanel.setMaximumSize(new Dimension(btnContainerWidth, btnContainerHeight));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        ImageIcon image = resizeImageIcon(cardBackImage,-1,btnContainerHeight/rows-5);

        btn1 = new JButton(image);
        btn2 = new JButton(image);
        btn3 = new JButton(image);
        btn4 = new JButton(image);
        btn5 = new JButton(image);
        btn6 = new JButton(image);
        btn7 = new JButton(image);
        btn8 = new JButton(image);
        btn9 = new JButton(image);
        btn10 = new JButton(image);
        btn11 = new JButton(image);
        btn12 = new JButton(image);
        btn13 = new JButton(image);
        btn14 = new JButton(image);
        btn15 = new JButton(image);
        btn16 = new JButton(image);
        btn17 = new JButton(image);
        btn18 = new JButton(image);

        btn1.addActionListener((evt) -> sendCommand("Flip_Card_1"));
        btn2.addActionListener((evt) -> sendCommand("Flip_Card_2"));
        btn3.addActionListener((evt) -> sendCommand("Flip_Card_3"));
        btn4.addActionListener((evt) -> sendCommand("Flip_Card_4"));
        btn5.addActionListener((evt) -> sendCommand("Flip_Card_5"));
        btn6.addActionListener((evt) -> sendCommand("Flip_Card_6"));
        btn7.addActionListener((evt) -> sendCommand("Flip_Card_7"));
        btn8.addActionListener((evt) -> sendCommand("Flip_Card_8"));
        btn9.addActionListener((evt) -> sendCommand("Flip_Card_9"));
        btn10.addActionListener((evt) -> sendCommand("Flip_Card_10"));
        btn11.addActionListener((evt) -> sendCommand("Flip_Card_11"));
        btn12.addActionListener((evt) -> sendCommand("Flip_Card_12"));
        btn13.addActionListener((evt) -> sendCommand("Flip_Card_13"));
        btn14.addActionListener((evt) -> sendCommand("Flip_Card_14"));
        btn15.addActionListener((evt) -> sendCommand("Flip_Card_15"));
        btn16.addActionListener((evt) -> sendCommand("Flip_Card_16"));
        btn17.addActionListener((evt) -> sendCommand("Flip_Card_17"));
        btn18.addActionListener((evt) -> sendCommand("Flip_Card_18"));

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);
        buttonPanel.add(btn3);
        buttonPanel.add(btn4);
        buttonPanel.add(btn5);
        buttonPanel.add(btn6);
        buttonPanel.add(btn7);
        buttonPanel.add(btn8);
        buttonPanel.add(btn9);
        buttonPanel.add(btn10);
        buttonPanel.add(btn11);
        buttonPanel.add(btn12);
        buttonPanel.add(btn13);
        buttonPanel.add(btn14);
        buttonPanel.add(btn15);
        buttonPanel.add(btn16);
        buttonPanel.add(btn17);
        buttonPanel.add(btn18);

    }


    /**
     * a method to resize the ImageIcon's image and return the resized ImageIcon
     * @param originalImageIcon the original ImageIcon to be resized
     * @param targetWidth required width of the resized ImageIcon (cannot be 0. If value is negative, then a value is
     *                    substituted to maintain the aspect ratio of the original image dimensions)
     * @param targetHeight required height of the resized ImageIcon (cannot be 0. If value is negative, then a value is
     *      *             substituted to maintain the aspect ratio of the original image dimensions)
     * @return resized ImageIcon
     */
    public ImageIcon resizeImageIcon(ImageIcon originalImageIcon, int targetWidth, int targetHeight){
        Image resizedImage = originalImageIcon.getImage().getScaledInstance(targetWidth,targetHeight,Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }


 
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

    }



 // THIS METHOD CURRENTLY NOT IN USE - MAY NEED IT LATER FOR THE TIMER OR MULTIPLAYER??....
    /** a variable called every tick that probes the server for the current state */
    @Override
    public void tick(Animator al, long now, long delta) {
        JsonObject result = new JsonObject();
        result.put("command", "update");
        
        //result.put("moves", moveCounter);
        //sendCommand(result);

        al.requestTick(this);
    }


    // Run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Memory();
            }
        });
    }


    // THIS METHOD CURRENTLY NOT IN USE - MAY NEED IT LATER....
    public ImageIcon isFlipped(boolean flipped){
        if (flipped){
            return new ImageIcon(getClass().getResource("/memory/images/playing_cards/front/_2_of_clubs.png"));
        }else {
           return cardBackImage;
         }      
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

    public void sendCommand(JsonObject command) {
        mnClient.send(
            new CommandPackage(
                gm.gameServer(), gm.name(), player, Collections.
                singletonList(command)));
    }


    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        //playerName.setText("Player: " + player);
        
        // Update Player Name
        updatePlayerName(player);
        // Exit button to return to main game menu - Working
        exitButton().addActionListener(e -> mnClient.runMainMenuSequence());

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(GUI);

        animator = mnClient.getAnimator();
        animator.requestTick(this);

        // FIX - Ask player which level difficulty they want to try before this pops up?
        // Set popup message to ask if player wants to start game: YES or NO
        
        /* 
        int choice = JOptionPane.showConfirmDialog(null, "Start the game?", "Memory Card Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // On selection of YES - Timer for stopwatch starts countdown
            timer = new Timer(1000, new TimerListener());
            timer.start();
        }
*/
        //textArea.append("Starting...");

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't need to say "break;" at the end of our cases)
        switch (command.getString("command")) {

            case "Flip_Card_1" -> {
                btn1.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_2" -> {
                btn2.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_3" -> {
                btn3.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_4" -> {
                btn4.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_5" -> {
                btn5.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_6" -> {
                btn6.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_7" -> {
                btn7.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_8" -> {
                btn8.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_9" -> {
                btn9.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_10" -> {
                btn10.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_11" -> {
                btn11.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_12" -> {
                btn12.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_13" -> {
                btn13.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_14" -> {
                btn14.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_15" -> {
                btn15.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_16" -> {
                btn16.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_17" -> {
                btn17.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }
            case "Flip_Card_18" -> {
                btn18.setIcon(resizeImageIcon(cardFrontImage,-1,btnContainerHeight/rows-5));
            }

           // case "clearText" -> textArea.setText("");
           // case "appendText" -> textArea.setText(textArea.getText() + command.getString("text"));
        }
    }
 

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */

/*
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newGameButton) {
            //TODO
            mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(new JsonObject().put("command", "newGame"))));
            System.out.println("New game started");
        }
        if (e.getSource() == restartLevelButton) {
            //TODO
           mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(new JsonObject().put("command", "restartLevel"))));
            System.out.println("Level Restarted...");
        }
        if (e.getSource() == exitButton) {
            //TODO
            closeGame();
            System.out.println("Testing...");
        }

    }
*/
    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    //@Override
    //public void mouseClicked(MouseEvent e) {

    //}

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    //@Override
    //public void mousePressed(MouseEvent e) {

    //}

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    //@Override
    //public void mouseReleased(MouseEvent e) {

    //}

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    //@Override
    //public void mouseEntered(MouseEvent e) {

    //}

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    //@Override
    //public void mouseExited(MouseEvent e) {

    //}
    /**
     * a method to resize the ImageIcon's image and return the resized ImageIcon
     * @param originalImageIcon the original ImageIcon to be resized
     * @param targetWidth required width of the resized ImageIcon (cannot be 0. If value is negative, then a value is
     *                    substituted to maintain the aspect ratio of the original image dimensions)
     * @param targetHeight required height of the resized ImageIcon (cannot be 0. If value is negative, then a value is
     *      *             substituted to maintain the aspect ratio of the original image dimensions)
     * @return resized ImageIcon
     */
    //public ImageIcon resizeImageIcon(ImageIcon originalImageIcon, int targetWidth, int targetHeight){
    //Image resizedImage = originalImageIcon.getImage().getScaledInstance(targetWidth,targetHeight,Image.SCALE_SMOOTH);
    //return new ImageIcon(resizedImage);
    //}

    @Override
    public void closeGame() {
        // Nothing to do        
    }

}


