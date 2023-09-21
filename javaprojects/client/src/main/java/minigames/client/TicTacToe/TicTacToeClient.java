package minigames.client.TicTacToe;

import javax.swing.JButton;
import javax.swing.JPanel;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;
import java.awt.GridLayout;
import java.util.Collections;

public class TicTacToeClient implements GameClient {

    private MinigameNetworkClient mnClient;
    private GameMetadata gm;
    private String player;

    private JButton[][] boardButtons = new JButton[3][3];
    private JPanel boardPanel;

    public TicTacToeClient() {
        boardPanel = new JPanel(new GridLayout(3, 3));
        for (int tempI = 0; tempI < 3; tempI++) {
            for (int tempJ = 0; tempJ < 3; tempJ++) {
                final int i = tempI;
                final int j = tempJ;
                JButton btn = new JButton("");
                btn.addActionListener(e -> sendMove(i, j));
                boardButtons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
    }

    private void sendMove(int i, int j) {
        JsonObject json = new JsonObject()
            .put("command", "move")
            .put("cell", i + "-" + j)
            .put("player", player);
        

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

        String cmd = command.getString("command");
        if ("move".equals(cmd)) {
            String[] cell = command.getString("cell").split("-");
            int i = Integer.parseInt(cell[0]);
            int j = Integer.parseInt(cell[1]);
            String playerMove = command.getString("player");

            boardButtons[i][j].setText(playerMove);
        }
    }

    @Override
    public void closeGame() {
        // Clear the board or any other cleanup tasks.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText("");
            }
        }
    }
}
