package minigames.server.peggle;

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
 * Our PeggleServer holds PeggleGames.
 * When it receives a CommandPackage, it finds the PeggleGame and calls it.
 */
public class PeggleServer implements GameServer {

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
    HashMap<String, PeggleGame> games = new HashMap<>();

    public PeggleServer() {
        achievementHandler = new AchievementHandler(PeggleServer.class);
        // Register all achievements with handler
        for (PeggleAchievement a : PeggleAchievement.values()) {
            achievementHandler.registerAchievement(a.achievement);
        }
    }
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Peggle", "One of the more peaceful things you can do with a cannon");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Peggle", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        PeggleGame g = new PeggleGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        PeggleGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        PeggleGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
