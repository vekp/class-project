package minigames.client.snake;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import java.util.Collections;

/**
 * The SnakeUI class provides an implementation for the GameClient interface.
 * It serves as the main interface between the Snake game client and the server,
 * handling actions such as loading the game, sending commands to the server, and executing server commands.
 */
public class SnakeUI implements GameClient {

    // Represents the name of the player
    String player;

    // Represents the main UI components and layout for the Snake game
    BasePanel basePanel;

    // Represents the client responsible for network communication with the game server
    MinigameNetworkClient mnClient;

    // Holds metadata related to the game, like server details and game name
    GameMetadata gameMetadata;

    /**
     * Default constructor for SnakeUI.
     */
    public SnakeUI() {
        // Constructor remains empty
    }

    /**
     * Loads the game UI and sets up the necessary client-server configurations.
     *
     * @param mnClient The network client responsible for communication with the game server.
     * @param game Holds metadata about the game, like server details and game name.
     * @param player Represents the name of the player.
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gameMetadata = game;
        this.player = player;

        // Initialize the base UI panel for the Snake game
        this.basePanel = new BasePanel(mnClient, this::closeGame);

        // Clear previous game window contents and set up the new game UI
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(basePanel.getContainerPanel());
        mnClient.getMainWindow().pack();
    }

    /**
     * Sends a command to the game server. The command structure is a JSON object: { "command": command }
     *
     * @param command The specific command string to be sent to the server.
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        mnClient.send(new CommandPackage(gameMetadata.gameServer(), gameMetadata.name(), player, Collections.singletonList(json)));
    }

    /**
     * Executes a server command. This is where the logic for handling server responses should be implemented.
     *
     * @param game Holds metadata about the game.
     * @param command The specific JSON command sent by the server.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gameMetadata = game;
        // TODO: Implement logic to handle server commands
    }

    /**
     * Provides logic for safely closing and cleaning up the game when the player exits.
     */
    public void closeGame() {
        // TODO: Add cleanup actions here
        mnClient.runMainMenuSequence();
    }
}
