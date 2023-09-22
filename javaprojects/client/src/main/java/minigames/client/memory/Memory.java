package minigames.client.memory;

import io.vertx.core.json.*;

import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.common.memory.DeckOfCards.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;

/**
 * COSC220 Assessment 3
 * 
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 *         Memory Card Game
 */
public class Memory implements GameClient, ActionListener {

    // Client instance
    MinigameNetworkClient mnClient;

    // Send commands to server
    GameMetadata gm;

    // Player
    String player;

    // Deck of playing cards
    PlayingCard[] deck;

    /** Swing components and variables used in the GUI*/
    private JFrame MemoryWindow;
    private JPanel GUI, mainPanel, gameMenuPanel, headingPanel, playerPanel, gameOptionsPanel, buttonPanel;
    private JLabel title, playerName, matches, stopwatch;
    private JButton newGameButton, restartLevelButton, exitButton, achievementsButton;
    private JButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18;
    private Border margin;
    private int rows = 3, columns = 6, btnContainerWidth, btnContainerHeight;
    private ImageIcon cardBackImage = new ImageIcon(getClass().getResource("/memory/images/playing_cards/back/card_back_black.png"));
    private ImageIcon[] cardFrontImages = new ImageIcon[18];

    // Game variables
    private boolean gameStarted = false, newGame, exiting;
    private int matchesCounter = 0;
    private int[] timeElapsed = { 1, 30 }; // {mins, seconds}
    private Timer timer;


    /** Initialize Swing components */
    public Memory() {
        GUI = new JPanel();
        MemoryGUI();
        MemoryWindow = new JFrame("Memory");
        MemoryWindow.setSize(800, 800);
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

    private void setMainPanelDisplay() {
        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMenuPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private void setGameMenuPanelDisplay() {
        // Create the heading panel
        headingPanel = new JPanel();
        headingPanel.setLayout(new GridLayout(1, 1));
        headingPanel.setBorder(new EmptyBorder(0, 0, 1, 0));
        title = new JLabel("Pair Up - A Memory Card Game");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        title.setHorizontalAlignment(JLabel.CENTER);
        headingPanel.add(title);

        // Create the game menu panel
        gameMenuPanel = new JPanel();
        gameMenuPanel.setLayout(new GridLayout(3, 0));
        gameMenuPanel.setBorder((new EmptyBorder(10, 10, 5, 10)));
        gameMenuPanel.add(headingPanel); // Add heading panel to the game menu panel
        gameMenuPanel.add(playerPanel); // Add playerPanel to gameMenuPanel
        gameMenuPanel.add(gameOptionsPanel); // Add gameOptionsPanel to gameMenuPanel
    }

    private void setPlayerPanelDisplay() {
        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 4));
        playerPanel.setPreferredSize(new Dimension(800, 30));

        playerName = new JLabel("Player: " + player);
        playerName.setFont(new Font("Arial", Font.BOLD, 14));
        playerName.setHorizontalAlignment(JLabel.CENTER);

        matches = new JLabel("Pairs Matched: " + matchesCounter + "/9");
        matches.setFont(new Font("Arial", Font.BOLD, 14));
        matches.setHorizontalAlignment(JLabel.CENTER);

        stopwatch = new JLabel(String.format("Time: %02d:%02d", timeElapsed[0], timeElapsed[1])); // Text for Timer
        stopwatch.setFont(new Font("Arial", Font.BOLD, 14));
        stopwatch.setHorizontalAlignment(JLabel.CENTER);

        // Add the player information to the player panel
        playerPanel.add(playerName);
        playerPanel.add(matches);
        playerPanel.add(stopwatch);
    }

    private void setGameOptionsPanelDisplay() {
        // Create game options panel which hosts achievements, restart level, and exit buttons
        gameOptionsPanel = new JPanel();
        gameOptionsPanel.setLayout(new GridLayout(1, 5));

        // Create achievements button that talks to the API
        achievementsButton = new JButton("Achievements");
        achievementsButton.setFont(new Font("Arial", Font.BOLD, 16));
        achievementsButton.addActionListener(e -> mnClient.getGameAchievements(player, gm.gameServer()));

        // Restart level button
        restartLevelButton = new JButton("Restart Level");
        restartLevelButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Exit button
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Add game options to gameOptionsPanel
        gameOptionsPanel.add(achievementsButton);
        gameOptionsPanel.add(restartLevelButton);
        gameOptionsPanel.add(exitButton);
    }

