package minigames.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import minigames.server.battleship.BattleshipServer;
import minigames.server.highscore.*;
import minigames.server.muddle.MuddleServer;
import minigames.server.telepathy.TelepathyServer;
import io.vertx.core.Launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for starting up the game server.
 * <p>
 * This is shaped a little by things that Vertx needs to start itself up
 */
public class Main extends AbstractVerticle {

    /**
     * A logger for logging output
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Port to start the server on.
     * This is a little hacky, but when we run the server, we just put the port
     * argument into this static field
     * so that the GameServer's start method can pick it up later
     */
    public static int port = 8080;

    /**
     * The games that are available for each client. Static so that game servers and
     * Main gan register them
     * without needing to worry about whether the server has started yet.
     */
    public static final GameRegistry gameRegistry = new GameRegistry();

    /**
     * HighScoreAPI static reference (INCOMPLETE don't try to use yet)
     * Provides access to high-score management and retrieval functionalities
     * for all game components via `Main.highScoreAPI`.
     */
    public static HighScoreAPI highScoreAPI = new HighScoreAPI();

    //todo replace this with a proper user profile/account system
    /**
     * This is a makeshift 'player database' holding currently-known players (basically just names that
     * have been entered into the client to join a game, plus a few default names for filling out the achievement
     * window). THIS SHOULD BE REPLACED WITH THE USER PROFILE SYSTEM WHEN READY
     */
    public static Set<String> players = new HashSet<>();


    /**
     * A place for groups to put code that registers their GameServer with the
     * GameRegistry, etc.
     */
    private static void doWiring() {
        // Register our first demo game
        gameRegistry.registerGameServer("Muddle", new MuddleServer());
        gameRegistry.registerGameServer("Battleship", new BattleshipServer());
        gameRegistry.registerGameServer("Telepathy", new TelepathyServer());

        //adding some dummy/default names to the player list
        players.add("James");
        players.add("Sarah");
        players.add("Andrew");
        players.add("Amy");
        players.add("Robert");
        players.add("Georgia");
    }

    public static void main(String... args) {
        if (args.length > 0) {
            try {
                int p = Integer.parseInt(args[0]);
                if (p >= 1024 && p <= 49151) {
                    port = p;
                } else {
                    logger.error("Port {} is outside the user port range of 1024 to 49151", args[0]);
                }
            } catch (NumberFormatException ex) {
                logger.error("Port {} could not be parsed as a number", args[0]);
            }
        }

        // Register games and services
        doWiring();

        // Ask the Vertx launcher to launch our "Verticle".
        // This will cause Vert.x to start itself up, and then create a Main object and
        // call our Main::start method
        logger.info("About to launch the server");
        Launcher.executeCommand("run", "minigames.server.Main");
    }

    MinigameNetworkServer gameServer;

    /**
     * The start method is called by vertx to initialise this Verticle.
     */
    @Override
    public void start(Promise<Void> promise) {
        logger.info("Our Verticle is being started by Vert.x");
        gameServer = new MinigameNetworkServer(vertx);
        gameServer.start(port);
    }

}