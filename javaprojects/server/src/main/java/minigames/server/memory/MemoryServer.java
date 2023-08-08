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

/**
 * Our MemoryServer holds MemoryGame. 
 * When it receives a CommandPackage, it finds the MemoryGame and calls it.
 * Used and adapted MuddleServer.java
 */
public class MemoryServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";

    /** Holds the game in progress in memory (no db). */
    HashMap<String, MemoryGame> games = new HashMap<>();

    /** Random name */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Memory", "Find the pairs in the Memory card game!");
    }

    // Only supports Java Swing
    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Memory", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        MemoryGame g = new MemoryGame(randomName());
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