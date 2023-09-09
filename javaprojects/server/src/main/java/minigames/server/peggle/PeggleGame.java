package minigames.server.peggle;

import java.util.*;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;


/**
 * Represents an actual Peggle game in progress
 */
public class PeggleGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(PeggleGame.class);

    record PegglePlayer(
            String name,
            int x, int y,
            List<String> inventory
    ) {
    }

    /** Uniquely identifies this game */
    String name;
    AchievementHandler achievementHandler;
    String playerName;

    public PeggleGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
        this.achievementHandler = new AchievementHandler(PeggleServer.class);

    }

    HashMap<String, PegglePlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {return players.keySet().toArray(String[]::new);}

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Peggle", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(PegglePlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%d,%d] \n\n", p.x, p.y));

        return sb.toString();
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        PegglePlayer p = players.get(cp.player());

        String userInput = String.valueOf(cp.commands().get(0).getValue("command"));

        // Unlock achievements
//        switch (userInput.toLowerCase().strip()) {
//            case "east" -> achievementHandler.unlockAchievement(playerName, EAST_BUTTON_PUSHER.achievement.name());
//            case "south" -> achievementHandler.unlockAchievement(playerName, SOUTH_BUTTON_PUSHER.achievement.name());
//            case "abracadabra" -> achievementHandler.unlockAchievement(playerName, SAY_THE_MAGIC_WORD.achievement.name());
//        }

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "startGame"));
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
            PegglePlayer p = new PegglePlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Peggle", "Peggle", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}
