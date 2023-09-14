package minigames.client.noughtsandcrosses;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import java.util.Random;

/**
 * A very simple interface for a text-based game.
 */
public class NoughtsAndCrosses implements GameClient {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Your name */
    String player;

    /** A text area for showing room descriptions, etc */
    JTextArea textArea;

    /** Direction commands */
    JButton playAgainButton;

    JPanel commandPanel;

    JPanel panel;
    JPanel frame;
    JLabel gameStateLabel;

    JPanel title_panel = new JPanel();
    JLabel textfield = new JLabel();

    int ROWS = 3; // adjustable variable for grid size
    int COLS = 3; // adjustable variable for grid size
    int GAP = 5; // board cell padding

    boolean isPlayerTurn;  // Track if it's the player's turn


    public NoughtsAndCrosses() {
        frame = new JPanel();
        frame.setLayout(new BorderLayout());

        // Text Area at the top
        JLabel titleLabel = new JLabel("Noughts And Crosses Game");
        titleLabel.setPreferredSize(new Dimension(frame.getWidth(), 50));
        titleLabel.setForeground(Color.BLACK);
        //titleLabel.setBackground(Color.LIGHT_GRAY);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setVerticalAlignment(JLabel.CENTER);
        titleLabel.setOpaque(true);

        // Game State Area below the 3x3 panel
        gameStateLabel = new JLabel("Your Turn");
        gameStateLabel.setPreferredSize(new Dimension(frame.getWidth(), 50));
        gameStateLabel.setForeground(Color.BLUE);
        //gameStateLabel.setBackground(Color.LIGHT_GRAY);
        gameStateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        gameStateLabel.setHorizontalAlignment(JLabel.CENTER);
        gameStateLabel.setVerticalAlignment(JLabel.CENTER);
        gameStateLabel.setOpaque(true);

        panel = new JPanel();
        panel.setLayout(new GridLayout(ROWS, COLS));
        panel.setPreferredSize(new Dimension(450, 450));
        panel.setBackground(new Color(150,150,150));

        // Create 3x3 grid of buttons
        JButton[][] buttons = new JButton[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                buttons[i][j].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 70));

                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isPlayerTurn && "".equals(((JButton) e.getSource()).getText())) {
                            JButton clickedButton = (JButton) e.getSource();
                            clickedButton.setText("X");
                            clickedButton.setForeground(Color.BLUE);  // Set 'X' color
                            isPlayerTurn = false;

                            if (!checkWinner(buttons)) {
                                gameStateLabel.setText("Computer's Turn");
                                gameStateLabel.setForeground(Color.BLACK);
                                // Sets a delay before the computer makes a move
                                Timer timer = new Timer(2000, new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {
                                        computerMove(buttons);
                                        checkWinner(buttons);
                                    }
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                    }
                });

                panel.add(buttons[i][j]);
            }
        }

        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(gameStateLabel, BorderLayout.SOUTH);

        // Randomly decide who goes first whenever a new game starts
        isPlayerTurn = new Random().nextBoolean();
        if (isPlayerTurn) {
            gameStateLabel.setText("Your Turn");
            gameStateLabel.setForeground(Color.BLUE);
        } else {
            gameStateLabel.setText("Computer's Turn");
            gameStateLabel.setForeground(Color.BLACK);
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    computerMove(buttons);
                    checkWinner(buttons);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }


        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());

        // Restarts the game
        playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener((evt) -> playAgain(buttons));

        commandPanel = new JPanel();
        for (Component c : new Component[] { playAgainButton, backButton }) {
            commandPanel.add(c);
        }

    }

    // Determines the computers move by first checking if they can make a winning move or prevent a losing move. If neither is found then make a randon move.
    public void computerMove(JButton[][] buttons) {
        boolean moveMade = false;

        // First, check for a winning move using 'O'
        if (!moveMade) {
            moveMade = checkAndMakeMove(buttons, "O");
        }

        // If no winning move found, check for a blocking move against the player using 'X'
        if (!moveMade) {
            moveMade = checkAndMakeMove(buttons, "X");

            isPlayerTurn = true;
            gameStateLabel.setText("Your Turn");
            gameStateLabel.setForeground(Color.BLUE);
        }

        // If neither winning nor blocking move is found, make a random move
        if (!moveMade) {
            Random rand = new Random();
            int i, j;
            do {
                i = rand.nextInt(3);
                j = rand.nextInt(3);
            } while (!"".equals(buttons[i][j].getText()));
            buttons[i][j].setText("O");

            isPlayerTurn = true;
            gameStateLabel.setText("Your Turn");
            gameStateLabel.setForeground(Color.BLUE);
        }

    }
    // Called by computerMove to make the move
    public boolean checkAndMakeMove(JButton[][] buttons, String character) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            int countChar = 0;
            int countEmpty = 0;
            int emptyCol = -1;
            for (int j = 0; j < 3; j++) {
                if (character.equals(buttons[i][j].getText())) {
                    countChar++;
                } else if ("".equals(buttons[i][j].getText())) {
                    countEmpty++;
                    emptyCol = j;
                }
            }
            if (countChar == 2 && countEmpty == 1) {
                buttons[i][emptyCol].setText("O");
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            int countChar = 0;
            int countEmpty = 0;
            int emptyRow = -1;
            for (int i = 0; i < 3; i++) {
                if (character.equals(buttons[i][j].getText())) {
                    countChar++;
                } else if ("".equals(buttons[i][j].getText())) {
                    countEmpty++;
                    emptyRow = i;
                }
            }
            if (countChar == 2 && countEmpty == 1) {
                buttons[emptyRow][j].setText("O");
                return true;
            }
        }

        // Check diagonals
        int countChar = 0;
        int countEmpty = 0;
        int emptyRow = -1;
        int emptyCol = -1;
        for (int i = 0; i < 3; i++) {
            if (character.equals(buttons[i][i].getText())) {
                countChar++;
            } else if ("".equals(buttons[i][i].getText())) {
                countEmpty++;
                emptyRow = i;
                emptyCol = i;
            }
        }
        if (countChar == 2 && countEmpty == 1) {
            buttons[emptyRow][emptyCol].setText("O");
            return true;
        }

        countChar = 0;
        countEmpty = 0;
        for (int i = 0; i < 3; i++) {
            if (character.equals(buttons[i][2 - i].getText())) {
                countChar++;
            } else if ("".equals(buttons[i][2 - i].getText())) {
                countEmpty++;
                emptyRow = i;
                emptyCol = 2 - i;
            }
        }
        if (countChar == 2 && countEmpty == 1) {
            buttons[emptyRow][emptyCol].setText("O");
            return true;
        }

        return false;
    }

    // Looks for a winner, if found set the border of the buttons to red, display winning message.
    public boolean checkWinner(JButton[][] buttons) {
        LineBorder winBorder = new LineBorder(Color.RED, 1);

        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText();
            }
        }

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!"".equals(field[i][0]) && field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2])) {
                buttons[i][0].setBorder(winBorder);
                buttons[i][1].setBorder(winBorder);
                buttons[i][2].setBorder(winBorder);
                updateGameStateLabel(field[i][0]);
                disableButtons(buttons);
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (!"".equals(field[0][i]) && field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i])) {
                buttons[0][i].setBorder(winBorder);
                buttons[1][i].setBorder(winBorder);
                buttons[2][i].setBorder(winBorder);
                updateGameStateLabel(field[0][i]);
                disableButtons(buttons);
                return true;
            }
        }

        // Check diagonals
        if (!"".equals(field[0][0]) && field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2])) {
            buttons[0][0].setBorder(winBorder);
            buttons[1][1].setBorder(winBorder);
            buttons[2][2].setBorder(winBorder);
            updateGameStateLabel(field[0][0]);
            disableButtons(buttons);
            return true;
        }

        if (!"".equals(field[0][2]) && field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0])) {
            buttons[0][2].setBorder(winBorder);
            buttons[1][1].setBorder(winBorder);
            buttons[2][0].setBorder(winBorder);
            updateGameStateLabel(field[0][2]);
            disableButtons(buttons);
            return true;
        }

        // Check for draw
        boolean allFilled = true;
        for (int i = 0; i < 3 && allFilled; i++) {
            for (int j = 0; j < 3 && allFilled; j++) {
                if ("".equals(buttons[i][j].getText())) {
                    allFilled = false;
                }
            }
        }

        if (allFilled) {
            gameStateLabel.setText("Draw");
            disableButtons(buttons);
            return true;
        }

        // Update game state label for next turn
        if (isPlayerTurn) {
            gameStateLabel.setText("Your Turn");
            gameStateLabel.setForeground(Color.BLUE);
        } else {
            gameStateLabel.setText("Computer's Turn");
            gameStateLabel.setForeground(Color.BLACK);
        }

        return false;
    }

    public void updateGameStateLabel(String winner) {
        if ("X".equals(winner)) {
            gameStateLabel.setText("You Win");
            gameStateLabel.setForeground(Color.BLUE);
        } else {
            gameStateLabel.setText("You Lose");
            gameStateLabel.setForeground(Color.BLACK);
        }
    }

    public void disableButtons(JButton[][] buttons) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void resetBorders(JButton[][] buttons) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }
    }

    // Called when the play again button is pressed. It resets the game.
    public void playAgain(JButton[][] buttons) {
        // Reset all buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setForeground(Color.BLACK);
                buttons[i][j].setEnabled(true);
            }
        }

        // Reset borders
        resetBorders(buttons);

        // Randomly decide who goes first
        isPlayerTurn = new Random().nextBoolean();
        if (isPlayerTurn) {
            gameStateLabel.setText("Your Turn");
            gameStateLabel.setForeground(Color.BLUE);
        } else {
            gameStateLabel.setText("Computer's Turn");
            gameStateLabel.setForeground(Color.BLACK);
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    computerMove(buttons);
                    checkWinner(buttons);
                }
            });
            timer.setRepeats(false);
            timer.start();
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
        mnClient.getMainWindow().addCenter(frame);
        mnClient.getMainWindow().addSouth(commandPanel);

        // Don't forget to call pack - it triggers the window to resize and repaint itself
        mnClient.getMainWindow().pack();
        //mnClient.getNotificationManager().setMargins(15, 10, 10);
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
