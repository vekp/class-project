package minigames.server.snake;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.achievements.AchievementHandler;
import minigames.server.gameNameGenerator.GameNameGenerator;  // Import the GameNameGenerator

import java.util.HashMap;

public class SnakeServer implements GameServer {

    // Instance variable to handle achievements
    AchievementHandler achievementHandler;

    // HashMap to hold the games in progress in memory
    HashMap<String, SnakeGame> games = new HashMap<>();

    // Instance of the GameNameGenerator
    GameNameGenerator nameGenerator;

    /**
     * Constructor for SnakeServer
     */
    public SnakeServer() {
        achievementHandler = new AchievementHandler(SnakeServer.class);
        // Register all achievements with handler
        for (SnakeAchievement a : SnakeAchievement.values()) {
            achievementHandler.registerAchievement(a.achievement);
        }

        // Initialize the GameNameGenerator with "snake" and "space" categories
        nameGenerator = new GameNameGenerator("snake", "space");
    }

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Snake", "Navigate your snake through the dark depths of space!");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Snake", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        String randomGameName = nameGenerator.randomName();  // Use the name generator
        SnakeGame g = new SnakeGame(randomGameName, playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        SnakeGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        SnakeGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
}
