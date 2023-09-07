package minigames.server.spacemaze;

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
 * Our SpaceMazeServer holds SpaceMazeGames.
 * When it receives a CommandPackage, it finds the SpaceMazeGame and calls it.
 *
 * @author Andrew McKenzie
 */


/**
 * Achievements API Implementation
 * @author Nik Olins
 */
enum achievements {
    DETERMINED_COLLECTOR, SEASONED_MAZE_RUNNER, FAST_AS_LIGHTNING;

    @Override
    public String toString() {
        switch(this) {
            case DETERMINED_COLLECTOR:
                return "Determined Collector";
            case SEASONED_MAZE_RUNNER:
                return "Seasoned Maze Runner";
            case FAST_AS_LIGHTNING:
                return "Fast As Lightning";
            default:
                return "Unknown Achievement";
        }
    }
}


public class SpaceMazeServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";

    // Achievements API integration
    AchievementHandler achievementHandler;
    public SpaceMazeServer() {
        achievementHandler = new AchievementHandler(SpaceMazeServer.class);

        achievementHandler.registerAchievement(new Achievement(achievements.DETERMINED_COLLECTOR.toString(), 
            "Clearing a single level of all items.", 500, "", true));

        achievementHandler.registerAchievement(new Achievement(achievements.SEASONED_MAZE_RUNNER.toString(), 
            "Finishing all levels without losing a single life.", 1000, "", false));

        achievementHandler.registerAchievement(new Achievement(achievements.FAST_AS_LIGHTNING.toString(), 
            "Collecting a chest to reset the elapsed time back to zero.", 500, "", false));

            
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
    HashMap<String, SpaceMazeGame> games = new HashMap<>();

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("SpaceMaze", "Race through the cosmic corridors");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            // Change to true when we go two player
            return new GameMetadata("SpaceMaze", name, games.get(name).getPlayerNames(), false);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        SpaceMazeGame g = new SpaceMazeGame(randomName());
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        SpaceMazeGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        SpaceMazeGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }

}