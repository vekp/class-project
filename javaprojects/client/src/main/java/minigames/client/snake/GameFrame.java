package minigames.client.snake;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import minigames.client.snake.*;

/**
 * GameFrame represents the main window for the Snake game.
 *
 * This frame comprises the game's display, score panel, and start button. The start button initiates
 * the game, and once started, the button becomes disabled.
 */
public class GameFrame extends JFrame {

    // Button to start the game.
    private final JButton startButton;

    // Indicates if the game has started.
    private boolean gameStarted = false;

    // The main display area for the Snake game.
    private final GameDisplay gameDisplay;

    /**
     * Constructor for GameFrame.
     *
     * Initializes the frame with game's title, close operation, and components
     * (score panel, game display, and start button).
     */
    GameFrame() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Score panel at the top to display the player's score.
        ScorePanel scorePanel = new ScorePanel();

        // Main game display where the Snake game runs.
        gameDisplay = new GameDisplay(scorePanel);

        // Button to start the Snake game.
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start the game only if it hasn't already started.
                if (!gameStarted) {
                    gameStarted = true;
                    startButton.setEnabled(false);
                    gameDisplay.startGame();
                }
            }
        });

        // Set border around the game display for aesthetics.
        Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(174, 52, 255));
        gameDisplay.setBorder(border);

        // Layout components on the frame.
        setLayout(new BorderLayout());
        add(scorePanel, BorderLayout.NORTH);
        add(gameDisplay, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        // Adjust frame size to fit components, center it on screen, and display it.
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
