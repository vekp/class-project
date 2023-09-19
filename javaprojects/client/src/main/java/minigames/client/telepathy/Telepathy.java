package minigames.client.telepathy;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import java.util.Collections;

import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.client.telepathy.TelepathyIcons;
import minigames.rendering.GameMetadata;
import minigames.utilities.MinigameUtils;


import minigames.commands.CommandPackage;
import minigames.client.notifications.NotificationManager;
import minigames.client.notifications.DialogManager;

import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommandHandler;
import minigames.telepathy.TelepathyCommands;
import minigames.telepathy.Symbols;
import minigames.telepathy.Colours;
import minigames.telepathy.Tile;

import minigames.telepathy.State;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.lang.String;
import java.util.HashMap;
import java.util.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.awt.Point;


/**
 * A Telepathy GameClient. Handles displaying information of a Telepathy game running on the
 * server to the player.
 * 
 * Implements Tickable to animate and keep the client up to date with the current state of the 
 * game running on the server.
 */
public class Telepathy implements GameClient, Tickable{

    private static final Logger logger = LogManager.getLogger(Telepathy.class);

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    DialogManager telepathyNotificationManager;

    /** Your name */    
    String player;

    ActionListener buttonListener;

    JPanel telepathyBoard; // integrated game board
    JPanel gridIndexWest; // alphabetical index
    JPanel gridIndexNorth; // numerical index
    JPanel board; // game board grid
    JPanel sidePanel; // board game side panel
    JPanel colourSymbolPanel; //  nested panel listing the colours and symbols in the gameboard

    ArrayList<JButton> buttonColour; // list of colours used in game
    ArrayList<JButton> buttonSymbols; // list of buttons with symbol icons used in game

    JButton player1Turn; 
    JButton player2Turn;
    
    ArrayList<ImageIcon> allIcons; // ordered list of icons
    ArrayList<Point> allCoordinatesList; // list of board coordinates

    Map<Point, ImageIcon> mappedIcons; // a map to store mapped icons to board buttons
 

    int ROWS = 9; // adjustable variable for grid size
    int COLS = 9; // adjustable variable for grid size
    int GAP = 5; // board cell padding

    String columns = "ABCDEFGHI"; // string of letters to label y coordinate
    
    JButton[][] buttonGrid; // 2D button array

    // Client information
    private int xCoord; // X coordinate of button on board that is pressed
    private int yCoord; // Y coordiante of button on board that is pressed

    // Tick information
    private boolean ticking;
    private long last;

    // Server information
    private State serverState;
    private HashMap<String, JComponent> componentList; // Maintains references to swing elements that need to be modified 
    
    // Guesses and eliminated rows/columns
    HashSet<Integer> eliminatedColumns;
    HashSet<Integer> eliminatedRows;
    HashSet<Tile> guessedTiles;

