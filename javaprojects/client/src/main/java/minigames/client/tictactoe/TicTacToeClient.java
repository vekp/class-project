package minigames.client.tictactoe;

import java.util.Collections;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TicTacToeClient implements GameClient {
    MinigameNetworkClient mnClient;

    GameMetadata gm;

    String player;

    // Frame to hold the Tic Tac Toe UI components
    private final JFrame frame;
    // Buttons representing the tic-tac-toe grid
    private final JButton[][] buttons;
    // Keeps track of whose turn it is
    private char currentPlayer;
    // Represents the game board state
    private final char[][] board;
    // Label showing the current status or winner
    private JLabel footerLabel, playerXScoreLabel, playerOScoreLabel;
    // Track scores of players X and O
    private int playerXScore, playerOScore;

    public TicTacToeClient() {
        // Initialize the current player to X
        currentPlayer = 'X';
        // Initial score set to 0
        playerXScore = 0;
        playerOScore = 0;

        // Initialize the game board to empty states
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }

        // Setting up the main frame
        frame = new JFrame("Tic Tac Toe");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); // Making the frame non-resizable

        // Creating a menu for starting a new game
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Options");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem exitGameItem = new JMenuItem("Exit Game");
        exitGameItem.addActionListener(e -> System.exit(0));
        newGameItem.addActionListener(e -> resetGame());
        gameMenu.add(newGameItem);
        gameMenu.add(exitGameItem);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);

        // Panel for the tic-tac-toe grid buttons
        JPanel gridPanel = new JPanel(new GridLayout(3, 3)) {
            @Override
            public Dimension getPreferredSize() {
                // Adjust size to make it square
                Dimension d = super.getPreferredSize();
                Dimension prefSize;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension((int) d.getWidth(), (int) d.getWidth());
                } else if (c.getWidth() > d.getWidth() && c.getHeight() > d.getWidth()) {
                    prefSize = new Dimension((int) d.getWidth(), (int) d.getWidth());
                } else {
                    prefSize = c.getSize();
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                int s = (Math.min(w, h)); // Smaller of the two sizes
                return new Dimension(s, s);
            }
        };

        // Initialize and add buttons to the grid panel
        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("") {
                    @Override
                    public Dimension getPreferredSize() {
                        // Square size of the button
                        return new Dimension(50, 50);
                    }
                };
                gridPanel.add(buttons[i][j]);

                // Set the initial font size to 12 points
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 12));

                // Action when button is clicked
                buttons[i][j].addActionListener(e -> {
                    JButton clickedButton = (JButton) e.getSource();
                    clickedButton.setText(String.valueOf(currentPlayer));

                    // Increase the font size
                    Font currentFont = clickedButton.getFont();
                    Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 50); // increase
                                                                                                                        // the
                                                                                                                        // size
                                                                                                                        // by
                                                                                                                        // 50
                    clickedButton.setFont(newFont);

                    clickedButton.setEnabled(false);

                    // Finding out which grid/button was clicked
                    int row = -1;
                    int col = -1;
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (buttons[x][y] == clickedButton) {
                                row = x;
                                col = y;
                                break;
                            }
                        }
                    }

                    // Update the game board state
                    board[row][col] = currentPlayer;

                    // Check for game winner
                    if (hasContestantWon(board, currentPlayer)) {
                        JOptionPane.showMessageDialog(frame, "Player " + currentPlayer + " wins!");
                        // Update scores accordingly
                        if (currentPlayer == 'X') {
                            playerXScore++;
                            playerXScoreLabel.setText("Player X score: " + playerXScore);
                        } else {
                            playerOScore++;
                            playerOScoreLabel.setText("Player O score: " + playerOScore);
                        }
                        resetGame(); // Reset the game state
                    } else if (isBoardFull()) {
                        // No more moves possible
                        JOptionPane.showMessageDialog(frame, "It's a tie!");
                        resetGame(); // Reset the game state
                    }

                    // Switch turns
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    footerLabel.setText("Player " + currentPlayer + "'s turn");
                });
            }
        }

        // Add the game board/grid to the main frame
        frame.add(gridPanel, BorderLayout.CENTER);

        // Set up the footer label to display whose turn it is
        footerLabel = new JLabel("Player " + currentPlayer + "'s turn");
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(footerLabel, BorderLayout.SOUTH);

        // Panel to display scores of players
        JPanel scorePanel = new JPanel(new GridLayout(1, 2));

        playerXScoreLabel = new JLabel("Player X score: " + playerXScore);
        playerOScoreLabel = new JLabel("Player O score: " + playerOScore, SwingConstants.RIGHT);

        // Set padding for the labels
        playerXScoreLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        playerOScoreLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add score labels to the panel
        scorePanel.add(playerXScoreLabel);
        scorePanel.add(playerOScoreLabel);

        // Add the score panel to the main frame
        frame.add(scorePanel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true); // Show the frame
    }

    // Check if a player has won
    private boolean hasContestantWon(char[][] board, char symbol) {
        // Check rows and columns for a win
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) {
                return true;
            }
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol) {
                return true;
            }
        }

        // Check diagonals for a win
        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) {
            return true;
        }
        return board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol;
    }

    // Check if the board is full (all cells occupied)
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    // Reset the game state to its initial state
    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);

                // Reset the font size to its initial value (12 points)
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 12));
            }
        }
        currentPlayer = 'X';
        footerLabel.setText("Player " + currentPlayer + "'s turn");
    }

    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
    }

    @Override
    public void closeGame() {
        // Clean up resources if needed
    }

}
