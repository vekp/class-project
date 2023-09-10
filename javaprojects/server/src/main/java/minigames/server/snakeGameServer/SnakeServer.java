package minigames.server.snakeGameServer;

import io.vertx.core.Future;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.achievements.AchievementHandler;

import java.util.HashMap;
import java.util.Random;

/**
 * Our MuddleServer holds MuddleGames. 
 * When it receives a CommandPackage, it finds the MuddleGame and calls it.
 */
public class SnakeServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";
    AchievementHandler achievementHandler;

    /** A random name. We could do with something more memorable, like Docker has */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Holds the games in progress in memory (no db) */
    HashMap<String, SnakeGame> games = new HashMap<>();

    public SnakeServer() {
        achievementHandler = new AchievementHandler(SnakeServer.class);
        // Register all achievements with handler
        for (SnakeAchievement a : SnakeAchievement.values()) {
            achievementHandler.registerAchievement(a.achievement);
        }
    }
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Snake", "It would be a SNK, but it's not really written yet");
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
        SnakeGame g = new SnakeGame(randomName(), playerName);
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
