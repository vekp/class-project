package minigames.client.snake;

// Required imports
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.*;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;
import minigames.client.snake.*;

/**
 * The SnakeUI class provides the user interface for the snake game.
 * It implements the GameClient interface to handle game-related functionalities.
 */
public class SnakeUI implements GameClient {

    // Logger for logging events and issues
    private static final Logger logger = LogManager.getLogger(SnakeUI.class);

    // Network client to communicate with the game server
    MinigameNetworkClient mnClient;

    // Metadata about the game
    GameMetadata gm;

    // Player's name
    String player;

    // UI components
    JPanel mainMenuPanel;
    JPanel buttonsPanel;

    // UI buttons
    JButton startButton;
    JButton howToPlayButton;

    // Constants
    private static final String[] snakePlayerStrs = { "Player Name:" };

    /**
     * Default constructor for SnakeUI.
     */
    public SnakeUI() {
        // TODO: Initialization logic (if any)
    }

    /**
     * Sends a command to the game server.
     * Commands are JSON objects with the structure: { "command": command }
     *
     * @param command The command to be sent to the server.
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    /**
     * Handles the actions to be taken when the client is loaded into the main screen.
     *
     * @param mnClient The network client for communication.
     * @param game Game's metadata.
     * @param player Player's name.
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;

        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(StartMenu.startMenu());
        mnClient.getMainWindow().pack();
    }

    /**
     * Executes a command.
     *
     * @param game Game's metadata.
     * @param command The command to be executed.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        // TODO: Execution logic
    }

    /**
     * Handles the actions to be taken when closing the game.
     */
    @Override
    public void closeGame() {
        // Nothing to do for now
    }

}
