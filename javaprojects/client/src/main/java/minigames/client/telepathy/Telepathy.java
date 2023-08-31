package minigames.client.telepathy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.telepathy.TelepathyCommands;
import minigames.commands.CommandPackage;
import minigames.client.notifications.NotificationManager;

import java.awt.*; // not the best coding practice - to update
import javax.swing.*; // not the best coding practice - to update
import java.lang.String;


/**
 * A Telepathy GameClient. Handles displaying information of a Telepathy game running on the
 * server to the player.
 * 
 * Implements Tickable to animate and keep the client up to date with the current state of the 
 * game running on the server.
 */
public class Telepathy implements GameClient, Tickable{

   
    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    NotificationManager telepathyNotificationManager;

    /** Your name */    
    String player;

    JPanel telepathyBoard; // integrated game board
    JPanel gridIndexWest; // alphabetical index
    JPanel gridIndexNorth; // numerical index
    JPanel board; // game board grid
    JPanel welcomeMessage; // first popup message to start game
    JPanel confirmTargetTile; // popup panel to confirm players target tile selection
    JPanel questionOrFinalGuess; // popup panel that asks player about their button selection
    

    int buttonClicks = 0; // an int to track button clicks

    int ROWS = 9; // adjustable variable for grid size
    int COLS = 9; // adjustable variable for grid size
    int GAP = 5; // board cell padding

    String columns = "ABCDEFGHI"; // string of letters to label y coordinate
    
    JButton[][] buttonGrid = new JButton[ROWS][COLS]; // 2D button array

    // Tick information
    private boolean ticking = true;
    private long last = System.nanoTime();
    

    /**
     * A Telepathy board UI, a 9 x 9 2D array of Jbuttons with coordinates around the 
     * border.
     */
    public Telepathy(){

        telepathyBoard = new JPanel();
        telepathyBoard.setLayout(new BorderLayout());

        // creates a panel using the GridLayout manager to label y coordinates alphabetically
        gridIndexWest = new JPanel();
        gridIndexWest.setBorder(new EmptyBorder(0, 30, 0, 0));
        gridIndexWest.setLayout(new GridLayout(ROWS, 1, GAP, GAP));
        for (int i = 0; i < buttonGrid.length; i++) {
            //FIXME need to implement better code to center north index numbers
            gridIndexWest.add(new JLabel(Integer.toString(i + 1)));
        }

        //creates a panel using the GridLayout manager to label x coordinates numerically
        gridIndexNorth = new JPanel();
        gridIndexNorth.setBorder(new EmptyBorder(30, 0, 0, 0));
        gridIndexNorth.setLayout(new FlowLayout());
        //gridIndexNorth.setLayout(new GridLayout(1, COLS, GAP, GAP));
        for (int i = 0; i < buttonGrid.length; i++) {
            //gridIndexNorth.add(new JLabel("        " + columns.substring(i, i + 1), SwingConstants.CENTER));
            gridIndexNorth.add(new JLabel("        " + columns.substring(i, i + 1) + "   "));
       
        }

        //a panel for the 2D button array
        board = new JPanel();
        board.setLayout(new GridLayout(ROWS, COLS, GAP, GAP));
        board.setMaximumSize(new Dimension(350, 350));
        board.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Button to go back to the main menu
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(20, 40));
        backButton.addActionListener(e -> {
            telepathyNotificationManager.dismissCurrentNotification();
            int resetButtonCLicks = 0;
            buttonClicks = resetButtonCLicks;
            sendCommand(TelepathyCommands.QUIT.toString());
        });

        // Temporary button to start the game...would be better for the Welcome Message to appear on Telepathy startup
       JButton startGame = new JButton("Start a Game!");
       startGame.setPreferredSize(new Dimension(20, 40));
        startGame.addActionListener(e -> {
            activateWelcomeMessage();
            startGame.setEnabled(false);
        });
   
