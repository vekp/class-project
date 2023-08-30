package minigames.server.memory;

import io.vertx.core.json.JsonObject;
import java.util.*;

import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.achievements.AchievementHandler;
import static minigames.server.memory.MemoryAchievement.*;
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
    String playerName;
    AchievementHandler achievementHandler;
    String cardToShow = "_2_of_Clubs.png";

    public MemoryGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
        this.achievementHandler = new AchievementHandler(MemoryServer.class);

        // Unlock TEST_THAT_MEMORY achievement for starting a new game
        achievementHandler.unlockAchievement(playerName, TEST_THAT_MEMORY.toString());
    }

    /** Achievement handler for this game */
    // Code snippet from AchievementHandler.java
    // private static final

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
            case "Flip_Card_1" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_1"));
            }
            case "Flip_Card_2" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_2"));
            }
            case "Flip_Card_3" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_3"));
            }
            case "Flip_Card_4" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_4"));
            }
            case "Flip_Card_5" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_5"));
            }
            case "Flip_Card_6" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_6"));
            }
            case "Flip_Card_7" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_7"));
            }
            case "Flip_Card_8" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_8"));
            }
            case "Flip_Card_9" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_9"));
            }
            case "Flip_Card_10" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_10"));
            }
            case "Flip_Card_11" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_11"));
            }
            case "Flip_Card_12" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_12"));
            }
            case "Flip_Card_13" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_13"));
            }
            case "Flip_Card_14" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_14"));
            }
            case "Flip_Card_15" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_15"));
            }
            case "Flip_Card_16" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_16"));
            }
            case "Flip_Card_17" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_17"));
            }
            case "Flip_Card_18" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_18"));
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