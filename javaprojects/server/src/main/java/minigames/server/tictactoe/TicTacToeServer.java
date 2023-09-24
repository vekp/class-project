package minigames.server.tictactoe;

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
 * Our TicTacToeServer holds TicTacToeGames.
 * When it receives a CommandPackage, it finds the TicTacToeGame and calls it.
 */
public class TicTacToeServer implements GameServer {

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
    HashMap<String, TicTacToeGame> games = new HashMap<>();

    public TicTacToeServer() {
        achievementHandler = new AchievementHandler(TicTacToeServer.class);
        // Register all achievements with handler
        for (TicTacToeAchievement a : TicTacToeAchievement.values()) {
            achievementHandler.registerAchievement(a.achievement);
        }
    }

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("TicTacToe", "The classic game of TicTacToe!");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("TicTacToe", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        TicTacToeGame g = new TicTacToeGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        TicTacToeGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        TicTacToeGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
