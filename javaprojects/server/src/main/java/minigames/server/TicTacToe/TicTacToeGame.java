package minigames.server.tictactoe;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.RenderingPackage;

import java.util.Arrays;

public class TicTacToeGame {

    public enum PlayerSymbol {
        X, O
    }

    public enum GameStatus {
        PLAYING, X_WINS, O_WINS, DRAW
    }

    private String name;
    private String[] players = new String[2];
    private PlayerSymbol[] board = new PlayerSymbol[9];
    private PlayerSymbol currentPlayer = PlayerSymbol.X;
    private GameStatus status = GameStatus.PLAYING;

    public TicTacToeGame(String name, String initialPlayer) {
        this.name = name;
        players[0] = initialPlayer;
        Arrays.fill(board, null);
    }

    public RenderingPackage joinGame(String playerName) {
        if (players[1] == null) {
            players[1] = playerName;
        }

        JsonObject command = new JsonObject().put("command", "join").put("player", playerName);
        return new RenderingPackage(name, players[0], command);
    }

    public String[] getPlayerNames() {
        return players;
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        JsonObject command = cp.commands().get(0); // assume single command for simplicity

        switch (command.getString("command")) {
            case "place":
                int position = command.getInteger("position");
                if (board[position] == null) {
                    board[position] = currentPlayer;
                    checkGameStatus();
                    switchPlayer();
                }
                break;
        }

        return renderGameState();
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer == PlayerSymbol.X ? PlayerSymbol.O : PlayerSymbol.X;
    }

    private void checkGameStatus() {
        // Check rows, columns, and diagonals
        if (
            (board[0] == currentPlayer && board[1] == currentPlayer && board[2] == currentPlayer) ||
            (board[3] == currentPlayer && board[4] == currentPlayer && board[5] == currentPlayer) ||
            (board[6] == currentPlayer && board[7] == currentPlayer && board[8] == currentPlayer) ||
            (board[0] == currentPlayer && board[3] == currentPlayer && board[6] == currentPlayer) ||
            (board[1] == currentPlayer && board[4] == currentPlayer && board[7] == currentPlayer) ||
            (board[2] == currentPlayer && board[5] == currentPlayer && board[8] == currentPlayer) ||
            (board[0] == currentPlayer && board[4] == currentPlayer && board[8] == currentPlayer) ||
            (board[2] == currentPlayer && board[4] == currentPlayer && board[6] == currentPlayer)
        ) {
            status = currentPlayer == PlayerSymbol.X ? GameStatus.X_WINS : GameStatus.O_WINS;
            return;
        }

        boolean isDraw = true;
        for (PlayerSymbol cell : board) {
            if (cell == null) {
                isDraw = false;
                break;
            }
        }

        if (isDraw) {
            status = GameStatus.DRAW;
        }
    }

    private RenderingPackage renderGameState() {
        JsonObject gameState = new JsonObject()
            .put("board", board)
            .put("currentPlayer", currentPlayer.toString())
            .put("status", status.toString());

        return new RenderingPackage(name, players[0], gameState);
    }
}
