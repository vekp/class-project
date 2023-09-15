package minigames.client.snake;

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
 * A very simple interface for a snake game.
 *
 */
public class SnakeGameText implements GameClient {
    private static final Logger logger = LogManager.getLogger(SnakeGameText.class);

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;

    /** Player name */
    String player;


    JPanel mainMenuPanel;
    JPanel buttonsPanel;

    // Buttons
    JButton startButton;
    JButton howToPlayButton;


    private static final String[] snakePlayerStrs = { "Player Name:" };


    // Menu
    public SnakeGameText() {
    }

    /**
     * Sends a command to the game at the server.
     * This being a text adventure, all our commands are just plain text strings our gameserver will interpret.
     * We're sending these as
     * { "command": command }
     */
    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }


    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;


        // We're now passing the component into the addCenter method of the getMainWindow() method of the MinigameNetworkClient
        // On the right track but new window still opening up.
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(StartMenu.startMenu());
        mnClient.getMainWindow().pack();

    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}
