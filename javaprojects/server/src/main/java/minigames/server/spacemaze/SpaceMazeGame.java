package minigames.server.spacemaze;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.achievements.Achievement;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.server.achievements.AchievementHandler;
import static minigames.server.spacemaze.achievements.*;

import java.awt.Point;

import minigames.server.achievements.AchievementHandler;
import static minigames.server.spacemaze.achievements.*;

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

    //HighScoreAPI api = new HighScoreAPI(new DerbyDatabase());

    //List<ScoreRecord> topScores;

    /**
     * Constructor
     * @param name to identify the individual game
     */
    public SpaceMazeGame(String name) {
        this.name = name;
        this.player = new SpacePlayer(new Point(1,0), 5);
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

        // Lets mazeControl keep track of the player for when client sends updateMaze
        if (commandString.startsWith("playerMoved")) {
            String keyPressed = commandString;
            processKeyInput(keyPressed, p);
        } else {
            switch (commandString) {
                case "START" -> renderingCommands.add(new JsonObject().put("command", "startGame"));
                case "SCORE" -> renderingCommands.add(new JsonObject().put("command", "viewHighScore"));
                case "MENU" -> renderingCommands.add(new JsonObject().put("command", "mainMenu"));
                case "HELP" -> renderingCommands.add(new JsonObject().put("command", "howToPlay"));
                case "backToMenu" -> renderingCommands.add(new JsonObject().put("command", "backToMenu"));
                case "collision" -> {
                    JsonObject serializedMazeArray = new JsonObject()
                            .put("command", "updateMaze")
                            .put("mazeArray", serialiseNestedCharArray(mazeControl.getMazeArray()));
                    switch (mazeControl.getNextTile()) {
                        case 'M' -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.DYNAMITE.toString());
                        case 'H' -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.WORM_HOLE.toString());
                        case '$' ->  {
                                boolean awardFastAsLightning = mazeControl.awardFastAsLightning();
                                if(awardFastAsLightning) { 
                                    AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                                    handler.unlockAchievement(getPlayerNames()[0],FAST_AS_LIGHTNING.toString());
                                }
                                serializedMazeArray.put("interactiveResponse", InteractiveResponses.TREASURE_CHEST.toString()); 
                            }
                        case 'K' -> {
                            int keysRemaining = mazeControl.getKeysRemaining();
                            switch (keysRemaining) {
                                case 0 -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.ALL_KEY_OBTAINED.toString());
                                case 1 -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.ONE_KEY_REMAINING.toString());
                                case 2 -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.TWO_KEY_REMAINING.toString());
                                case 3 -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.THREE_KEY_REMAINING.toString());
                                case 4 -> serializedMazeArray.put("interactiveResponse", InteractiveResponses.FOUR_KEY_REMAINING.toString());
                            }
                        }
                    }
                    renderingCommands.add(serializedMazeArray);
                }
                case "botCollision" -> {
                    // For testing triggering of achievements.
                    /*
                    String playerName = getPlayerNames()[0];
                    *AchievementHandler thisHandler = new AchievementHandler(SpaceMazeServer.class);
                    thisHandler.unlockAchievement(playerName, DETERMINED_COLLECTOR.toString());
                     * 
                     */

                    int playerLives = player.getLives();
                    if (playerLives > 0) {
                        player.removeLife(1);
                        playerLives = player.getLives();
                        if (playerLives > 0) {
                            renderingCommands.add(new JsonObject().put("command", "playerLives")
                                    .put("lives", playerLives)
                                    .put("interactiveResponse", InteractiveResponses.BOTS_COLLISION.toString()));
                        } else {
                            int time = mazeControl.timeTaken;
                            int minutes = time / 60;
                            int seconds = time % 60;
                            String timeTaken = String.format("%d:%02d", minutes, seconds);
                            mazeControl.playerDead();
                            renderingCommands.add(new JsonObject().put("command", "playerDead")
                                .put("timeTaken", timeTaken)
                                .put("level", Integer.toString(mazeControl.getCurrentLevel()))
                            );
                        }
                    }
                }
                case "gameTimer" -> {
                    String currentTime = mazeControl.mazeTimer.getCurrentTime();
                    renderingCommands.add(new JsonObject().put("command", "timer")
                            .put("time", currentTime));
                }
                case "requestGame" -> {
                    // Moved here from the constructor to only start the timer on maze load
                    player.resetPlayer();
                    this.mazeControl = new MazeControl(this.player);
                    mazeControl.playerEntersMaze(new Point(1, 0));
                    JsonObject serializedMazeArray = new JsonObject()
                            .put("command", "firstLevel")
                            .put("mazeArray", serialiseNestedCharArray(mazeControl.getMazeArray()))
                            .put("botStartLocations", mazeControl.getBotStartLocations())
                            .put("playerLives", player.getLives())
                            .put("interactiveResponse", InteractiveResponses.GAME_STARTED.toString());
                    renderingCommands.add(serializedMazeArray);
                }
                case "onExit" -> {
                    mazeControl.newLevel();
                    player.calculateScore(mazeControl.timeTaken, 8000);
                    String playerScoreString = String.valueOf(player.getPlayerScore());
                    if (!mazeControl.isGameFinished()) {
                        JsonObject serializedMazeArray = new JsonObject()
                                .put("command", "nextLevel")
                                .put("mazeArray", serialiseNestedCharArray(mazeControl.getMazeArray()))
                                .put("botStartLocations", mazeControl.getBotStartLocations())
                                .put("totalScore", playerScoreString)
                                .put("level", Integer.toString(mazeControl.getCurrentLevel()))
                                .put("interactiveResponse", InteractiveResponses.NEW_LEVEL.toString());
                        renderingCommands.add(serializedMazeArray);
                        // Set level achievements
                        Boolean isPlayerADeterminedCollector = mazeControl.getLevelAllKeyBonusStatus();
                        if (isPlayerADeterminedCollector) {
                            AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                            handler.unlockAchievement(getPlayerNames()[0],DETERMINED_COLLECTOR.toString());
                        }
                    } else {
                        int time = mazeControl.timeTaken;
                        int minutes = time / 60;
                        int seconds = time % 60;
                        String timeTaken = String.format("%d:%02d", minutes, seconds);
                        renderingCommands.add(new JsonObject().put("command", "gameOver")
                                .put("totalScore", playerScoreString)
                                .put("timeTaken", timeTaken));
                        //api.recordScore(p, "SpaceMaze", player.getPlayerScore());

                        // Set endgame achievements
                        Boolean isPlayerATimeLord = mazeControl.getAllBonusStatus();
                        Boolean isPlayerAKeyKeeper = mazeControl.getAllKeysStatus();
                        if (isPlayerATimeLord) {
                            AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                            handler.unlockAchievement(getPlayerNames()[0],TIME_LORD.toString());
                        }
                        if (isPlayerAKeyKeeper) {
                            AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                            handler.unlockAchievement(getPlayerNames()[0],KEEPER_OF_THE_KEYS.toString());
                        }
                        if (isPlayerAKeyKeeper && isPlayerATimeLord) {
                            AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                            handler.unlockAchievement(getPlayerNames()[0],THE_COLLECTORS_COLLECTION.toString());
                        }

                        // TODO Nik - implement seasoned maze runner achievement.
                        Boolean isPlayerASeasonedMazeRunner = !player.hasLostLives();
                        if(isPlayerASeasonedMazeRunner) {
                            AchievementHandler handler = new AchievementHandler(SpaceMazeServer.class);
                            handler.unlockAchievement(getPlayerNames()[0],SEASONED_MAZE_RUNNER.toString());
                        }
                        // TODO Nik - implement fast as lightning achievement.
                    }
                }
            }
        }
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /**
     * Method to break down a char[][] array to be sent as
     * a JSON object
     * @param mazeArray char[][]
     * @return List of Strings for each row in the maze
     */
    private List<String> serialiseNestedCharArray(char[][] mazeArray) {
        List<String> serialisedMaze = new ArrayList<>();
        for (int i = 0; i < mazeArray.length; i++) {
            serialisedMaze.add(new String(mazeArray[i]));
        }
        return serialisedMaze;
    }

    /**
     * Method to process the key inputs sent by the client
     * @param keyPressed a string representing the key that was pressed
     * @param player the player that sent the key
     */
    private void processKeyInput(String keyPressed, SpacePlayer player) {

        player.updateLocation(mazeControl.getPlayerLocationInMaze(player));

        // Point locations to check
        Point ploc = new Point(player.getLocation());
        Point uploc = new Point(ploc.x, ploc.y-1);
        Point downloc = new Point(ploc.x, ploc.y+1);
        Point leftloc = new Point(ploc.x-1, ploc.y);
        Point rightloc = new Point(ploc.x+1, ploc.y);

        switch (keyPressed) {
            case "playerMovedUp":
                player.updateLocation(uploc);
                mazeControl.updatePlayerLocationMaze(player,uploc);
                break;
            case "playerMovedDown":
                player.updateLocation(downloc);
                mazeControl.updatePlayerLocationMaze(player, downloc);
                break;
            case "playerMovedLeft":
                player.updateLocation(leftloc);
                mazeControl.updatePlayerLocationMaze(player, leftloc);
                break;
            case "playerMovedRight":
                player.updateLocation(rightloc);
                mazeControl.updatePlayerLocationMaze(player, rightloc);
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
            SpacePlayer p = new SpacePlayer(new Point(1, 0), 5);
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();

            // TODO Any commands when a second player joins

            renderingCommands.add(new LoadClient("SpaceMaze", "SpaceMaze", name, playerName).toJson());
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }
}