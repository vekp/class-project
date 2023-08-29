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
import javax.swing.border.Border;
import javax.swing.Timer;

/**
 * COSC220 Assessment 3
 * @author Melinda Luo, Lyam Talbot, Scott Lehmann, William Koller
 * Memory Card Game
 */

public class MemoryGUI extends JPanel implements ActionListener {

    /** Swing components */
    private JPanel mainPanel, gameMenuPanel, commandPanel, headingPanel, playerPanel, gameOptionsPanel, gridContainerPanel, cardGridPanel;

    private JLabel testLabel;
    private JLabel title, playerName, matches, stopwatch, difficulty;

    private JButton newGameButton, restartLevelButton, exitButton, achievementsButton;

    private Border margin;

    private int rows, columns;


    ImageIcon cardBackImage = new ImageIcon(getClass().getResource("/memory/images/playing_cards/back/card_back_black.png"));

    // Path to card images directory (card front images only)
    String cardImagesDirectory = getClass().getResource("/memory/images/playing_cards/front/").getPath();


    public MemoryGUI() {
        margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        setTestLabel();
        setPlayerPanelDisplay();
        setGameOptionsPanelDisplay();
        setGridContainerPanelDisplay();
        setGameMenuPanelDisplay();

        setMainPanelDisplay();

    this.add(mainPanel, BorderLayout.CENTER);

    }

    private void setTestLabel(){

        testLabel = new JLabel("TestLabel");
        testLabel.setFont(new Font("Monospaced", Font.PLAIN, 25));
        testLabel.setOpaque(false);
        testLabel.setBorder(margin);
        testLabel.setForeground(new Color(0xFFFFFF));

    }


    private void setMainPanelDisplay(){


        // Set up the main layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gameMenuPanel, BorderLayout.NORTH);
        mainPanel.add(cardGridPanel, BorderLayout.CENTER);
        //mainPanel.add(gridContainerPanel, BorderLayout.CENTER);
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

        playerName = new JLabel("Player: " + "NEED TO FIX"); //player); // Placeholder text for player name
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
        //exitButton.addActionListener(e -> Memory.mnClient.runMainMenuSequence());

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

    private void setGridContainerPanelDisplay(){
        int containerWidth = 500;
        int containerHeight = 465;


        rows = 3;
        columns = 6;

        cardGridPanel = new JPanel();
        cardGridPanel.setLayout(new GridLayout(rows, columns, 0, 0));
        cardGridPanel.setPreferredSize(new Dimension(containerWidth, containerHeight));

        for (int i = 0; i < (rows * columns); i++) {
            JButton card = new JButton(resizeImageIcon(cardBackImage,-1,containerHeight/rows-5));
            card.setPreferredSize(new Dimension(containerWidth/columns, containerHeight/rows));
            card.setActionCommand("Card_" + (i+1));
            card.addActionListener(this);
            cardGridPanel.add(card);

        }

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
        String actionCommand = button.getActionCommand();
        System.out.println(actionCommand);
    }



}
    
