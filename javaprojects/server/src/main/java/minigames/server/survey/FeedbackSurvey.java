package minigames.server.survey;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Survey game in progress
 */
public class FeedbackSurvey {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(FeedbackSurvey.class);

    // static int WIDTH = 2;
    // static int HEIGHT = 2;

    record MuddlePlayer(
        String name,
        int x, int y,
        List<String> inventory
    ) {    
    }

    /** Uniquely identifies this game */
    String name;

    public FeedbackSurvey(String name) {
        this.name = name;
    }

    HashMap<String, MuddlePlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Survey", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(MuddlePlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append("Hello World");

        return sb.toString();
    }

    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);     
        MuddlePlayer p = players.get(cp.player());

        // FIXME: Need to actually run the commands!

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));

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
            MuddlePlayer p = new MuddlePlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("MuddleText", "Survey", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
    
}
