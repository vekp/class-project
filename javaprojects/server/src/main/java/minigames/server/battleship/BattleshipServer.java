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
import minigames.server.gameNameGenerator.GameNameGenerator;

import java.util.HashMap;
import java.util.Random;

/**
 * An enum containing all Achievements to be passed to the AchievementHandler for tracking
 */
enum achievements{
    FOUR_CORNERS, COSY_CONVOY, YOU_GOT_HIM, SLOW_LEARNER, C_120, HUNTER_KILLER, THREE_HOUR_CRUISE, THE_BIGGER_THEY_ARE,
    DESTROYER_DESTROYED, TITLE_DROP, MISSION_COMPLETE;

    /**
     * Sorry Nathan I'm not sure if this is the format needed by the handler
     * @return
     */
    @Override
    public String toString(){
        switch(this) {
            case FOUR_CORNERS:          return "Four Corners";
            case COSY_CONVOY:           return "Cosy Convoy";
            case YOU_GOT_HIM:           return "I Think You Got Him!";
            case SLOW_LEARNER:          return "Slow Learner";
            case C_120:                 return "COSC-120";
            case HUNTER_KILLER:         return "Hunter Killer";
            case THREE_HOUR_CRUISE:     return "Three Hour Cruise";
            case THE_BIGGER_THEY_ARE:   return "The Bigger They Are";
            case DESTROYER_DESTROYED:   return "Destroyer == Destroyed!";
            case TITLE_DROP:            return "Watership Down";
            case MISSION_COMPLETE:      return "Mission Complete";
            default:            return "Unknown Achievement";
        }
    }
}

public class BattleshipServer implements GameServer {
//    static final String chars = "abcdefghijklmopqrstuvwxyz";
    static GameNameGenerator gameNameGenerator;
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
                "Fire at a square that you've fired at already that contains nothing.", 10, "confused", true));
        achievementHandler.registerAchievement(new Achievement(achievements.C_120.toString(),
                "Crack a blue can of V. (Enter C120 as a target coordinate).", 50, "blue can", true));
        achievementHandler.registerAchievement(new Achievement(achievements.HUNTER_KILLER.toString(),
                "Destroy a submarine.", 10, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.THREE_HOUR_CRUISE.toString(),
                "Destroy a patrol boat.", 10, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.THE_BIGGER_THEY_ARE.toString(),
                "Destroy a carrier.", 10, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.DESTROYER_DESTROYED.toString(),
                "Destroy a destroyer.", 10, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.TITLE_DROP.toString(),
                "Destroy a battleship.", 10, "", false));
        achievementHandler.registerAchievement(new Achievement(achievements.MISSION_COMPLETE.toString(),
                "Destroy all enemy ships.", 100, "", false));

        gameNameGenerator = new GameNameGenerator("battleAdjectives", "battleNouns");
    }

    // A random name. We could do with something more memorable, like Docker has
    static String randomName() {
        return gameNameGenerator.randomName().replace("_", " ");
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
            if (games.get(name).getPlayerNames().length == 2 && !games.get(name).getPlayerNames()[1].equals("Computer")) {
                return new GameMetadata("Battleship", name, games.get(name).getPlayerNames(), false);
            }if (games.get(name).getPlayerNames().length < 2) {
                return new GameMetadata("Battleship", name, games.get(name).getPlayerNames(), false);
            }
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
//        if (cp.commands().get(0).getValue("command").equals("exitGame") && g.getPlayerNames().length == 0) games.remove(g.gameName);
        return Future.succeededFuture(g.runCommands(cp));
    }

}
