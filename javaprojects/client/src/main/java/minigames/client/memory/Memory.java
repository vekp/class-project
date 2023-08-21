package minigames.client.memory;

import io.vertx.core.json.JsonObject;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * COSC220 Assessment 3
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 * Memory Card Game
 */
public class Memory implements GameClient, ActionListener, MouseListener {

    // Client instance
    MinigameNetworkClient mnClient;

    // Send commands to server
    GameMetadata gm;

    // Player
    String player;

    /** Swing components */
    JPanel mainPanel; // Main panel for the game
    JPanel gameMenuPanel; // Panel for the game menu which houses the options for the game
    JPanel commandPanel; // Panel for the game to react to user input such as "it's a pair"
    JPanel headingPanel; // Panel that houses the game and player objects
    JLabel title;
    JPanel playerPanel; // Panel for the player information
    JLabel playerName;
    JPanel gameOptionsPanel; // Houses the button objects for the game options
    JButton newGameButton;
    JButton restartLevelButton;
    JButton exitButton;
    JPanel gridContainerPanel; // Wraps the cardGridPanel
    JPanel cardGridPanel; // Sets the grid for the playing cards

    ImageIcon cardBackImage = new ImageIcon(getClass().getResource("/memory/images/playing_cards/back/card_back_black.png"));
    //ImageIcon clubs_2 = new ImageIcon(getClass().getResource("/memory/images/playing_cards/2_of_clubs.png"));
    
    // Path to card images directory (card front images only)
    String cardImagesDirectory = getClass().getResource("/memory/images/playing_cards/front/").getPath();
        
    JTextArea textArea;

    // Game variable
    boolean gameStarted = false;

    /** Initialize Swing components */
    public Memory() {
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

        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 1));
        playerPanel.setPreferredSize(new Dimension(800,30));
        //playerPanel.setBackground(Color .decode("#B2C6B2"));
        playerName = new JLabel("Player: " + player); // Placeholder text for player name
        playerName.setFont(new Font("Arial", Font.BOLD, 16));
        playerPanel.add(playerName);

        // Create the game options panel which hosts new game, restart level, and exit buttons
        gameOptionsPanel = new JPanel();
        gameOptionsPanel.setLayout(new GridLayout(1, 3));
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartLevelButton = new JButton("Restart Level");
        restartLevelButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(e -> mnClient.runMainMenuSequence());
        gameOptionsPanel.add(newGameButton);
        gameOptionsPanel.add(restartLevelButton);
        gameOptionsPanel.add(exitButton);

        gameMenuPanel.add(playerPanel);
        gameMenuPanel.add(gameOptionsPanel);

        // Create a list of card images
        List<ImageIcon> cardImages = new ArrayList<>();
        File cardImageDirectory = new File(cardImagesDirectory);
        File[] cardImageFiles = cardImageDirectory.listFiles();

        // Add all card images available
        if (cardImageFiles != null) {
            for (File file: cardImageFiles) {
                if (file.isFile()) {
                    cardImages.add(new ImageIcon(file.getAbsolutePath()));
                }
            }
        }

        // Create a list of card pairs
        // TODO: Look into the possible error for Random - 'out of bound' - if it occurs again!
        List<ImageIcon> cardPairs = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(cardImages.size());
            ImageIcon cardImage = cardImages.get(randomIndex);
            cardPairs.add(cardImage);
            cardPairs.add(cardImage); // Add a duplicate for each card
            cardImages.remove(randomIndex); // Then remove selected card
        }


        // Shuffle the card pairs - FIXME: Use the shuffling framework/system here!
        Collections.shuffle(cardPairs);


        // Create card grid and add cards with the card images and "flip" buttons
        cardGridPanel = new JPanel();
        cardGridPanel.setLayout(new GridLayout(4, 4, 0, 0)); // 4x4 grid for placeholders  
        
        int cardImageIndex = 0;
       
        for (int i = 0; i < 16; i++) {
            final JPanel cards; 
            final String FLIP = "Flip";

            JPanel cardBack = new JPanel();
            cardBack.add(new JLabel(resizeImageIcon(cardBackImage,50,-1)));

            JPanel cardFront = new JPanel();
            //cardFront.add(new JLabel(resizeImageIcon(clubs_2,50,-1))); // need to make this change to the random card - DONE
            
            // Get the next card image from cardPairs
            ImageIcon image = cardPairs.get(cardImageIndex);
            // Define and append to each section (16 total)
            JLabel cardLabel = new JLabel(resizeImageIcon(image, 50, -1));
            cardFront.add(cardLabel);
            // Increment the cardImageIndex and wrap around if needed
            cardImageIndex = (cardImageIndex + 1) % cardPairs.size();

            cards = new JPanel(new CardLayout());
            cards.add(cardBack);
            cards.add(cardFront);

            class ControlActionListener implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    CardLayout cl = (CardLayout) (cards.getLayout());
                    String cmd = e.getActionCommand();
                    if (cmd.equals(FLIP)) {
                        cl.next(cards);
                    }
                }
            }
            ControlActionListener cal = new ControlActionListener();

            JButton flipButton = new JButton("Flip");
            flipButton.setActionCommand(FLIP);
            flipButton.addActionListener(cal);

            JPanel cardPane = new JPanel();
            cardPane.setLayout(new BoxLayout(cardPane, BoxLayout.Y_AXIS));
            cardPane.add(cards);
            cardPane.add(flipButton);

            cardGridPanel.add(cardPane);
        }

        // Container for the card grid
        gridContainerPanel = new JPanel(new BorderLayout());
        gridContainerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2,true)); // Set border around player grid to separate the UI elements
        gridContainerPanel.setPreferredSize(new Dimension(750, 450));
        gridContainerPanel.add(cardGridPanel, BorderLayout.CENTER); // Add the cardGridPanel to the container

        // Create the command panel
        commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.setBorder((new EmptyBorder(15,10,0,10)));
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.BOLD, 16));
        commandPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMenuPanel, BorderLayout.NORTH);
        mainPanel.add(gridContainerPanel, BorderLayout.CENTER);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);


        for (Component c:  new Component[] {title, headingPanel, playerName, newGameButton, restartLevelButton, exitButton,
                cardGridPanel, commandPanel, textArea, gameMenuPanel, playerPanel, gridContainerPanel, mainPanel})
            //c.setBackground(Color.decode("#B2C6B2"));

        // Add ActionListeners to the buttons
        newGameButton.addActionListener(this);
        restartLevelButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Add ActionListeners to the card grid
        for (Component c : cardGridPanel.getComponents()) {
            c.addMouseListener(this);
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

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Add our components to the north, south, east, west, or centre of the main window's BorderLayout
        mnClient.getMainWindow().addCenter(mainPanel);  

        textArea.append("Starting...");

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
            case "clearText" -> textArea.setText("");
            case "appendText" -> textArea.setText(textArea.getText() + command.getString("text"));
        }
    }


    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
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

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {

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
    public void closeGame() {
        // Nothing to do        
    }

}


