package minigames.server.gameshow;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual GameShow game in progress
 */
public class GameShow {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(GameShow.class);

    static int WIDTH = 2;
    static int HEIGHT = 2;

    record GameShowPlayer(
        String name,
        int x, int y,
        List<String> inventory
    ) {
    }

    /** Uniquely identifies this game */
    String name;

    public GameShow(String name) {
        this.name = name;
    }

    HashMap<String, GameShowPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("GameShow", name, getPlayerNames(), true);
    }



    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        GameShowPlayer p = players.get(cp.player());

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

         // Add buttons to the rendering commands
         renderingCommands.add(new JsonObject().put("command", "imageGame"));

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
            GameShowPlayer p = new GameShowPlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("GameShow", "GameShow", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "imageGame"));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}
