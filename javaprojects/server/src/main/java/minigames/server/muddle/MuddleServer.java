package minigames.server.muddle;

import io.vertx.core.Future;
import minigames.achievements.Achievement;
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
public class MuddleServer implements GameServer {

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
    HashMap<String, MuddleGame> games = new HashMap<>();

    public MuddleServer() {
        achievementHandler = new AchievementHandler(MuddleServer.class);
        // Register all achievements with handler
        for (MuddleAchievement a : MuddleAchievement.values()) {
            achievementHandler.registerAchievement(new Achievement(a.toString(), a.description, 25, "", a.hidden));
        }
    }
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Muddle", "It would be a MUD, but it's not really written yet");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Muddle", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        MuddleGame g = new MuddleGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        MuddleGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        MuddleGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
