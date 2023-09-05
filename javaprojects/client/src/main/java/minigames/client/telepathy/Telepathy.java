package minigames.client.telepathy;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.rendering.GameMetadata;
import minigames.telepathy.TelepathyCommandException;
import minigames.telepathy.TelepathyCommandHandler;
import minigames.telepathy.TelepathyCommands;
import minigames.commands.CommandPackage;
import minigames.client.notifications.NotificationManager;

import java.awt.*; 
import javax.swing.*; 
import javax.swing.border.*;
import java.lang.String;
import java.util.HashMap;


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

    NotificationManager telepathyNotificationManager;

    /** Your name */    
    String player;

    ActionListener buttonListener;

    JPanel telepathyBoard; // integrated game board
    JPanel gridIndexWest; // alphabetical index
    JPanel gridIndexNorth; // numerical index
    JPanel board; // game board grid
    JPanel welcomeMessage; // first popup message to start game
    JPanel confirmTargetTile; // popup panel to confirm players target tile selection
    JPanel questionOrFinalGuess; // popup panel that asks player about their button selection
    JPanel sidePanel; // board game side panel
    JPanel colourSymbolPanel; //  nested panel listing the colours and symbols in the gameboard

    ArrayList<JButton> coloursList; // list of colours used in game
    ArrayList<JButton> symbolsList; // list of symbols used in game

    
    JButton startGame; // Button to initialise game
    int buttonClicks = 0; // an int to track button clicks

    int ROWS = 9; // adjustable variable for grid size
    int COLS = 9; // adjustable variable for grid size
    int GAP = 5; // board cell padding

    String columns = "ABCDEFGHI"; // string of letters to label y coordinate
    
    JButton[][] buttonGrid; // 2D button array

    // Tick information
    private boolean ticking = true;
    private long last = System.nanoTime();

    private HashMap<String, JComponent> componentList; // Maintains references to swing elements that need to be modified 
    

    /**
     * A Telepathy board UI, a 9 x 9 2D array of Jbuttons with coordinates around the 
     * border.
     */
    public Telepathy(){
        this.componentList = new HashMap<>();
        this.buttonGrid = new JButton[COLS][ROWS];
        
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
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(20, 40));
        backButton.addActionListener(e -> {
            //TODO: Make own method
            telepathyNotificationManager.dismissCurrentNotification();
            startGame.setEnabled(true);
            int resetButtonCLicks = 0;
            buttonClicks = resetButtonCLicks;
            enableButtonGrid();
            sendCommand(TelepathyCommands.QUIT);
        });
        this.componentList.put("backButton", backButton);

        JButton readyButton = new JButton("Ready");
        readyButton.addActionListener(e -> {
            sendCommand(TelepathyCommands.TOGGLEREADY);
        });
        this.componentList.put("readyButton", readyButton);
        

        // Button to start game
       startGame = new JButton("Start a Game!");
       startGame.setPreferredSize(new Dimension(20, 40));
        startGame.addActionListener(e -> {
            activateWelcomeMessage();
            startGame.setEnabled(false);
        });
   
        // panel to display buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(readyButton);
        buttonPanel.add(backButton);
        buttonPanel.add(startGame);

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
                    if (this.buttonGrid[col][row] == selectedBtn && buttonClicks == 1) {
                        disableButtonGrid();
                        selectTargetTile(col, row); 
                    }else if (this.buttonGrid[col][row] == selectedBtn && buttonClicks >= 2){
                        disableButtonGrid();
                        activateQuestOrGuessMessage(col, row); 
                    }
                }
            }
        };
        // a nested for loop to add buttons with ActionListeners to the panel
        // UPDATE code here to add colours and symbols?? 
        for (int row = 0; row < this.buttonGrid.length; row++) {
            for (int col = 0; col < this.buttonGrid.length; col++) {
                this.buttonGrid[col][row] = new JButton();
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

        Font font = new Font("futura", Font.BOLD, 16);
        JLabel heading = new JLabel("Tile Features");
        heading.setFont(font);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
       
        colourSymbolPanel = new JPanel();
        colourSymbolPanel.setLayout(new FlowLayout());
        colourSymbolPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colourSymbolPanel.setOpaque(true);
        JPanel colours = colourSideTiles();
        JPanel symbols = symbolSideTiles();
        
        colourSymbolPanel.add(colours);
        colourSymbolPanel.add(symbols);

        sidePanel.add(heading);
        sidePanel.add(colourSymbolPanel);

        
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
                this.buttonGrid[col][row].setEnabled(true);
            }
        }

    }


    /**
     * A JPanel displaying a welcome message and information about game play.
     * @return popupWelcome JPanel 
     */
    public JPanel popupWelcomeMessage(){
    

        JPanel popupWelcome = new JPanel();
        popupWelcome.setLayout(new BoxLayout(popupWelcome, BoxLayout.PAGE_AXIS));
        popupWelcome.setPreferredSize(new Dimension(300, 300));
        
        
        Font font = new Font("futura", Font.BOLD, 20);
        Font subFont = new Font("georgia", Font.ITALIC, 18);

        JPanel headings = new JPanel();
        headings.setLayout(new BoxLayout(headings, BoxLayout.PAGE_AXIS));
        JLabel heading = new JLabel("Telepathy");
        JLabel subHeading = new JLabel("are you telepathic " + player + "? ");
        heading.setFont(font);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        subHeading.setFont(subFont);
        subHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        headings.add(heading);
        headings.add(subHeading);

        JTextPane description = new JTextPane();
        description.setText("\n\n\n     To start a game, choose your target tile!\n\n\n     Once your target tile is selected, start asking questions by clicking another tile...");
        description.setEditable(false);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton gotIt = new JButton("Got it!");
        gotIt.setPreferredSize(new Dimension(20, 40));
        gotIt.setAlignmentX(Component.CENTER_ALIGNMENT);
        gotIt.addActionListener(e -> {
            int updateButtonClicks = buttonClicks++; 
            telepathyNotificationManager.dismissCurrentNotification();
        });

        popupWelcome.add(headings);
        popupWelcome.add(subHeading);
        popupWelcome.add(description);
        popupWelcome.add(gotIt);

        return popupWelcome;
    }


    /**
     * A method to display the Welcome JPanel as a popup message.
     */
    public void activateWelcomeMessage(){
        welcomeMessage = popupWelcomeMessage();
        telepathyNotificationManager.setAnimationSpeed(8);
        telepathyNotificationManager.setAlignment(0.65f);
        telepathyNotificationManager.setDisplayTime(0);
        telepathyNotificationManager.showNotification(welcomeMessage, false);
    }

    /**
     * A method to display the questionOrGuess Jpanel as a popup message.
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     */

    public void activateQuestOrGuessMessage(int x, int y){
        questionOrFinalGuess = questionOrGuess(x, y);
        telepathyNotificationManager.setAnimationSpeed(8);
        telepathyNotificationManager.setAlignment(0.65f);
        telepathyNotificationManager.setDisplayTime(0);
        telepathyNotificationManager.showNotification(questionOrFinalGuess, false); 
    }


    /**
     * A method to display the selectTargetTile Jpanel as a popup message.
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     */
    public void selectTargetTile(int x, int y){
        confirmTargetTile = confirmTargetTile(x, y);
        telepathyNotificationManager.setAnimationSpeed(8);
        telepathyNotificationManager.setAlignment(0.65f);
        telepathyNotificationManager.setDisplayTime(0);
        telepathyNotificationManager.showNotification(confirmTargetTile, false); 
    }

    
    /**
     * A JPanel that enables the player to select their target tile.
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     * @return confirmTargetTile JPanel
     */

     public JPanel confirmTargetTile(int x, int y){

        JPanel confirmTargetTile = new JPanel();
        confirmTargetTile.setLayout(new BoxLayout(confirmTargetTile, BoxLayout.Y_AXIS));
        confirmTargetTile.setPreferredSize(new Dimension(400, 150));
        
        Font font = new Font("futura", Font.BOLD, 16);

        JLabel heading = new JLabel("Is this your chosen target tile?");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(font);

        JTextPane description = new JTextPane();
        description.setText("\n\n\n      Select 'Yes' to continue or 'No' to make another choice");
        description.setEditable(false);
       

        JPanel buttonPanel = new JPanel();
        
        String xyTargetTile = (Integer.toString(x) + ", " + Integer.toString(y));

        JButton yes = new JButton("Yes");
        yes.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS, xyTargetTile);
            setButtonBorder(this.buttonGrid[x][y], Color.BLUE);
            int updateButtonClicks = buttonClicks++;
            telepathyNotificationManager.dismissCurrentNotification();
            enableButtonGrid();
        });

        JButton no = new JButton("No");
        no.addActionListener(e -> {
            telepathyNotificationManager.dismissCurrentNotification();
            enableButtonGrid();
        });

        buttonPanel.add(yes);
        buttonPanel.add(no);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        confirmTargetTile.add(heading);
        confirmTargetTile.add(description);
        confirmTargetTile.add(buttonPanel);
        
        
        return confirmTargetTile;
    }


    /**
     * A JPanel that enables the player to ask a question or make their final guess. 
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     * @return questionOrGuess JPanel
     */

    public JPanel questionOrGuess(int x, int y){
    
        JPanel questionOrGuess = new JPanel();
        questionOrGuess.setLayout(new BoxLayout(questionOrGuess, BoxLayout.Y_AXIS));
        questionOrGuess.setPreferredSize(new Dimension(300, 290));
        
        Font font = new Font("futura", Font.BOLD, 20);

        JLabel heading = new JLabel("Question Or Final Guess?");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(font);

        JTextPane description = new JTextPane();
        description.setText("\n\n\nAre you asking if your opponent's tile shares any features with this tile?\n\n\nOr is this your Final Guess?\n\n\nChoose wisely!");
        description.setEditable(false);
       

        JPanel buttonPanel = new JPanel();
        
        String xyCoords = (Integer.toString(x) + ", " + Integer.toString(y));

        JButton question = new JButton("Question");
        question.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS, xyCoords);
            telepathyNotificationManager.dismissCurrentNotification();
            enableButtonGrid();
        });

        JButton finalGuess = new JButton("Final Guess");
        finalGuess.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS, xyCoords);
            telepathyNotificationManager.dismissCurrentNotification();
        });

        buttonPanel.add(question);
        buttonPanel.add(finalGuess);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionOrGuess.add(heading);
        questionOrGuess.add(description);
        questionOrGuess.add(buttonPanel);
      
        return questionOrGuess;
    }

    /**
     * a method that generates a panel of buttons displaying game colours
     */
   
    public JPanel colourSideTiles(){

        coloursList = new ArrayList<>();
        JPanel colours = new JPanel();
        colours.setLayout(new BoxLayout(colours, BoxLayout.Y_AXIS));
        
        JButton red = new JButton("Red");
        setButtonBorder(red, Color.RED);
        coloursList.add(red);
        JButton green = new JButton("Green");
        setButtonBorder(green, Color.GREEN);
        coloursList.add(green);
        JButton pink = new JButton("Pink");
        setButtonBorder(pink, Color.PINK);
        coloursList.add(pink);
        JButton blue = new JButton("Blue");
        setButtonBorder(blue, Color.BLUE);
        coloursList.add(blue);
        JButton grey = new JButton("Grey");
        setButtonBorder(grey, Color.GRAY);
        coloursList.add(grey);
        JButton yellow = new JButton("Yellow");
        setButtonBorder(yellow, Color.YELLOW);
        coloursList.add(yellow);
        JButton cyan = new JButton("Cyan");
        setButtonBorder(cyan, Color.CYAN);
        coloursList.add(cyan);
        JButton magenta = new JButton("Magenta");
        setButtonBorder(magenta, Color.MAGENTA);
        coloursList.add(magenta);
        JButton orange = new JButton("Orange");
        setButtonBorder(orange, Color.ORANGE);
        coloursList.add(orange);
       
        for(JButton button: coloursList){
            colours.add(button);
            colours.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return colours;
    }
    
    /**
     * a method that generates a panel of buttons displaying game symbols
     * TODO: update to tile icons
     */
   
    public JPanel symbolSideTiles(){

        symbolsList = new ArrayList<>();
        JPanel symbols = new JPanel();
        symbols.setLayout(new BoxLayout(symbols, BoxLayout.Y_AXIS));
        
        JButton hearts = new JButton("!!");
        symbolsList.add(hearts);
        JButton stars = new JButton("<3");
        symbolsList.add(stars);
        JButton diamonds = new JButton("??");
        symbolsList.add(diamonds);
        JButton spades = new JButton("*");
        symbolsList.add(spades);
        JButton clubs = new JButton("D");
        symbolsList.add(clubs);
        JButton moons = new JButton("d");
        symbolsList.add(moons);
        JButton circles = new JButton("s");
        symbolsList.add(circles);
        JButton questionMarks = new JButton("c");
        symbolsList.add(questionMarks);
        JButton exclamationMarks = new JButton("O");
        symbolsList.add(exclamationMarks);

        for(JButton button: symbolsList){
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
     * Sends a command to the Telepathy game running on the server.
     * @param command: The TelepathyCommand used to specify the type of command.
     * @param attributes: Any attributes that need to be packaged with the command. 
     */
    public void sendCommand(TelepathyCommands command, String... attributes){
        JsonObject json = new JsonObject().put("command", command.toString());
        
        if(attributes.length > 0) {json.put("attributes", new JsonArray().add(attributes));}
        logger.info("Sending command: {}", json);

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
        this.ticking = true;

        telepathyNotificationManager = new NotificationManager(mnClient);

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
            case GAMEOVER -> sendCommand(TelepathyCommands.QUIT);
            case BUTTONUPDATE -> updateButton(jsonCommand);
            default -> logger.info("{} not handled", jsonCommand);
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
     * Get a List of the attributes stored in a renderingCommand.
     * @param renderingCommand: The renderingCommand portion of a RenderingPackage 
     *      received from the server.
     * @return ArrayList of Strings containing the attributes sent in the renderingCommand. 
     
    private ArrayList<String> getAttributes(JsonObject renderingCommand){
        // Weirdness with double nested JsonArray - works now to separate attributes but could change later
        JsonArray jsonAttributes = renderingCommand.getJsonArray("attributes").getJsonArray(0);
        ArrayList<String> strAttributes = new ArrayList<>();
        for(int i = 0; i < jsonAttributes.size(); i++){
            strAttributes.add(jsonAttributes.getString(i));
        }

        return strAttributes;
    }

    */

    /**
     * Updates a button based on an UPDATEBUTTON RenderingCommand received from the server. The
     * command must contain attributes describing the button to update and changes how it should
     * be updated.
     * 
     * The button to be updated MUST be in the componentList HashMap in order to access it.
     * 
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
        }

    }
}
