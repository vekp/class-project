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

    SpacePlayer player;

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
        this.player = new SpacePlayer(1,0);
        players.put(name, this.player);
    }

    // Players in this game
    HashMap<String, SpacePlayer> players = new HashMap<>();

    /**
     * Getter to get the names of all players currently playing this game
     * @return an array of the names
     */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /**
     * Metadata for this instance of the game
     * @return A GameMetadata object
     */
    public GameMetadata gameMetadata() {
        return new GameMetadata("SpaceMaze", name, getPlayerNames(), true);
    }

    /**
     * Runs the commands sent by the client to the SpaceMazeServer
     * @param cp Command Packet Object with commands
     * @return Rendering Package with commands
     */
    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);
        SpacePlayer p = players.get(cp.player());

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String commandString = (String) cp.commands().get(0).getValue("command");

        if (commandString.startsWith("key")) {
            String keyPressed = commandString.toLowerCase().replace("key", "");
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
            case "up":
                if (mazeControl.validMove(x, y-1)) {
                    player.updateLocation(x, y-1);
                    mazeControl.updatePlayerLocationMaze(player, x, y-1);
                }
                break;
            case "down":
                if (mazeControl.validMove(x, y+1)) {
                    player.updateLocation(x, y+1);
                    mazeControl.updatePlayerLocationMaze(player, x, y+1);
                }
                break;
            case "left":
                if (mazeControl.validMove(x-1, y)) {
                    player.updateLocation(x-1, y);
                    mazeControl.updatePlayerLocationMaze(player, x-1, y);
                }
                break;
            case "right":
                if (mazeControl.validMove(x+1, y)) {
                    player.updateLocation(x+1, y);
                    mazeControl.updatePlayerLocationMaze(player, x+1, y);
                }
                break;
        }
    }

    /**
     * To allow more players to join a current game in progress
     * @param playerName The name of the player to join
     * @return RenderingPackage of the game with or without the new player
     */
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
            SpacePlayer p = new SpacePlayer(1, 0);
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();

            // TODO Any commands when a second player joins

            renderingCommands.add(new LoadClient("SpaceMazeGame", "SpaceMaze", name, playerName).toJson());
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}