    /**
     * A Telepathy board UI, a 9 x 9 2D array of Jbuttons with coordinates around the 
     * border.
     */
    public Telepathy(){
        this.ticking = true;
        this.last = System.nanoTime();
        this.serverState = null;

        this.eliminatedColumns =  new HashSet<>();
        this.eliminatedRows = new HashSet<>();
        this.guessedTiles = new HashSet<>();

        this.componentList = new HashMap<>();
        this.buttonGrid = new JButton[COLS][ROWS];

        this.player1Turn = new JButton();
        this.player2Turn = new JButton();
        

        telepathyBoard = new JPanel();
        telepathyBoard.setLayout(new BorderLayout());

        // creates a panel using the GridLayout manager to label y coordinates numerically
        gridIndexWest = new JPanel();
        gridIndexWest.setBorder(new EmptyBorder(0, 30, 0, 0));
        gridIndexWest.setLayout(new GridLayout(ROWS, 1, GAP, GAP));
        for (int i = 0; i < this.buttonGrid.length; i++) {
            gridIndexWest.add(new JLabel(Integer.toString(i + 1)));
        }

        //creates a panel using the GridLayout manager to label x coordinates alphabetically
        gridIndexNorth = new JPanel();
        gridIndexNorth.setBorder(new EmptyBorder(30, 0, 0, 0));
        gridIndexNorth.setLayout(new FlowLayout());
        for (int i = 0; i < this.buttonGrid.length; i++) {
            gridIndexNorth.add(new JLabel("        " + columns.substring(i, i + 1) + "   "));
       
        }

       
        // Button to go back to the main menu
        JButton backButton = new JButton("Menu");
        backButton.setPreferredSize(new Dimension(20, 40));
        backButton.addActionListener(e -> {
            telepathyNotificationManager.dismissCurrentNotification();
            clearButtonBackgrounds();
            enableButtonGrid();
            sendCommand(TelepathyCommands.QUIT);
        });
        this.componentList.put("backButton", backButton);

        JButton readyButton = new JButton("READY");
        readyButton.addActionListener(e -> {
            sendCommand(TelepathyCommands.TOGGLEREADY);
        });
        this.componentList.put("readyButton", readyButton);


      
        // panel to display buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(readyButton);
        buttonPanel.add(backButton);
     
    

         //a panel for the 2D button array
        board = new JPanel();
        board.setLayout(new GridLayout(COLS, ROWS, GAP, GAP));
        board.setMaximumSize(new Dimension(500, 500));
        board.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        buttonListener = evt -> {
            // on click event: iterates through the button array
            /* compares the grid button with the get(Source) JButton and displays a pop-up 
            message enabling players to finalise their selection */
            JButton selectedBtn = (JButton) evt.getSource();
            for (int row = 0; row < this.buttonGrid.length; row++) {
                for (int col = 0; col < this.buttonGrid[row].length; col++) {
                    if (this.buttonGrid[col][row] == selectedBtn) {
                        this.xCoord = col;
                        this.yCoord = row;
                        disableButtonGrid();

                        if (this.serverState == State.TILESELECTION) {
                            activateTargetTileMessage();    
                        } else if (this.serverState == State.RUNNING) {
                            activateQuestionGuessMessage();
                        }
                    }
                    /* Old version of selecting buttons
                    if (this.buttonGrid[col][row] == selectedBtn && this.serverState == State.TILESELECTION) {
                        
                        disableButtonGrid();
                         
                    }else if (this.buttonGrid[col][row] == selectedBtn && this.serverState == State.RUNNING){
                        disableButtonGrid();
                        activateQuestOrGuessMessage(col, row); 
                    }*/
                }
            }
        };

       
        allCoordinatesList = TelepathyIcons.allCoordinates(this.buttonGrid);
        allIcons = TelepathyIcons.allIcons();
        // a map of ordered icons to board coordinates
        mappedIcons = TelepathyIcons.mappedIcons(allCoordinatesList, allIcons);


        for (int row = 0; row < this.buttonGrid.length; row++) {
            for (int col = 0; col < this.buttonGrid[row].length; col++) {
                ImageIcon icon = mappedIcons.get(new Point(col, row));
                this.buttonGrid[col][row] = new JButton(icon);
                this.buttonGrid[col][row].setPreferredSize(new Dimension(50, 50));
                this.buttonGrid[col][row].addActionListener(buttonListener);
                board.add(buttonGrid[col][row]);
            }

        }

        // added components to the telepathyBoard panel
       
        telepathyBoard.add(buttonPanel, BorderLayout.SOUTH); 
        telepathyBoard.add(gridIndexWest, BorderLayout.WEST, SwingConstants. CENTER);
        telepathyBoard.add(gridIndexNorth, BorderLayout.NORTH, SwingConstants.CENTER);
        telepathyBoard.add(board, BorderLayout.CENTER);

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));        

        //Font font = new Font("futura", Font.BOLD, 16);
        //JLabel heading = new JLabel("Tile Features");
       // heading.setFont(font);
       // heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        TitledBorder border = new TitledBorder("Tile Features");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);

        colourSymbolPanel = new JPanel();
        colourSymbolPanel.setBorder(border);

        colourSymbolPanel.setLayout(new FlowLayout());
        colourSymbolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colourSymbolPanel.setOpaque(true);
        JPanel colours = colourSideTiles();
        JPanel symbols = symbolSideTiles();
       
        colourSymbolPanel.add(colours);
        colourSymbolPanel.add(symbols);

        JPanel lobby = gameLobby();

       // sidePanel.add(heading);
        sidePanel.add(colourSymbolPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sidePanel.add(lobby);

        
    }



    /**
     * a method that disables all buttons on the board
     */

    public void disableButtonGrid(){

        for (int row = 0; row < this.buttonGrid.length; row++) {
            for (int col = 0; col < this.buttonGrid[row].length; col++) {
                this.buttonGrid[col][row].setEnabled(false);
            }
        }

    }

    /**
     * a method that enables all buttons on the board
     */

    public void enableButtonGrid(){

        for (int row = 0; row < this.buttonGrid.length; row++) {
            for (int col = 0; col < this.buttonGrid[row].length; col++) {
                if(!this.eliminatedRows.contains(row) && !this.eliminatedColumns.contains(col)){
                    this.buttonGrid[col][row].setEnabled(true);
                }
            }
        }

    }


    /**
     * a method that clears all selected buttons
     */
    public void clearButtonBackgrounds(){
        for (int row = 0; row < this.buttonGrid.length; row++) {
            for (int col = 0; col < this.buttonGrid[row].length; col++) {
                this.buttonGrid[col][row].setBackground(null);
            }
        }
    }
    
    /**
     * Create a JPanel that can be used to display a message to the user.
     * 
     * @param heading: The main heading to use at the top of the panel.
     * @param subHeading: A sub-heading to use underneath the main heading.
     * @param descriptionString: The message to display to the user.
     * @return JPanel containing all the required components to display a message.
     */
    private JPanel makeMessagePopup(String heading, String subHeading, String descriptionString) {

        // Sub-panels for the heading
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.PAGE_AXIS));

        Font font = new Font("futura", Font.BOLD, 20);
        Font subFont = new Font("georgia", Font.ITALIC, 18);

        JLabel headingLabel = new JLabel(heading);
        JLabel subHeadingLabel = new JLabel(subHeading);

        headingLabel.setFont(font);
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subHeadingLabel.setFont(subFont);
        subHeadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headingPanel.add(headingLabel);
        headingPanel.add(subHeadingLabel);

        // Main text for the popup
        JTextPane descriptionTextPane = new JTextPane();
        descriptionTextPane.setText("\n" + descriptionString);
        descriptionTextPane.setEditable(false);
        descriptionTextPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // The main popup JPanel
        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.PAGE_AXIS));
        popupPanel.setPreferredSize(new Dimension(300, 300));

        popupPanel.add(headingPanel);
        popupPanel.add(descriptionTextPane);

        return popupPanel;
    }

    /**
     * Create a JPanel that can be used to display a message and give the user a 
     * choice to make. 
     * 
     * @param headingString: The main heading to use at the top of the panel.
     * @param descriptionString: The message to display to the user.
     * @param buttonOneLabel: The label to apply to the first button option.
     * @param buttonTwoLabel: The label to apply to the second button option.
     * @param listenerOne: ActionListener defining action for first button press.
     * @param listenerTwo: ActionListener defining action for second button press.
     * @return The JPanel with a message and two buttons prompting the user.
     */
    private JPanel makeChoicePopup(String headingString, String descriptionString, String buttonOneLabel, String buttonTwoLabel, ActionListener listenerOne,
            ActionListener listenerTwo) {

        // Create the heading
        Font font = new Font("futura", Font.BOLD, 20);

        JLabel headingLabel = new JLabel(headingString);
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headingLabel.setFont(font);

        // Create the main text pane
        JTextPane descriptionTextPane = new JTextPane();
        descriptionTextPane.setText("\n\n\n" + descriptionString);
        descriptionTextPane.setEditable(false);

        // Create the buttons and add the ActionListeners
        JPanel buttonPanel = new JPanel();
        JButton buttonOne = new JButton(buttonOneLabel);
        JButton buttonTwo = new JButton(buttonTwoLabel);
        buttonOne.addActionListener(listenerOne);
        buttonTwo.addActionListener(listenerTwo);

        buttonPanel.add(buttonOne);
        buttonPanel.add(buttonTwo);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create the JPanel to contain all the components
        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setPreferredSize(new Dimension(300, 290));

        choicePanel.add(headingLabel);
        choicePanel.add(descriptionTextPane);
        choicePanel.add(buttonPanel);

        return choicePanel;

    }

    /**
     * Display the game welcome message.
     */
    private void activateWelcomeMessage() {
        JPanel welcomePanel = makeMessagePopup(
                "Telepathy",
                "are you telepathic " + player + "? ",
                "  Can you deduce your opponent's secret tile?!" +
                "\n\nGame Play" + "\nQuestions are asked by selecting a tile from the board. Then you will find out if" +
                " the coordinates, colour AND/OR symbol match your opponent's tile." +
                " IF you've scored a match, the tile will be highlighted! \nUse this information to help you ask" +
                " the next question." +
                " Keep asking questions till you're SURE you KNOW your opponent's tile!" +
                "\n\nWhen you're ready, select the READY button." + "    Wait for the other player to join :D\n");

        //welcomeMessage = popupWelcomeMessage();
        telepathyNotificationManager.showMessageDialog("Telepathy", welcomePanel);
    }

    /**
     * Display the tile selection state popup message.
     */
    private void activateTileSelectMessage() {
        JPanel tileSelectPanel = makeMessagePopup(
                "Secret Tile Selection",
                "",
                "\nNotice how each tile on the board has a set of coordinates, a colour and a symbol..." +
                "\n\n\nThe list of colours and symbols at the side will help you track your guesses." +
                " When your question has no matches, the tile's row, column, colour and symbol will be eliminated." +
                "\n\n\nNow it's time to choose your secret tile for your opponent to guess!"
                );

        telepathyNotificationManager.showMessageDialog("Telepathy", tileSelectPanel);
    }
    
    /**
     * Display the game running popup message.
     */
    private void activateGameRunningMessage() {
        JPanel gameRunningPanel = makeMessagePopup(
            "Time to play", 
            "", 
            "\n                   The game has started!" +
            "\n\n\nWhen it is your turn, the board will activate and you can ask a question" +
            " by selecting a tile." + 
            "\n\n\n                        Good luck!" +
            "\n\n\n  REMEMBER: the game is won with a player's\n                      final guess..."
            );

        telepathyNotificationManager.showMessageDialog("Telepathy", gameRunningPanel);
    }

    /**
     * Display the game over popup message.
     * @param winLose: String received from the server with the name of the winner
     *  of this game of Telepathy.
     */
    private void activateGameOverMessage(String gameOverText, String gameWinner) {
        
        String winLose = "";
        if(gameWinner.equals(this.player)){
            winLose = "You win!";
        } else{
            winLose = "You lose!";
        }
        
        JPanel gameOverPanel = makeMessagePopup(
            "Game Over",
            winLose, 
                gameOverText + "\nThanks for playing!");
        telepathyNotificationManager.showMessageDialog("Telepathy", gameOverPanel);
    }


    /**
     * A method to display the question or guess message as a popup message.
     * Prompts the user using two buttons.
     */
    private void activateQuestionGuessMessage(){
        // Define actions to take when player presses Question/Final Guess button
        ActionListener questionListener = e -> {
            sendCommand(TelepathyCommands.ASKQUESTION, Integer.toString(this.xCoord), Integer.toString(this.yCoord));
            telepathyNotificationManager.dismissCurrentNotification();
        };

        ActionListener finalListener = e -> {
            sendCommand(TelepathyCommands.FINALGUESS, Integer.toString(this.xCoord), Integer.toString(this.yCoord));
            telepathyNotificationManager.dismissCurrentNotification();
        };

        // Create the JPanel to display in the notification
        JPanel choicePanel = makeChoicePopup(
            "Question Or Final Guess?",
            "Are you asking if your opponent's tile shares any features with this tile?\n\n\nOr is this your Final Guess?\n\n\nChoose wisely!",
            "Question",
            "Final Guess",
            questionListener,
            finalListener
            );

        telepathyNotificationManager.showNotification(choicePanel, false);
    }

    /**
     * A method to display the selectTargetTile Jpanel as a popup message.
     */
    public void activateTargetTileMessage() {
        // Define actions to take when player presses yes/no buttons
        ActionListener yesListener = e -> {
            sendCommand(TelepathyCommands.CHOOSETILE, Integer.toString(this.xCoord), Integer.toString(this.yCoord));
            setButtonBorder(this.buttonGrid[this.xCoord][this.yCoord], new Color(232,224,31,255));
            telepathyNotificationManager.dismissCurrentNotification();
        };

        ActionListener noListener = e -> {
            telepathyNotificationManager.dismissCurrentNotification();
            enableButtonGrid();
        };

        // Create the JPanel to display in the notification
        JPanel confirmTargetTile = makeChoicePopup(
            "Is this your secret tile?",
            "Select 'Yes' to continue or 'No' to make another choice",
            "Yes",
            "No",
            yesListener,
            noListener);
        telepathyNotificationManager.showNotification(confirmTargetTile, false); 
    }

    /**
     * a method that generates a panel of buttons displaying game colours
     */
   
    public JPanel colourSideTiles(){

        // a list to store buttons with coloured backgrounds
        buttonColour = new ArrayList<>();
        JPanel colours = new JPanel();
        colours.setLayout(new BoxLayout(colours, BoxLayout.Y_AXIS));

        // a list to call colour constants
        ArrayList<Colours> coloursList = new ArrayList<>(List.of(Colours.values()));
        
         // creates colour buttons and adds to the buttonColour list
        for (Colours colour: coloursList){
            Color color = colour.getColor();
            String buttonLabel = colour.toString();
            JButton colourButton = new JButton(buttonLabel);
            setButtonBorder(colourButton, color);
            buttonColour.add(colourButton);
        }
       
       // adds buttons to the JPanel
        for(JButton button: buttonColour){
            colours.add(button);
            colours.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        return colours;
    }
    
    /**
     * a method that generates a panel of buttons displaying game symbols
     */
   
    public JPanel symbolSideTiles(){

        // a list to store buttons with symbol icons
        buttonSymbols = new ArrayList<>();
        JPanel symbols = new JPanel();
        symbols.setLayout(new BoxLayout(symbols, BoxLayout.Y_AXIS));
        
        // a list to call symbol constants
        ArrayList<Symbols> symbolsList = new ArrayList<>(List.of(Symbols.values()));

        // creates button icons and adds to the buttonSymbols list
        for (Symbols symbol: symbolsList){
            String path = symbol.getPath();
            ImageIcon icon = MinigameUtils.scaledImage(path, 20);
            JButton iconButton = new JButton(icon);
            buttonSymbols.add(iconButton);
        }

       // adds buttons to the JPanel
        for(JButton button: buttonSymbols){
            symbols.add(button);
            symbols.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return symbols;
    }
    
    /**
     * a method to set a coloured border around a button
     */
     public void setButtonBorder(JButton button, Color color){
        button.setBackground(color);
        button.setForeground(color);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
    }

     /**
     * a method to set a button's colour
     */
     public void setButtonColour(JButton button, Color color){
        button.setBackground(color);
        button.setForeground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(true);
    }

     /**
     * a method that resets the player turn button colours to default red
     */
     public void resetTurnButtons(){
        setButtonColour(this.player1Turn, Color.RED);
        setButtonColour(this.player2Turn, Color.RED);
     }



    public JPanel gameLobby(){

        TitledBorder border = new TitledBorder("Game Lobby");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);

        JPanel lobby = new JPanel();
        lobby.setBorder(border);
        lobby.setLayout(new BoxLayout(lobby, BoxLayout.Y_AXIS));

        JPanel player1 = new JPanel();
        player1.setLayout(new BoxLayout(player1, BoxLayout.X_AXIS));
        JButton currentPlayer = new JButton("  > Your Turn <  ");
        setButtonColour(this.player1Turn, Color.RED);
        this.player1Turn.setPreferredSize(new Dimension(30, 6));

        player1.add(currentPlayer);
        player1.add(Box.createRigidArea(new Dimension(6, 0)));
        player1.add(this.player1Turn);


        JPanel player2 = new JPanel();
        player2.setLayout(new BoxLayout(player2, BoxLayout.X_AXIS));
        JButton opponent = new JButton("Opponent's Turn");
        setButtonColour(this.player2Turn, Color.RED);
        this.player2Turn.setPreferredSize(new Dimension(30, 6));
        player2.add(opponent);
        player2.add(Box.createRigidArea(new Dimension(5, 0)));
        player2.add(this.player2Turn);


       // lobby.add(headings);
        lobby.add(Box.createRigidArea(new Dimension(0, 10)));
        lobby.add(player1);
        lobby.add(Box.createRigidArea(new Dimension(0, 18)));
        lobby.add(player2);

        return lobby;

    }


    /**
     * Sends a command to the Telepathy game running on the server.
     * @param command: The TelepathyCommand used to specify the type of command.
     * @param attributes: Any attributes that need to be packaged with the command. 
     */
    public void sendCommand(TelepathyCommands command, String... attributes){
        JsonObject jsonCommand = TelepathyCommandHandler.makeJsonCommand(
            command, attributes
        );
        logger.info("Sending command: {}", jsonCommand);

        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(jsonCommand)));
    }

   

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        this.ticking = true;

        // Reset tiles for new game
        this.guessedTiles = new HashSet<>();
        this.eliminatedColumns = new HashSet<>();
        this.eliminatedRows = new HashSet<>();


        telepathyNotificationManager = mnClient.getDialogManager();

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(telepathyBoard);
        mnClient.getMainWindow().addWest(sidePanel);


        // Window listener to properly close game if window is closed
        mnClient.getMainWindow().getFrame().addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e){
                    sendCommand(TelepathyCommands.SYSTEMQUIT);
                }
            }
        );
       
        // Begin requesting ticks to get updates from the server
        mnClient.getAnimator().requestTick(this);
         
        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();     
    }

    /**
     * Execute commands received from the server that are not NativeCommands.
     * 
     * @param game Metadata associated with the game this client is connected to.
     * @param command The custom command stored in a JsonObject.
     */
    @Override
    public void execute(GameMetadata game, JsonObject jsonCommand) {
        this.gm = game;

        TelepathyCommands command;
        try{
            command = TelepathyCommands.valueOf(jsonCommand.getString("command"));
        } catch(IllegalArgumentException e){
            throw(new TelepathyCommandException(jsonCommand.getString("command"), "Invalid Telepathy command received from server"));
        }
        
        // Handle TelepathyCommands
        switch(command){
            case QUIT -> closeGame();
            case POPUP -> handlePopupCommand(jsonCommand);
            case GAMEOVER -> sendCommand(TelepathyCommands.QUIT);
            case BUTTONUPDATE -> updateButton(jsonCommand);
            case ELIMINATETILES -> handleNoResonse(jsonCommand);
            case PARTIALMATCH -> handleYesResponse(jsonCommand);
            default -> logger.info("{} not handled", jsonCommand);
        }
    }

    /**
     * Handle a no response from the server after asking a question. When the
     * question tile does not match the target at all, eliminate the row/column
     * on the board and the colour and symbol on the side panel.
     * 
     * @param jsonCommand: The command received with an ELIMINATETILES command.
     *  should contain X and Y coordinates and Colour/Symbol to eliminate.
     */
    private void handleNoResonse(JsonObject jsonCommand){
        ArrayList<String> attributes  = TelepathyCommandHandler.getAttributes(jsonCommand);
        
        int xElim = Integer.parseInt(attributes.get(0));
        int yElim = Integer.parseInt(attributes.get(1));

        //TODO: add colours/symbols to eliminated sets
        String cElim = attributes.get(2);
        String sElim = attributes.get(3); 

        setButtonBorder(buttonGrid[xElim][yElim], Color.RED);

        this.eliminatedColumns.add(xElim);
        this.eliminatedRows.add(yElim);
    
    }

    /**
     * Handle a yes response from the server after asking a question. If at least
     * one attribute of the guessed Tile has a match with the target tile add the
     * guessed Tile to the list and set their border to green.
     * @param jsonCommand: The command with Tile attributes of question Tile that 
     *  has passed a check.
     */
    private void handleYesResponse(JsonObject jsonCommand){
        ArrayList<String> attributes = TelepathyCommandHandler.getAttributes(jsonCommand);

        int x = Integer.parseInt(attributes.get(0));
        int y = Integer.parseInt(attributes.get(1));

        this.guessedTiles.add(
            new Tile(
                x,
                y,
                Colours.valueOf(attributes.get(2).toUpperCase()),
                Symbols.fromString(attributes.get(3))));
    
        setButtonBorder(buttonGrid[x][y], Color.GREEN);
    }

    /**
     * Take POPUP renderingCommands from the server and trigger the correct popup
     * based on attribute values.
     * @param commandPackage: The POPUP command with a popup attribute to be triggered.
     */
    private void handlePopupCommand(JsonObject commandPackage){
        ArrayList<String> popups = TelepathyCommandHandler.getAttributes(commandPackage);

        // First attribute is the identifier for popup
        if (popups.get(0).equals("welcomeMessage")) {
            activateWelcomeMessage();
            resetTurnButtons();
            this.serverState = State.INITIALISE;
        }
        
        if (popups.get(0).equals("tileSelect")) {
            activateTileSelectMessage();
            this.serverState = State.TILESELECTION;
        }
        
        if(popups.get(0).equals("gameRunning")){
            activateGameRunningMessage();
            this.serverState = State.RUNNING;
        }

        if(popups.get(0).equals("gameOver")){            
            activateGameOverMessage(popups.get(1), popups.get(2));
            this.serverState = State.GAMEOVER;
        }
    }

    /**
     * Actions that need to be taken when closing the game.
     * 
     * Sets ticking to false so that the client stops requesting game updates
     * from the server.
     */
    @Override
    public void closeGame() {
        this.ticking = false; // Stop receiving updates from server
    }
    
    /**
     * Action to take on each tick of the animator.
     * 
     * Every second, ask the server for an update of the current game
     * state.
     * @param al The animator containing the list of 'tickable' objects
     * @param now The system time at the time of this tick
     * @param delta The time delta between now and the last tick
     */
    @Override
    public void tick(Animator al, long now, long delta){
        if(now - last > 1000000000){
            sendCommand(TelepathyCommands.REQUESTUPDATE);
            last = now;
        }

        if(this.ticking) al.requestTick(this);
    }

    /**
     * Updates a button based on an UPDATEBUTTON RenderingCommand received from the server. The
     * command must contain attributes describing the button to update and changes how it should
     * be updated.
     * 
     * The button to be updated MUST be in the componentList HashMap in order to access it.
     * 
     * @param command A RenderingCommand JsonObject with a UPDATEBUTTON command value. There must also
     *  be a list of attributes specifying the button to update and what updates are to be made.
     * 
     *  e.g: ("command": UPDATEBUTTON, "attributes": ["readyButton", true]) - sets ready button to ready 
     *  state (blue).
     */
    private void updateButton(JsonObject command){
        // Get the attributes with the UPDATEBUTTON command that show what button to update and how
        ArrayList<String> attributes = TelepathyCommandHandler.getAttributes(command);
        
        // First element should be button name
        switch(attributes.get(0)){
            case "readyButton" -> {
                // TODO: This could potentially go into it's own method to make updateButton() clearer
                if(Boolean.valueOf(attributes.get(1))){
                    this.componentList.get("readyButton").setBackground(Color.BLUE);
                }else {
                    this.componentList.get("readyButton").setBackground(Color.RED);
                }
            }
            case "board" -> {
                // Alter all tiles on board
                if(attributes.get(1).equals("disableAll")){
                    disableButtonGrid();
                    if (this.serverState == State.RUNNING){
                        setButtonColour(this.player2Turn, Color.GREEN);
                        setButtonColour(this.player1Turn, Color.RED);
                    }else{
                        resetTurnButtons();
                    }
                   
                } else if(attributes.get(1).equals("enableAll")){
                    enableButtonGrid();
                    if (this.serverState == State.RUNNING){
                        setButtonColour(this.player1Turn, Color.GREEN);
                        setButtonColour(this.player2Turn, Color.RED);
                    }else{
                        resetTurnButtons();
                    }
                    
                }
            }
        }

    }
}
