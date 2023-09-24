package minigames.server.snake;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.achievements.AchievementHandler;
import minigames.server.gameNameGenerator.GameNameGenerator;

import java.util.HashMap;

/**
 * SnakeServer class represents the game server for the Snake game.
 * It implements the GameServer interface and handles game management operations such as starting, joining,
 * and calling games.
 */
public class SnakeServer implements GameServer {

    /**
     * Instance variable to handle game achievements.
     */
    AchievementHandler achievementHandler;

    /**
     * HashMap to store the ongoing games with their game names as keys and the game instances as values.
     */
    HashMap<String, SnakeGame> games = new HashMap<>();

    /**
     * Instance of GameNameGenerator to generate random game names.
     */
    GameNameGenerator nameGenerator;

    /**
     * Constructor initializes the SnakeServer with an AchievementHandler and a GameNameGenerator.
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
    /**
     * Provides details of the Snake game server.
     * @return GameServerDetails object with the name and description of the game.
     */
    public GameServerDetails getDetails() {
        return new GameServerDetails("Snake", "Retro Thrills, Modern Feels: Dive into 3310 Nostalgia!");
    }

    @Override
    /**
     * Lists the client types supported by the Snake game server.
     * @return An array of ClientType enumerations indicating the supported clients.
     */
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    /**
     * Lists the games currently in progress.
     * @return An array of GameMetadata objects representing the ongoing games.
     */
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Snake", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    /**
     * Starts a new Snake game with a randomly generated name.
     * @param playerName Name of the player starting the game.
     * @return A Future holding the RenderingPackage for the game.
     */
    public Future<RenderingPackage> newGame(String playerName) {
        String randomGameName = nameGenerator.randomName();  // Use the name generator
        SnakeGame g = new SnakeGame(randomGameName, playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    /**
     * Allows a player to join an ongoing Snake game.
     * @param game Name of the game to be joined.
     * @param playerName Name of the player joining the game.
     * @return A Future holding the RenderingPackage for the game.
     */
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        SnakeGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    /**
     * Processes a command package for a Snake game.
     * @param cp Command package containing game actions.
     * @return A Future holding the RenderingPackage after processing the commands.
     */
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        SnakeGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
}
