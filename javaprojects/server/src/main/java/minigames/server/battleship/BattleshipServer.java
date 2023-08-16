package minigames.server.battleship;

import io.vertx.core.Future;
import minigames.achievements.Achievement;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import minigames.server.achievements.AchievementHandler;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Random;

import static minigames.server.battleship.achievements.FOUR_CORNERS;

/**
 * An enum containing all Achievements to be passed to the AchievementHandler for tracking
 */
enum achievements{
    FOUR_CORNERS, COSY_CONVOY, YOU_GOT_HIM, SLOW_LEARNER, C_120, DOUBLE_TAP;

    /**
     * Sorry Nathan I'm not sure if this is the format needed by the handler
     * @return
     */
    @Override
    public String toString(){
        switch(this) {
            case FOUR_CORNERS:  return "Four Corners";
            case COSY_CONVOY:   return "Cosy Convoy";
            case YOU_GOT_HIM:   return "I Think You Got Him!";
            case SLOW_LEARNER:  return "Slow Learner";
            case C_120:         return "COSC-120";
            case DOUBLE_TAP:    return "Double Tap";
            default:            return "Unknown Achievement";
        }
    }
}

public class BattleshipServer implements GameServer {
    static final String chars = "abcdefghijklmopqrstuvwxyz";
    AchievementHandler achievementHandler;    // Nathan's Handler for implementing achievement features

    public BattleshipServer(){
        achievementHandler = new AchievementHandler(BattleshipServer.class);
        // Create the achievements and give them to the handler
        achievementHandler.registerAchievement(new Achievement(achievements.FOUR_CORNERS.toString(),
                "Place a ship at each corner of the game board.", 25, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.COSY_CONVOY.toString(),
                "Have each of your ships be touching at least one other ship.", 25, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.YOU_GOT_HIM.toString(),
                "Fire at a square containing an enemy ship that you have already hit.", 10, "", true));
        achievementHandler.registerAchievement(new Achievement(achievements.SLOW_LEARNER.toString(),
                "Fire at a square that you've fired at already that contains nothing.", 10, "", true));
        achievementHandler.registerAchievement(new Achievement(achievements.C_120.toString(),
                "Crack a blue can of V. (Enter C120 as a target coordinate).", 50, "blue can", true));
        achievementHandler.registerAchievement(new Achievement(achievements.DOUBLE_TAP.toString(),
                "Fire on an already destroyed vessel.", 10, "", false));

    }

    // A random name. We could do with something more memorable, like Docker has
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Add the game to a HashMap in memory containing all in-progress games
    HashMap<String, BattleshipGame> games = new HashMap<>();

    /**
     * Give the title and a description to the server for the game (to be displayed in the main menu)
     * @return A GameServerDetails object for the game
     */
    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Battleship", "Conquer the seas");
    }

    /**
     * Return the supported clients that can run the game
     * @return An Array containing the ClientTypes supported by this game
     */
    @Override
    public ClientType[] getSupportedClients() {
        // Should this not just be Swing?
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    /**
     * Return all information for in-progress games
     * @return An Array of GameMetadata objects for currently in-progress games
     */
    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Battleship", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    /**
     * Create a new game to be hosted on the server
     * @param playerName The name of the player creating the new game
     * @return A Future Object representing a successful game being joined (I think)
     */
    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        BattleshipGame g = new BattleshipGame(randomName(), playerName);
        games.put(g.returnGameName(), g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    /**
     * Join a game-in-progress on the server
     * @param game The name of the game
     * @param playerName The name of the player requesting to join
     * @return A Future Object representing a successful game being joined
     */
    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        BattleshipGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    /**
     * Call the game to update the current state / run any commands
     * @param cp The CommandPackage to be run
     * @return The state of the game after the command package has been run (if successful)
     */
    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        BattleshipGame g = games.get(cp.gameId());
        return Future.succeededFuture(g.runCommands(cp));
    }
    
}
