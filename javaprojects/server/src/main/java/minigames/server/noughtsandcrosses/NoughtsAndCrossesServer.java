package minigames.server.noughtsandcrosses;

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
 * Our NoughtsAndCrossesServer holds NoughtsAndCrossesGames.
 * When it receives a CommandPackage, it finds the NoughtsAndCrossesGame and calls it.
 */
public class NoughtsAndCrossesServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";

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
    HashMap<String, NoughtsAndCrossesGame> games = new HashMap<>();

    /*
    public NoughtsAndCrossesServer() {

    }*/

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("NoughtsAndCrosses", "Single Player Noughts and Crosses Game");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("NoughtsAndCrosses", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        NoughtsAndCrossesGame g = new NoughtsAndCrossesGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        NoughtsAndCrossesGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        NoughtsAndCrossesGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
