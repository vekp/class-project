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

    static Board player1 = new Board("Mitcho");
    static Board player2 = new Board("Craig");


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

    static String welcomeMessage = "Welcome Captain! Enter 'Ready' to start conquering the seas!\nUse arrow keys to move ships" +
            " around the grid. Press 'Tab' to switch vessel and 'Space' to rotate.\n...";

//    Ready\n\nTo fire at the enemy, enter " +
//            "grid coordinates: (eg, A,4)\n...D,7\n\nSalvo missed. Prepare for incoming fire!\nEnemy has hit our fleet, " +
//            "Ship-Class:Carrier at coordinates [C,3]\n\nReturn fire! Enter grid coordinates:\n...";

    HashMap<String, BattleshipPlayer> players = new HashMap<>();

    /**
     * Returns the names of the players currently playing the game
     * @return An array containing the names of current players
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /**
     * Return the meta-data for the in-progress game
     * @return a GameMetadata Object containing the information for the current game
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Battleship", name, getPlayerNames(), true);
    }

    /**
     * Returns the current state of the requested player, takes a BattleShipPlayer Object as a parameter
     * @param p The player whose state is to be described
     * @return The String to be displayed to the player
     */
    private String describeState(BattleshipPlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(welcomeMessage);

        return sb.toString();
    }

    /**
     * Run the commands received from the BattleshipServer class
     * @param cp The CommandPackage Object containing the commands to be run
     * @return The information to render in the client
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        BattleshipPlayer p = players.get(cp.player());

        // FIXME: Need to actually run the commands!

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "clearText"));
        renderingCommands.add(new JsonObject().put("command", "appendText").put("text", cp.commands().get(0).getValue("command")));
        renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.defaultGridCreator())));
        renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.defaultGridCreator())));
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Joins an in-progress game taking the player name as a parameter. Returns the rendering package to display the
     * game in its current state
     * NOTE: We'll be adding in some logic to check the number of players and other conditions to not open / join a game
     * @param playerName The name of the player making the request to join a game
     * @return The RenderingPackage for the game in its current (or new) state
     */
    public RenderingPackage joinGame(String playerName) {
        // Don't allow a player to join if the player's name is already taken
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
            renderingCommands.add(new JsonObject().put("command", "placePlayer1Board").put("text", Board.generateBoard(player, player1.defaultGridCreator())));
            renderingCommands.add(new JsonObject().put("command", "placePlayer2Board").put("text", Board.generateBoard(enemy, player2.defaultGridCreator())));


            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
