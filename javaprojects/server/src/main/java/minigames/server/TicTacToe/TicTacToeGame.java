package minigames.server.tictactoe;

import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;

import java.util.Arrays;

public class TicTacToeGame {

    public enum PlayerSymbol {
        X, O
    }

    public enum GameStatus {
        PLAYING, X_WINS, O_WINS, DRAW
    }

    public String name;
    private String[] players = new String[2];
    private PlayerSymbol[] board = new PlayerSymbol[9];
    private PlayerSymbol currentPlayer = PlayerSymbol.X;
    private GameStatus status = GameStatus.PLAYING;

    public TicTacToeGame(String name, String initialPlayer) {
        this.name = name;
        players[0] = initialPlayer;
        Arrays.fill(board, null);
    }

    public GameMetadata gameMetadata() {
    // Determine the list of players currently in the game (excluding null entries)
    List<String> playerNames = Arrays.stream(players)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

    // Assume the game is joinable if there are fewer than 2 players
    boolean isJoinable = playerNames.size() < 2;

    return new GameMetadata("TicTacToe", name, playerNames.toArray(new String[0]), isJoinable);
    
}


    public RenderingPackage joinGame(String playerName) {
    if (players[1] == null) {
        players[1] = playerName;
    }

    ArrayList<JsonObject> renderingCommands = new ArrayList<>();
    renderingCommands.add(new JsonObject().put("command", "join").put("player", playerName));

    return new RenderingPackage(gameMetadata(), renderingCommands);
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
    ArrayList<JsonObject> renderingCommands = new ArrayList<>();
    
    JsonObject gameState = new JsonObject()
            .put("command", "updateGameState")
            .put("board", board)
            .put("currentPlayer", currentPlayer.toString())
            .put("status", status.toString());
    
    renderingCommands.add(gameState);
    
    return new RenderingPackage(gameMetadata(), renderingCommands);
}

}
