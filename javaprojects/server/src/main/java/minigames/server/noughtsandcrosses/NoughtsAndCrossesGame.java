package minigames.server.noughtsandcrosses;

import java.util.*;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Muddle game in progress
 */
public class NoughtsAndCrossesGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(NoughtsAndCrossesGame.class);

    /** Uniquely identifies this game */
    String name;
    String playerName;

    record NoughtsAndCrossesPlayer(
            String name,
            int x, int y,
            List<String> inventory
    ) {
    }

    public NoughtsAndCrossesGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;

    };

    HashMap<String, NoughtsAndCrossesPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("NoughtsAndCrosses", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        NoughtsAndCrossesPlayer p = players.get(cp.player());

        String userInput = String.valueOf(cp.commands().get(0).getValue("command"));

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

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
            NoughtsAndCrossesPlayer p = new NoughtsAndCrossesPlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("NoughtsAndCrosses", "NoughtsAndCrosses", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}
