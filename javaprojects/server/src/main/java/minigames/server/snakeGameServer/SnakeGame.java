package minigames.server.snakeGameServer;

import java.util.*;

import minigames.server.achievements.AchievementHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

import static minigames.server.snakeGameServer.SnakeAchievement.*;


/**
 * Represents an actual Muddle game in progress
 */
public class SnakeGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(SnakeGame.class);

    static int WIDTH = 2;
    static int HEIGHT = 2;

    record SnakePlayer(
        String name,
        int x, int y,
        List<String> inventory
    ) {    
    }

    /** Uniquely identifies this game */
    String name;
    AchievementHandler achievementHandler;
    String playerName;

    public SnakeGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
        this.achievementHandler = new AchievementHandler(SnakeServer.class);

        // Unlock Muddler achievement for starting a new game
        achievementHandler.unlockAchievement(playerName, MUDDLER.toString());
    }

    String[][] rooms = new String[][] {
        {
           "Welcome to Snake Game"
        },
        {
            "You are standing in an open field west of a white house, with a boarded front door. There is a small mailbox here.",
            "You wake up. The room is very gently spinning around your head. Or at least it would be if you could see it which you can't. It is pitch black."
        }
    };

    HashMap<String, SnakePlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Snake", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(SnakePlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%d,%d] \n\n", p.x, p.y));
        sb.append(rooms[p.x()][p.y()]);

        return sb.toString();
    }

    private String directions(int x, int y) {
        String d = "";
        if (x > 0) d += "W";
        if (y > 0) d += "N";
        if (x < WIDTH - 1) d += "E";
        if (x < HEIGHT - 1) d += "S";

        return d;
    }


    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);     
        SnakePlayer p = players.get(cp.player());

        // FIXME: Need to actually run the commands!
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String commandString = String.valueOf(cp.commands().get(0).getValue("command"));

        switch (commandString){
            case "START" -> renderingCommands.add(new JsonObject().put("command", "startGame"));
            case "SCORE" -> renderingCommands.add(new JsonObject().put("command", "viewHighScore"));
            case "HELP" -> renderingCommands.add(new JsonObject().put("command", "howToPlay"));
            case "BACK" -> renderingCommands.add(new JsonObject().put("command", "backToMenu"));
            case "GAME" -> renderingCommands.add(new JsonObject().put("command", "game"));
            case "EXIT" -> {
                
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
            SnakePlayer p = new SnakePlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Snake", "Snake", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("NEW", "new"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));
            renderingCommands.add(new JsonObject().put("command", "setDirections").put("directions", directions(p.x(), p.y())));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
    
}
