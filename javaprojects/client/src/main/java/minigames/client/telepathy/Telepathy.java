package minigames.client.telepathy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

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

import java.awt.*; // not the best coding practice - to update
import javax.swing.*; // not the best coding practice - to update
import java.lang.String;


 
public class Telepathy implements GameClient, Tickable {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */    
    String player;

    JPanel telepathyBoard; // integrated game board
    JPanel gridIndexWest; // alphabetical index
    JPanel gridIndexNorth; // numerical index
    JPanel board; // game board grid

    int ROWS = 9; // adjustable variable for grid size
    int COLS = 9; // adjustable variable for grid size
    int GAP = 5; // board cell padding

    String columns = "ABCDEFGHI"; // string of letters to label y coordinate
    
    JButton[][] buttonGrid = new JButton[ROWS][COLS]; // 2D button array

    // Tick information
    private boolean ticking = true;
    private long last = System.nanoTime();
    


    /**
     * the beginings of a Telepathy board UI. A 9 x 9 2D array of Jbuttons with coordinates around the 
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
            gridIndexWest.add(new JLabel(columns.substring(i, i + 1)));
        }

        //creates a panel using the GridLayout manager to label x coordinates numerically
        gridIndexNorth = new JPanel();
        gridIndexNorth.setBorder(new EmptyBorder(30, 0, 0, 0));
        gridIndexNorth.setLayout(new GridLayout(1, COLS, GAP, GAP));
        for (int i = 0; i < buttonGrid.length; i++) {
            //FIXME need to implement better code to center north index numbers
            gridIndexNorth.add(new JLabel("    " + (i + 1), SwingConstants.CENTER));
        }


        //a panel for the 2D button array
        board = new JPanel();
        board.setLayout(new GridLayout(ROWS, COLS, GAP, GAP));
        board.setMaximumSize(new Dimension(350, 350));
        board.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            sendCommand(TelepathyCommands.QUIT.toString());
        });
    
        //temporary panel to display xy button coordinates
        JPanel gridCheck = new JPanel();
        gridCheck.setLayout(new BoxLayout(gridCheck, BoxLayout.X_AXIS));
        JLabel label = new JLabel("xy button coordinates: (y, x)");
        JTextField xyCheck = new JTextField(4);
        gridCheck.add(label);
        gridCheck.add(xyCheck);
        gridCheck.add(backButton);
        

        //FIXME: the action listener accesses coordinates: but no current functionality
        //also need to implement switch code somewhere - i.e. 0,0 == A,1; 0,1 == A,2 etc
        ActionListener buttonListener = evt -> {
            // on click event: iterates through the button array
            // compares the grid button with the get(Source) JButton
            JButton selectedBtn = (JButton) evt.getSource();
            for (int row = 0; row < buttonGrid.length; row++) {
                for (int col = 0; col < buttonGrid[row].length; col++) {
                    if (buttonGrid[row][col] == selectedBtn) {
                        String buttonText = (Integer.toString(row) + ", " + Integer.toString(col));
                        xyCheck.setText(Integer.toString(row) + ", " + Integer.toString(col));
                        sendCommand(TelepathyCommands.BUTTONPRESS.toString(), buttonText);
                    }
                }
            }
        };
        // a nested for loop to add buttons with ActionListeners to the panel
        for (int row = 0; row < buttonGrid.length; row++) {
            for (int col = 0; col < buttonGrid.length; col++) {
                buttonGrid[row][col] = new JButton();
                buttonGrid[row][col].setPreferredSize(new Dimension(50, 50));
                buttonGrid[row][col].addActionListener(buttonListener);
                board.add(buttonGrid[row][col]);
            }
        }

        // added components to the telepathyBoard panel
       
        telepathyBoard.add(gridCheck, BorderLayout.SOUTH); //temporary panel
        telepathyBoard.add(gridIndexWest, BorderLayout.WEST, SwingConstants. CENTER);
        telepathyBoard.add(gridIndexNorth, BorderLayout.NORTH, SwingConstants.CENTER);
        telepathyBoard.add(board, BorderLayout.CENTER);

        
    }

    
    /** COMMENTED OUT - Deprecated by sendCommand(String, String...)
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our gameserver will interpret.
     * We're sending these as 
     * { "command": command }
     
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }
    */

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

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(telepathyBoard);

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
     * Every 5 seconds, ask the server for an update of the current game
     * state.
     * @param al The animator containing the list of 'tickable' objects
     * @param now The system time at the time of this tick
     * @param delta The time delta between now and the last tick
     */
    @Override
    public void tick(Animator al, long now, long delta){
        if(now - last > 1000000000){
            sendCommand(TelepathyCommands.UPDATECLIENT.toString());
            last = now;
        }

        if(this.ticking) al.requestTick(this);
    }
}
