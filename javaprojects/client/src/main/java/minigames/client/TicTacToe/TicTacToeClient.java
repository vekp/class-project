package minigames.client.tictactoe;

import java.awt.*;
import javax.swing.*;
import java.util.Collections;
import io.vertx.core.json.JsonArray;


import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

public class TicTacToeClient implements GameClient {

    MinigameNetworkClient mnClient;
    GameMetadata gm;
    String player;

    JTextArea gameStatus;
    JButton[][] ticTacToeGrid = new JButton[3][3];

    JPanel gamePanel;
    JButton achievementButton;
    JButton backButton;

    public TicTacToeClient() {
        gameStatus = new JTextArea();
        gameStatus.setEditable(false);
        gameStatus.setPreferredSize(new Dimension(800, 30));
        gameStatus.setForeground(Color.BLACK);
        gameStatus.setFont(new Font("Monospaced", Font.PLAIN, 18));

        gamePanel = new JPanel(new GridLayout(3, 3));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int finalI = i;
                final int finalJ = j;
                JButton btn = new JButton();
                btn.addActionListener(e -> sendMove(finalI, finalJ));
                ticTacToeGrid[i][j] = btn;
                gamePanel.add(btn);
            }
        }

        achievementButton = new JButton("Achv");
        achievementButton.addActionListener(e -> mnClient.getGameAchievements(player, gm.gameServer()));

        backButton = new JButton("Back");
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
    }

    public void sendMove(int x, int y) {
        JsonObject json = new JsonObject().put("move", new int[]{x, y});
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        mnClient.getMainWindow().addNorth(gameStatus);
        mnClient.getMainWindow().addCenter(gamePanel);

        mnClient.getMainWindow().addSouth(achievementButton);
        mnClient.getMainWindow().addSouth(backButton);
        
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
    String cmd = command.getString("command");

    switch (cmd) {
        case "updateGrid":
            JsonArray jsonArray = command.getJsonArray("grid");
            for (int i = 0; i < 3; i++) {
                JsonArray row = jsonArray.getJsonArray(i);
                for (int j = 0; j < 3; j++) {
                    ticTacToeGrid[i][j].setText(row.getString(j));
                }
            }
            break;
                
            case "setPlayerTurn":
                gameStatus.setText("Turn: " + command.getString("player"));
                break;

            case "declareWinner":
                gameStatus.setText("Winner: " + command.getString("winner"));
                break;

            case "declareDraw":
                gameStatus.setText("It's a draw!");
                break;
        }
    }

    @Override
    public void closeGame() {
        // Clean up resources or listeners if any.
    }
}
