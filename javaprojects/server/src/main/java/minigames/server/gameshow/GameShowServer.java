package minigames.server.gameshow;

import minigames.server.gameshow.GameShow;
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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Our GameShowServer holds GameShows.
 * When it receives a CommandPackage, it finds the GameShow and calls it.
 */
public class GameShowServer implements GameServer {

    AchievementHandler achievementHandler;
    static final String chars = "abcdefghijklmopqrstuvwxyz";

    public GameShowServer() {
        achievementHandler = new AchievementHandler(GameShowServer.class);
       // Create the achievements and give them to the handler
        achievementHandler.registerAchievement(new Achievement(achievements.WORD_SCRAMBLE.toString(),
        "Play your first Word Scramble game.", 25, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.IMAGE_GUESSER.toString(),
        "Play your first word Image Guesser game.", 25, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.FIRST_CORRECT.toString(),
        "Get something right.", 10, "", true));
        achievementHandler.registerAchievement(new Achievement(achievements.FIRST_INCORRECT.toString(),
        "Don't get something right.", 10, "", true));
        achievementHandler.registerAchievement(new Achievement(achievements.MEMORY.toString(),
        "Play your first word Memory game.", 50, "", true));
    }

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
    HashMap<String, GameShow> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("GameShow", "It's a game show!");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("GameShow", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        GameShow g = new GameShow(randomName());
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        GameShow g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        GameShow g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}
