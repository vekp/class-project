package minigames.server.memory;

import io.vertx.core.json.JsonObject;
import java.util.*;

import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents an actual Memory game in progress.
 * Used and adapted MuddleGame.java
 */
public class MemoryGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(MemoryGame.class);

    // Create a record of MemoryPlayer class
    record MemoryPlayer(String name) {}

    /** Uniquely identifies this game */
    String name;

    public MemoryGame(String name) {
        this.name = name;
    }

    // Players
    HashMap<String, MemoryPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Memory", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);

        // Create temp JSONObject object
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        JsonObject cmd = cp.commands().get(0);
        switch (cmd.getString("command")) {
            case "exitGame" -> {
                players.remove(cp.player());
                renderingCommands.add(new JsonObject().put("command", "showGames"));
            }
        }

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                gameMetadata(),
                Arrays.stream(new RenderingCommand[] {
                    new NativeCommands.ShowMenuError("That name's not available")
                }).map((r) -> r.toJson()).toList()
            );
        } else {
            MemoryPlayer p = new MemoryPlayer(playerName);
            players.put(playerName, p);

            // Create temp JSONObject object
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Memory", "Memory", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }

}