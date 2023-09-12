import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Collections;
import com.somepackage.networking.*;  // Placeholder import for networking utilities

public class TicTacToeClient implements Minigame {

    private MinigameNetworkClient ttClient;
    private GameMetadata gm;
    private String player;
    private String opponent;
    private JButton[][] boardButtons = new JButton[3][3];
    private boolean isMyTurn = false;
    private String mySymbol = "";
    private String opponentSymbol = "";

    // Constructor to initialize the board UI
    public TicTacToeClient() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int row = (int) e.getSource().getClientProperty("row");
                        int col = (int) e.getSource().getClientProperty("col");
                        makeMove(row, col);
                    }
                });
                boardButtons[i][j].putClientProperty("row", i);
                boardButtons[i][j].putClientProperty("col", j);
                boardButtons[i][j].setEnabled(false);  // Disable buttons initially
            }
        }
    }

    // Load game and set up initial configurations
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.ttClient = mnClient;
        this.gm = game;
        this.player = player;
        this.opponent = player.equals("Player1") ? "Player2" : "Player1";
        this.mySymbol = player.equals("Player1") ? "X" : "O";
        this.opponentSymbol = player.equals("Player1") ? "O" : "X";
        this.isMyTurn = player.equals("Player1");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setEnabled(isMyTurn);
            }
        }
    }

    // Handling received game commands
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        String cmd = command.getString("command");

        if ("makeMove".equals(cmd)) {
            int row = command.getInt("row");
            int col = command.getInt("col");
            String symbol = command.getString("symbol");

            // Guard against invalid moves
            if (!"".equals(boardButtons[row][col].getText())) {
                return;
            }

            boardButtons[row][col].setText(symbol);

            // Check game end conditions
            if (checkForWin(row, col, symbol) || isBoardFull()) {
                if (checkForWin(row, col, symbol)) {
                    JOptionPane.showMessageDialog(null, symbol.equals(mySymbol) ? "You Win!" : "You Lose!");
                } else {
                    JOptionPane.showMessageDialog(null, "It's a Draw!");
                }
                closeGame();
                return;
            }

            // Swap turns
            isMyTurn = !isMyTurn;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    boardButtons[i][j].setEnabled(isMyTurn);
                }
            }
        }
    }

    // Execute move after ensuring valid conditions
    public void makeMove(int row, int col) {
        if (!isMyTurn || !"".equals(boardButtons[row][col].getText())) {
            return;
        }

        boardButtons[row][col].setText(mySymbol);

        JsonObject command = new JsonObject();
        command.put("command", "makeMove");
        command.put("row", row);
        command.put("col", col);
        command.put("symbol", mySymbol);

        ttClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(command)));
    }

    // Check for win condition
    private boolean checkForWin(int x, int y, String symbol) {
        // Check respective column
        if (boardButtons[x][0].getText().equals(symbol) &&
            boardButtons[x][1].getText().equals(symbol) &&
            boardButtons[x][2].getText().equals(symbol)) {
            return true;
        }

        // Check respective row
        if (boardButtons[0][y].getText().equals(symbol) &&
            boardButtons[1][y].getText().equals(symbol) &&
            boardButtons[2][y].getText().equals(symbol)) {
            return true;
        }

        // Check main diagonal
        if (boardButtons[0][0].getText().equals(symbol) &&
            boardButtons[1][1].getText().equals(symbol) &&
            boardButtons[2][2].getText().equals(symbol)) {
            return true;
        }

        // Check other diagonal
        if (boardButtons[0][2].getText().equals(symbol) &&
            boardButtons[1][1].getText().equals(symbol) &&
            boardButtons[2][0].getText().equals(symbol)) {
            return true;
        }

        return false;
    }

    // Check if the board is full leading to a draw
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ("".equals(boardButtons[i][j].getText())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Cleanup or reset when the game ends
    @Override
    public void closeGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText("");
                boardButtons[i][j].setEnabled(false);
            }
        }
        isMyTurn = player.equals("Player1");
    }
}
