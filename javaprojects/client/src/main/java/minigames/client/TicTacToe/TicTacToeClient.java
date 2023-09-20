package minigames.client.tictactoe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Collections;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

public class TicTacToeClient implements GameClient {

    MinigameNetworkClient mnClient;
    GameMetadata gm;
    String player;

    JTextArea messageArea;
    JButton[][] boardButtons = new JButton[3][3];

    public TicTacToeClient() {
        messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j] = new JButton("");
                boardButtons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton clickedButton = (JButton) e.getSource();
                        int rowIndex = -1, colIndex = -1;
                        for (int r = 0; r < 3; r++) {
                            for (int c = 0; c < 3; c++) {
                                if (boardButtons[r][c] == clickedButton) {
                                    rowIndex = r;
                                    colIndex = c;
                                }
                            }
                        }
                        int position = rowIndex * 3 + colIndex;
                        sendMove(position);
                    }
                });
            }
        }
    }

    public void sendMove(int position) {
        JsonObject json = new JsonObject().put("command", "makeMove").put("move", position);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardPanel.add(boardButtons[i][j]);
            }
        }

        mnClient.getMainWindow().addCenter(boardPanel);
        mnClient.getMainWindow().addSouth(messageArea);

        messageArea.append("Welcome to Tic Tac Toe!");

        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        switch (command.getString("command")) {
            case "boardUpdate":
                char[] boardState = command.getJsonArray("board").getString(0).toCharArray();
                for (int i = 0; i < 9; i++) {
                    boardButtons[i / 3][i % 3].setText(String.valueOf(boardState[i]));
                }
                break;
            case "gameWon":
                String winner = command.getString("winner");
                messageArea.append("\n" + winner + " has won the game!");
                break;
            case "gameDraw":
                messageArea.append("\nIt's a draw!");
                break;
        }
    }

    @Override
    public void closeGame() {
        // Cleanup resources if any
    }
}
