package minigames.server.tictactoe;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicTacToeGame {

    String name;
    char[] board = new char[9];
    List<String> players = new ArrayList<>();
    int currentPlayerIndex = 0;

    public TicTacToeGame(String name, String firstPlayer) {
        this.name = name;
        Arrays.fill(board, '\0');
        players.add(firstPlayer);
    }

    public String[] getPlayerNames() {
        return players.toArray(new String[0]);
    }

    public RenderingPackage joinGame(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }

        List<JsonObject> commands = new ArrayList<>();
        commands.add(new JsonObject().put("boardState", boardState()));
        GameMetadata metadata = new GameMetadata("TicTacToe", name, players.toArray(new String[0]), true);

        return new RenderingPackage(metadata, commands);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        List<JsonObject> commands = new ArrayList<>();

        if (!cp.player().equals(players.get(currentPlayerIndex))) {
            commands.add(new JsonObject().put("message", "notYourTurn"));
            GameMetadata metadata = new GameMetadata("TicTacToe", name, players.toArray(new String[0]), true);
            return new RenderingPackage(metadata, commands);
        }

        int move = cp.commands().get(0).getInteger("move");
        char currentPlayerMark = currentPlayerIndex == 0 ? 'X' : 'O';

        if (board[move] == '\0') {
            board[move] = currentPlayerMark;

            if (checkVictory(currentPlayerMark)) {
                commands.add(new JsonObject().put("result", "gameWon").put("winner", currentPlayerMark));
            } else if (isBoardFull()) {
                commands.add(new JsonObject().put("result", "gameDraw"));
            } else {
                currentPlayerIndex = 1 - currentPlayerIndex;
            }
        } else {
            commands.add(new JsonObject().put("message", "cellTaken"));
        }

        commands.add(new JsonObject().put("boardState", boardState()));
        GameMetadata metadata = new GameMetadata("TicTacToe", name, players.toArray(new String[0]), true);

        return new RenderingPackage(metadata, commands);
    }

    private boolean checkVictory(char mark) {
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == mark && board[i + 1] == mark && board[i + 2] == mark) return true;
        }
        for (int i = 0; i < 3; i++) {
            if (board[i] == mark && board[i + 3] == mark && board[i + 6] == mark) return true;
        }
        if (board[0] == mark && board[4] == mark && board[8] == mark) return true;
        if (board[2] == mark && board[4] == mark && board[6] == mark) return true;

        return false;
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '\0') return false;
        }
        return true;
    }

    private String boardState() {
        return new String(board);
    }
}
