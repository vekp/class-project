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

    static int WIDTH = 3;
    static int HEIGHT = 3;

    record TicTacToePlayer(
        String name,
        char symbol,
        List<String> movesMade
    ) {
    }

    String name;
    AchievementHandler achievementHandler;
    String currentPlayerName;

    public TicTacToeGame(String name, String currentPlayerName) {
        this.name = name;
        this.currentPlayerName = currentPlayerName;
        this.achievementHandler = new AchievementHandler(TicTacToeServer.class);

        // Unlock TicTacToe beginner achievement for starting a new game
        achievementHandler.unlockAchievement(currentPlayerName, TICTACTOE_BEGINNER.achievement.name());
    }

    char[][] board = new char[WIDTH][HEIGHT];

    HashMap<String, TicTacToePlayer> players = new HashMap<>();

    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    public GameMetadata gameMetadata() {
        return new GameMetadata("TicTacToe", name, getPlayerNames(), true);
    }

    private String describeState() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                sb.append(board[i][j] == 0 ? '.' : board[i][j]);
                if (j < WIDTH - 1) sb.append('|');
            }
            sb.append('\n');
            if (i < HEIGHT - 1) sb.append("-----\n");
        }

        return sb.toString();
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        TicTacToePlayer p = players.get(cp.player());

        String userInput = String.valueOf(cp.commands().get(0).getValue("command"));

        // Parse command to get move
        String[] parts = userInput.split(",");
        int x = Integer.parseInt(parts[0].strip());
        int y = Integer.parseInt(parts[1].strip());

        if (board[x][y] == 0) {
            board[x][y] = p.symbol;
            p.movesMade().add(userInput);
        } else {
            // TODO: Handle invalid move
        }

        // Unlock achievements (for simplicity, I've added just one for a move)
        achievementHandler.unlockAchievement(currentPlayerName, FIRST_MOVE.achievement.name());

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState()));

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    public RenderingPackage joinGame(String playerName, char symbol) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                gameMetadata(),
                Arrays.stream(new RenderingCommand[]{
                    new NativeCommands.ShowMenuError("That name's not available")
                }).map(RenderingCommand::toJson).toList()
            );
        } else {
            TicTacToePlayer p = new TicTacToePlayer(playerName, symbol, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("TicTacToeClient", "TicTacToe", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState()));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}
