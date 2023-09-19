package minigames.client.tictactoe;

import java.awt.*;
import javax.swing.*;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;
import java.util.Collections;

public class TicTacToeClient implements GameClient {

    MinigameNetworkClient mnClient;
    GameMetadata gm;
    String player;
    JButton[][] boardButtons = new JButton[3][3];

    JPanel boardPanel;

    public TicTacToeClient() {
        boardPanel = new JPanel(new GridLayout(3, 3));
        
        for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        JButton btn = new JButton("");
        final int x = i;
        final int y = j;
        btn.addActionListener((evt) -> sendMove(x, y));
        boardButtons[i][j] = btn;
        boardPanel.add(btn);
    }
}
    }

    public void sendMove(int x, int y) {
        JsonObject json = new JsonObject()
            .put("command", "move")
            .put("x", x)
            .put("y", y);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        mnClient.getMainWindow().addCenter(boardPanel);
        mnClient.getMainWindow().pack();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        
        switch (command.getString("command")) {
            case "clearBoard":
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        boardButtons[i][j].setText("");
                    }
                }
                break;
            case "setMove":
                int x = command.getInteger("x");
                int y = command.getInteger("y");
                String movePlayer = command.getString("player");
                boardButtons[x][y].setText(movePlayer);
                break;
            case "setWinner":
                String winner = command.getString("player");
                // Handle winner announcement, e.g., through a dialog box
                JOptionPane.showMessageDialog(null, winner.equals("Draw") ? "It's a Draw!" : winner + " Wins!");
                break;
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do
    }
}