        // panel to display buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(backButton);
        buttonPanel.add(startGame);
       
        

       
        ActionListener buttonListener = evt -> {
            // on click event: iterates through the button array
            /* compares the grid button with the get(Source) JButton and displays a pop-up 
            message enabling players to finalise their selection */
            
            JButton selectedBtn = (JButton) evt.getSource();
            for (int row = 0; row < buttonGrid.length; row++) {
                for (int col = 0; col < buttonGrid[row].length; col++) {
                    if (buttonGrid[row][col] == selectedBtn && buttonClicks == 1) {
                        selectTargetTile(col, row); 
                    }else if (buttonGrid[row][col] == selectedBtn && buttonClicks >= 2){
                        activateQuestOrGuessMessage(col, row); 
                    }
                }
            }
        };
        // a nested for loop to add buttons with ActionListeners to the panel
        // UPDATE code here to add colours and symbols??
        for (int row = 0; row < buttonGrid.length; row++) {
            for (int col = 0; col < buttonGrid.length; col++) {
                buttonGrid[row][col] = new JButton();
                buttonGrid[row][col].setPreferredSize(new Dimension(50, 50));
                buttonGrid[row][col].addActionListener(buttonListener);
                board.add(buttonGrid[row][col]);
            }
        }

        // added components to the telepathyBoard panel
       
        telepathyBoard.add(buttonPanel, BorderLayout.SOUTH); //temporary panel
        telepathyBoard.add(gridIndexWest, BorderLayout.WEST, SwingConstants. CENTER);
        telepathyBoard.add(gridIndexNorth, BorderLayout.NORTH, SwingConstants.CENTER);
        telepathyBoard.add(board, BorderLayout.CENTER);

        
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
        subHeading.setFont(subFont);

        headings.add(heading, heading.CENTER_ALIGNMENT);
        headings.add(subHeading, subHeading.CENTER_ALIGNMENT);

        JTextPane description = new JTextPane();
        description.setText("\n\n\nTo start a game, choose your target tile!\n\n\nOnce your target tile is selected, start asking questions by clicking another tile...");
        description.setEditable(false);

        JButton gotIt = new JButton("Got it!");
        gotIt.setPreferredSize(new Dimension(20, 40));
        gotIt.addActionListener(e -> {
            int updateButtonClicks = buttonClicks++; 
            telepathyNotificationManager.dismissCurrentNotification();
        });


        popupWelcome.add(headings, headings.CENTER_ALIGNMENT);
        popupWelcome.add(subHeading, subHeading.CENTER_ALIGNMENT);
        popupWelcome.add(description, description.CENTER_ALIGNMENT);
        popupWelcome.add(gotIt, gotIt.CENTER_ALIGNMENT);

        