    public void setButtonPanelDisplay() {
        btnContainerWidth = 470;
        btnContainerHeight = 435;

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, columns, 0, 0));
        buttonPanel.setMaximumSize(new Dimension(btnContainerWidth, btnContainerHeight));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ImageIcon image = resizeImageIcon(cardBackImage, -1, btnContainerHeight / rows - 5);

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

        btn1.addActionListener((evt) -> {
            sendCommand("Flip_Card_1");
            checkIfTimerStarted();
        });
        btn2.addActionListener((evt) -> {
            sendCommand("Flip_Card_2");
            checkIfTimerStarted();
        });
        btn3.addActionListener((evt) -> {
            sendCommand("Flip_Card_3");
            checkIfTimerStarted();
        });
        btn4.addActionListener((evt) -> {
            sendCommand("Flip_Card_4");
            checkIfTimerStarted();

        });
        btn5.addActionListener((evt) -> {
            sendCommand("Flip_Card_5");
            checkIfTimerStarted();
        });
        btn6.addActionListener((evt) -> {
            sendCommand("Flip_Card_6");
            checkIfTimerStarted();
        });
        btn7.addActionListener((evt) -> {
            sendCommand("Flip_Card_7");
            checkIfTimerStarted();
        });
        btn8.addActionListener((evt) -> {
            sendCommand("Flip_Card_8");
            checkIfTimerStarted();
        });
        btn9.addActionListener((evt) -> {
            sendCommand("Flip_Card_9");
            checkIfTimerStarted();
        });
        btn10.addActionListener((evt) -> {
            sendCommand("Flip_Card_10");
            checkIfTimerStarted();
        });
        btn11.addActionListener((evt) -> {
            sendCommand("Flip_Card_11");
            checkIfTimerStarted();
        });
        btn12.addActionListener((evt) -> {
            sendCommand("Flip_Card_12");
            checkIfTimerStarted();
        });
        btn13.addActionListener((evt) -> {
            sendCommand("Flip_Card_13");
            checkIfTimerStarted();
        });
        btn14.addActionListener((evt) -> {
            sendCommand("Flip_Card_14");
            checkIfTimerStarted();
        });
        btn15.addActionListener((evt) -> {
            sendCommand("Flip_Card_15");
            checkIfTimerStarted();
        });
        btn16.addActionListener((evt) -> {
            sendCommand("Flip_Card_16");
            checkIfTimerStarted();
        });
        btn17.addActionListener((evt) -> {
            sendCommand("Flip_Card_17");
            checkIfTimerStarted();
        });
        btn18.addActionListener((evt) -> {
            sendCommand("Flip_Card_18");
            checkIfTimerStarted();
        });

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
     * A method to resize the ImageIcon's image and return the resized ImageIcon
     * 
     * @param originalImageIcon The original ImageIcon to be resized
     * @param targetWidth       Required width of the resized ImageIcon (cannot be
     *                          0. If value is negative, then a value is
     *                          substituted to maintain the aspect ratio of the
     *                          original image dimensions)
     * @param targetHeight      Required height of the resized ImageIcon (cannot be
     *                          0. If value is negative, then a value is
     *                          * substituted to maintain the aspect ratio of the
     *                          original image dimensions)
     * @return Resized ImageIcon
     */
    public ImageIcon resizeImageIcon(ImageIcon originalImageIcon, int targetWidth, int targetHeight) {
        Image resizedImage = originalImageIcon.getImage().getScaledInstance(targetWidth, targetHeight,
                Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    // ActionListener for button press
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
    }

    // TimerListener implementing ActionListener - Define stopwatch timer countdown & call method to update stopwatch
    public class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timeElapsed[1] == 0) {
                if (timeElapsed[0] == 0) {
                    ((Timer) e.getSource()).stop();
                    stopwatch.setText("Game Over!");
                    JOptionPane.showMessageDialog(null, "Game Over!");
                } else {
                    timeElapsed[0]--;
                    timeElapsed[1] = 59;
                }
            } else {
                timeElapsed[1]--;
            }
            if (matchesCounter == 9) {
                timer.stop();
                JOptionPane.showMessageDialog(null, String.format("You have matched all the pairs!\nFinal Score: %d/9 :D\nPress 'OK' to return to the main game menu.", matchesCounter), "Return to Main Menu", JOptionPane.INFORMATION_MESSAGE);
                resetTimer();
                resetScore();
                matchesCounter = 0;
                gameStarted = false;
                sendCommand("resetToCardBacks");
                closeGame();
                mnClient.runMainMenuSequence();
            }
            updateStopwatch();
        }
    }

    // Check if Timer has been started. Start Timer if not already running
    private void checkIfTimerStarted(){
        if (!gameStarted) {
            startTimer();
        }
    }

    // Method to update stopwatch
    private void updateStopwatch() {
        stopwatch.setText(String.format("Time: %02d:%02d", timeElapsed[0], timeElapsed[1]));
    }

    // Method to reset stopwatch timer back to original time
    public void resetTimer() {
        timeElapsed[0] = 1; // Set minutes to 1
        timeElapsed[1] = 30; // Set seconds to 30
        updateStopwatch(); // Update the display
    }

    // Reset score (pairs matched) back to 0/9
    private void resetScore() {
        matches.setText("Pairs Matched: 0/9");
    }

    // Method to start the stopwatch timer
    public void startTimer() {
        timer = new Timer(1000, new TimerListener());
        timer.start();
        gameStarted = true;
    }

    // Update score (pairs matched) depending on current game progress
    public void updateScore() {
        matchesCounter++;
        matches.setText(String.format("Pairs Matched: %d/9", matchesCounter));
    }

    // Return boolean array for solved cards
    public boolean[] parseBooleanArray(JsonArray array){
        boolean[] solvedCards = new boolean[18];
        for(int i = 0; i < array.size(); i++){
            if(array.getString(i) == "true"){
                solvedCards[i] = true;
            }
        }
        return solvedCards;
    }

    // Reset card ImageIcon to card back image
    public void resetCards(boolean[] solvedCards) {
        JButton[] cardButtons = {btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15, btn16,
        btn17, btn18};
        ImageIcon image = resizeImageIcon(cardBackImage, -1, btnContainerHeight / rows - 5);
        for(int i = 0; i < cardButtons.length; i++){
            if(solvedCards[i] == false){
                cardButtons[i].setIcon(image);
            }
        }
    }

    // Card names to string
    public String parseCardString(String cardString) {
        StringBuilder cardName = new StringBuilder();
        String[] card = new String[2];
        String[] cardAsArray = cardString.replaceAll(" ", "").replace("{", "").replace("}", "").split(",");

        for (int i = 0; i < cardAsArray.length - 1; i++) {
            String[] cardElements = cardAsArray[i].split("=");
            card[i] = cardElements[1];
        }
        cardName.append("_").append(card[1]).append("_of_").append(card[0]).append(".png");

        return cardName.toString();
    }

     // Run
     public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Memory();
            }
        });
    }

    /**
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our
     * gameserver will interpret.
     * We're sending these as
     * { "command": command }
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    // Send command method that takes a JsonObject as input
    public void sendCommand(JsonObject command) {
        if (exiting)
            return;
        if (newGame)
            return;
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(command)));
    }

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        // Update Player Name
        playerName.setText("Player: " + player);

        // Restart level button + ActionListener to include changes/updates on click
        restartLevelButton.addActionListener(e -> {
            timer.stop();
            resetTimer();
            resetScore();
            sendCommand("resetToCardBacks");
            gameStarted = false;
        });

        // Exit button + ActionListener to include changes/updates on click
        exitButton.addActionListener(e -> {
            if (gameStarted == true) {
                timer.stop();
            }
            resetTimer();
            resetScore();
            sendCommand("resetToCardBacks");
            gameStarted = false;
            closeGame();
            mnClient.runMainMenuSequence();
        });

        // Add our components to the north, south, east, west, or centre of the main
        // window's BorderLayout
        mnClient.getMainWindow().addCenter(GUI);

        // Don't forget to call pack - it triggers the window to resize and repaint
        // itself
        mnClient.getMainWindow().pack();
    }

    // execute rendering packages received from the server
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't need to say "break;" at the end of our cases)

        switch (command.getString("command")) {

            case "deckOfCards" -> {
                // Get the data about what cards exist on the server and use that to populate
                // our card images.
                // Not sure what to do with this data yet, maybe just an array of strings with
                // the suit and value info
                // and we use that to get the image for each button. idk
                JsonArray cardsArray = command.getJsonArray("cards");
                for (int i = 0; i < cardsArray.size(); i++) {
                    StringBuilder imagePath = new StringBuilder();
                    imagePath.append("/memory/images/playing_cards/front/")
                            .append(parseCardString(cardsArray.getString(i)));
                    cardFrontImages[i] = new ImageIcon(getClass().getResource(imagePath.toString()));
                }
            }
            case "updateScore" -> {
                updateScore();
                System.out.println("You have earned a point!");
            }
            case "Flip_Card_1" -> {
                btn1.setIcon(resizeImageIcon(cardFrontImages[0], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_2" -> {
                btn2.setIcon(resizeImageIcon(cardFrontImages[1], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_3" -> {
                btn3.setIcon(resizeImageIcon(cardFrontImages[2], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_4" -> {
                btn4.setIcon(resizeImageIcon(cardFrontImages[3], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_5" -> {
                btn5.setIcon(resizeImageIcon(cardFrontImages[4], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_6" -> {
                btn6.setIcon(resizeImageIcon(cardFrontImages[5], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_7" -> {
                btn7.setIcon(resizeImageIcon(cardFrontImages[6], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_8" -> {
                btn8.setIcon(resizeImageIcon(cardFrontImages[7], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_9" -> {
                btn9.setIcon(resizeImageIcon(cardFrontImages[8], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_10" -> {
                btn10.setIcon(resizeImageIcon(cardFrontImages[9], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_11" -> {
                btn11.setIcon(resizeImageIcon(cardFrontImages[10], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_12" -> {
                btn12.setIcon(resizeImageIcon(cardFrontImages[11], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_13" -> {
                btn13.setIcon(resizeImageIcon(cardFrontImages[12], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_14" -> {
                btn14.setIcon(resizeImageIcon(cardFrontImages[13], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_15" -> {
                btn15.setIcon(resizeImageIcon(cardFrontImages[14], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_16" -> {
                btn16.setIcon(resizeImageIcon(cardFrontImages[15], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_17" -> {
                btn17.setIcon(resizeImageIcon(cardFrontImages[16], -1, btnContainerHeight / rows - 5));
            }
            case "Flip_Card_18" -> {
                btn18.setIcon(resizeImageIcon(cardFrontImages[17], -1, btnContainerHeight / rows - 5));
            }
            case "resetCards" -> {
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                boolean[] solvedCards = parseBooleanArray(command.getJsonArray("solved"));
                resetCards(solvedCards);
            }
            case "resetToCardBacks" -> {
                ImageIcon image = resizeImageIcon(cardBackImage, -1, btnContainerHeight / rows - 5);
                btn1.setIcon(image);
                btn2.setIcon(image);
                btn3.setIcon(image);
                btn4.setIcon(image);
                btn5.setIcon(image);
                btn6.setIcon(image);
                btn7.setIcon(image);
                btn8.setIcon(image);
                btn9.setIcon(image);
                btn10.setIcon(image);
                btn11.setIcon(image);
                btn12.setIcon(image);
                btn13.setIcon(image);
                btn14.setIcon(image);
                btn15.setIcon(image);
                btn16.setIcon(image);
                btn17.setIcon(image);
                btn18.setIcon(image);
            }
        }
    }

    // Exit game
    @Override
    public void closeGame() {
        sendCommand("exitGame");
        exiting = true;
    }

}

