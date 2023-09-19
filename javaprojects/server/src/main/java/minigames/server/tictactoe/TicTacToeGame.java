package minigames.server.tictactoe;

import java.util.*;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import static minigames.server.tictactoe.TicTacToeAchievement.*;

public class TicTacToeGame {

    private static final Logger logger = LogManager.getLogger(TicTacToeGame.class);

    record TicTacToePlayer(String name, char symbol) { }

    String name;
    AchievementHandler achievementHandler;
    String currentPlayerName;

    char[][] board = new char[3][3];

    HashMap<String, TicTacToePlayer> players = new HashMap<>();

    public TicTacToeGame(String name, String playerName) {
        this.name = name;
        this.currentPlayerName = playerName;
        this.achievementHandler = new AchievementHandler(TicTacToeServer.class);

        // Initialization of the board
        for (int i = 0; i < 3; i++) {
            Arrays.fill(board[i], ' ');
        }

        // Assume 'X' starts first
        players.put(playerName, new TicTacToePlayer(playerName, 'X'));
    }

    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    public GameMetadata gameMetadata() {
        return new GameMetadata("TicTacToe", name, getPlayerNames(), true);
    }

    private String describeState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
                if (j < 2) {
                    sb.append('|');
                }
            }
            if (i < 2) {
                sb.append('\n');
                sb.append("-----");
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        
        // Extracting the move from the command package
        int x = Integer.parseInt(cp.commands().get(0).getValue("x").toString());
        int y = Integer.parseInt(cp.commands().get(0).getValue("y").toString());

        char playerSymbol = players.get(currentPlayerName).symbol();

        // Check if the move is valid
        if (board[x][y] == ' ') {
            board[x][y] = playerSymbol;

            // Switch to the next player
            currentPlayerName = getNextPlayerName();
        } else {
            // Handle invalid move
            logger.warn("Invalid move by {}", currentPlayerName);
        }

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState()));

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    private String getNextPlayerName() {
        return players.keySet().stream().filter(player -> !player.equals(currentPlayerName)).findFirst().orElse(currentPlayerName);
    }

    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                gameMetadata(),
                Arrays.stream(new RenderingCommand[] {
                    new NativeCommands.ShowMenuError("That name's not available")
                }).map(RenderingCommand::toJson).toList()
            );
        } else {
            char symbol = players.get(currentPlayerName).symbol() == 'X' ? 'O' : 'X';
            players.put(playerName, new TicTacToePlayer(playerName, symbol));

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("TicTacToeClient", "TicTacToe", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState()));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}
