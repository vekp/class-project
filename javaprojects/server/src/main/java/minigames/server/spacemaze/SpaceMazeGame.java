package minigames.server.spacemaze;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual Space Maze game in progress
 *
 * @author Andrew McKenzie
 */
public class SpaceMazeGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(SpaceMazeGame.class);

    record SpacePlayer (
            String name
    ) {
    }

    /** String to uniquely identify this game*/
    String name;

    // The instance of MazeControl for this individual game
    MazeControl mazeControl;

    /**
     * Constructor
     * @param name to identify the individual game
     */
    public SpaceMazeGame(String name) {
        this.name = name;
        this.mazeControl = new MazeControl();
    }

    // Players in this game
    HashMap<String, SpacePlayer> players = new HashMap<>();


    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("SpaceMaze", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        SpacePlayer p = players.get(cp.player());

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String commandString = cp.commands().get(0).getValue("command");

        if (commandString.startsWith("key")) {
            String keyPressed = commandString.toLowerCase.replace("key", "");
            processKeyInput(keyPressed, p);
        }

        //TODO Any other to be recieved by our server

        // Do we use quitToMenu() in common/rendering/NativeCommands.java?

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Method to process the key inputs sent by the client
     * @param keyPressed a string representing the key that was pressed
     * @param player the player that sent the key
     */
    private void processKeyInput(String keyPressed, SpacePlayer player) {
        // Very verbose for now but it will get refactored a lot as the game develops
        // validMove is essentailly being called twice, But we need to check the move is valid first
        int[] pLoc = player.getLocation();
        int x = pLoc[0];
        int y = pLoc[1];

        switch (keyPressed) {
            case "up" -> {
                if (mazeControl.validMove(x, y-1) {
                    player.updateLocation(x, y-1);
                    mazeControl.updateLocation(x, y-1);
                }
            }
            case "down" -> if {
                (mazeControl.validMove(x, y+1) {
                    player.updateLocation(x, y+1);
                    mazeControl.updateLocation(x, y+1);
                }
            }
            case "left" -> {
                if (mazeControl.validMove(x-1, y) {
                    player.updateLocation(x-1, y);
                    mazeControl.updateLocation(x-1, y);
                }
            }
            case "right" -> {
                if (mazeControl.validMove(x+1, y) {
                    player.updateLocation(x+1, y);
                    mazeControl.updateLocation(x+1, y);
                }
            }
        }
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        // If playerName is already in the game, show error
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            // Create the new player with starting position
            SpacePlayer p = new SpacePlayer(playerName);
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();

            // TODO Any commands when a second player joins

            renderingCommands.add(new LoadClient("SpaceMazeGame", "SpaceMaze", name, playerName).toJson());
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
}