        return popupWelcome;
    }


    /**
     * A method to display the popupWelcome Jpanel as a popup message.
     */
    public void activateWelcomeMessage(){
        welcomeMessage = popupWelcomeMessage();
        telepathyNotificationManager.setAnimationSpeed(8);
        telepathyNotificationManager.setAlignment(0.5f);
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
        telepathyNotificationManager.setAlignment(0.5f);
        telepathyNotificationManager.showNotification(questionOrFinalGuess); 
    }


    /**
     * A method to display the selectTargetTile Jpanel as a popup message.
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     */
    public void selectTargetTile(int x, int y){
        confirmTargetTile = confirmTargetTile(x, y);
        telepathyNotificationManager.setAnimationSpeed(8);
        telepathyNotificationManager.setAlignment(0.5f);
        telepathyNotificationManager.showNotification(confirmTargetTile); 
    }

    
    /**
     * A JPanel that enables the player to select their target tile.
     * @param int x representing the x coordinate of the selected tile
     * @param int y represetning the y coordinate of the selected tile
     * @return confirmTargetTile JPanel
     */

     public JPanel confirmTargetTile(int x, int y){
    
        JPanel confirmTargetTile = new JPanel();
        confirmTargetTile.setLayout(new BorderLayout());
        confirmTargetTile.setPreferredSize(new Dimension(400, 150));
        
        Font font = new Font("futura", Font.BOLD, 16);

        JLabel heading = new JLabel("Is this your chosen target tile?");
        heading.setHorizontalAlignment(JLabel.CENTER);
        heading.setFont(font);

        JTextPane description = new JTextPane();
        description.setText("\n\n\nSelect 'Yes' to continue or 'No' to make another choice");
        description.setEditable(false);
       

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        String xyTargetTile = (Integer.toString(x) + ", " + Integer.toString(y));

        JButton yes = new JButton("Yes");
        yes.setPreferredSize(new Dimension(20, 40));
        yes.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS.toString(), xyTargetTile);
            int updateButtonClicks = buttonClicks++;
            telepathyNotificationManager.dismissCurrentNotification();
        });

        JButton no = new JButton("No");
        no.setPreferredSize(new Dimension(20, 40));
        no.addActionListener(e -> {
            telepathyNotificationManager.dismissCurrentNotification();
        });

        buttonPanel.add(yes);
        buttonPanel.add(no);

        confirmTargetTile.add(heading, BorderLayout.NORTH);
        confirmTargetTile.add(description, BorderLayout.CENTER);
        confirmTargetTile.add(buttonPanel, BorderLayout.SOUTH);
        
        
      
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
        questionOrGuess.setLayout(new BorderLayout());
        questionOrGuess.setPreferredSize(new Dimension(300, 290));
        
        Font font = new Font("futura", Font.BOLD, 20);

        JLabel heading = new JLabel("Question Or Final Guess?");
        heading.setHorizontalAlignment(JLabel.CENTER);
        heading.setFont(font);

        JTextPane description = new JTextPane();
        description.setText("\n\n\nAre you asking if your opponent's tile shares any features with this tile?\n\n\nOr is this your Final Guess?\n\n\nChoose wisely!");
        description.setEditable(false);
       

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        String xyCoords = (Integer.toString(x) + ", " + Integer.toString(y));

        JButton question = new JButton("Question");
        question.setPreferredSize(new Dimension(20, 40));
        question.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS.toString(), xyCoords);
            telepathyNotificationManager.dismissCurrentNotification();
        });

        JButton finalGuess = new JButton("Final Guess");
        finalGuess.setPreferredSize(new Dimension(20, 40));
        finalGuess.addActionListener(e -> {
            sendCommand(TelepathyCommands.BUTTONPRESS.toString(), xyCoords);
            telepathyNotificationManager.dismissCurrentNotification();
        });

        buttonPanel.add(question);
        buttonPanel.add(finalGuess);

        questionOrGuess.add(heading, BorderLayout.NORTH);
        questionOrGuess.add(description, BorderLayout.CENTER);
        questionOrGuess.add(buttonPanel, BorderLayout.SOUTH);
      
        return questionOrGuess;
    }



    /**
     * Sends a command to the Telepathy game running on the server.
     * @param command: The TelepathyCommand used to specify the type of command.
     * @param attributes: Any attributes that need to be packaged with the command. 
     */
    public void sendCommand(String command, String... attributes){
        JsonObject json = new JsonObject().put("command", command);
        
        if(attributes.length > 0) {json.put("attributes", new JsonArray().add(attributes));}

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

        // Activate popup welcome message when window is opened
        //FIXME attempted code to activate welcome message on start up
        /*
        mnClient.getMainWindow().getFrame().addWindowListener(
            new WindowAdapter(){
                @Override
                public void windowOpened(WindowEvent e){
                    activateWelcomeMessage();
                }
            }
        );
        */

        // Window listener to properly close game if window is closed
        mnClient.getMainWindow().getFrame().addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e){
                    sendCommand(TelepathyCommands.SYSTEMQUIT.toString());
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

        // TODO handle TelepathyCommands from the server
        TelepathyCommands command = TelepathyCommands.valueOf(jsonCommand.getString("command"));
        switch(command){
            case QUIT -> closeGame();
            default -> {}
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
            sendCommand(TelepathyCommands.REQUESTUPDATE.toString());
            last = now;
        }

        if(this.ticking) al.requestTick(this);
    }
}
