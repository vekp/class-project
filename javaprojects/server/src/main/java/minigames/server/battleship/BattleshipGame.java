package minigames.server.battleship;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 *
 */
public class BattleshipGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(BattleshipGame.class);

    static String player = "Nautical Map";
    static String enemy = "Target Map";
    static String chars = "ABCDEFGHI";

    static int WIDTH = 2;
    static int HEIGHT = 2;

//    static Map<String, Integer> vessels = new HashMap<String, Integer>() {{
//            vessels.put("Carrier", 6);
//            vessels.put("BattleShip", 5);
//            vessels.put("Destroyer", 4);
//            vessels.put("Submarine", 4);
//            vessels.put("Patrol Boat", 3);
//        }};

    record BattleshipPlayer(
            String name,
            int x, int y,
            List<String> inventory
    ) {
    }

    /** Uniquely identifies this game */
    String name;

    public BattleshipGame(String name) {
        this.name = name;
    }

    String[][] rooms = new String[][] {
            {
                    "You are in a maze of twisting passages, all alike",
                    "You are in a maze of twisting passages that weren't so alike after all"
            },
            {
                    "You are standing in an open field west of a white house, with a boarded front door. There is a small mailbox here.",
                    "You wake up. The room is very gently spinning around your head. Or at least it would be if you could see it which you can't. It is pitch black."
            }
    };

    static String welcomeMessage = "Welcome Captain! Enter 'Ready' to start conquering the seas!\nUse arrow keys to move ships" +
            " around the grid. Press 'Tab' to switch vessel and 'Space' to rotate.\n\n...Ready\n\nTo fire at the enemy, enter " +
            "grid coordinates: (eg, A,4)\n...D,7\n\nSalvo missed. Prepare for incoming fire!\nEnemy has hit our fleet, " +
            "Ship-Class:Carrier at coordinates [C,3]\n\nReturn fire! Enter grid coordinates:\n...";

    HashMap<String, BattleshipPlayer> players = new HashMap<>();

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Battleship", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(BattleshipGame.BattleshipPlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(welcomeMessage);

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
        BattleshipGame.BattleshipPlayer p = players.get(cp.player());

        // FIXME: Need to actually run the commands!

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));
        renderingCommands.add(new JsonObject().put("command", "setDirections").put("directions", directions(p.x(), p.y())));

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
            BattleshipPlayer p = new BattleshipPlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Battleship", "Battleship", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            renderingCommands.add(new JsonObject().put("command", "appendText").put("text", describeState(p)));
            renderingCommands.add(new JsonObject().put("command", "setDirections").put("directions", directions(p.x(), p.y())));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
