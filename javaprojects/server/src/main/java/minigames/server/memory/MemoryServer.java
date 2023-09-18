package minigames.server.memory;

import io.vertx.core.Future;

import java.util.HashMap;
import java.util.Random;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.achievements.AchievementHandler;

/**
 * Our MemoryServer holds MemoryGame. 
 * When it receives a CommandPackage, it finds the MemoryGame and calls it.
 * Used and adapted MuddleServer.java
 */

public class MemoryServer implements GameServer {
    
    static final String chars = "abcdefghijklmopqrstuvwxyz";
    AchievementHandler achievementHandler;

    /** A random name. We could do with something more memorable, like Docker has. */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Holds the game in progress in memory (no db). */
    HashMap<String, MemoryGame> games = new HashMap<>();
    public MemoryServer() {
        achievementHandler = new AchievementHandler(MemoryServer.class);
        // Register all achievements with handler
        for (MemoryAchievement a : MemoryAchievement.values()) {
            achievementHandler.registerAchievement(a.achievement);
        }
    }

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Memory", "Find the pairs in Pair Up [Memory card game]!");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Memory", name, games.get(name).getPlayerNames(), false); // true for multiplayer
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        MemoryGame g = new MemoryGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        MemoryGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        MemoryGame g = games.get(cp.gameId());

        return Future.succeededFuture(g.runCommands(cp));
    }

}