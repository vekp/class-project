package minigames.server.hangman;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;


/**
 * Represents an actual Muddle game in progress
 */
public class HangmanGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(HangmanGame.class);

    record HangmanPlayer(String name, int x, int y, List<String> inventory) {}

    String[][] status = new String[][]{
        {"Hangman server demo"},
    };

    HashMap<String, HangmanPlayer> players = new HashMap<>();

    public HangmanGame(String name) { 
        this.name = name;   
    }

    /** Uniquely identifies this game */
    String name;
    String playerName;

    public HangmanGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
    }

    String[][] rooms = new String[][] {
        {
           "Welcome to Hangman"
        }
    };

   

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Hangman", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(HangmanPlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%d,%d] \n\n", p.x, p.y));
        sb.append(rooms[p.x()][p.y()]);

        return sb.toString();
    }

    
    public RenderingPackage runCommands(CommandPackage cp) {   
        logger.info("Received command package {}", cp);     
        HangmanPlayer p = players.get(cp.player());

        // FIXME: Need to actually run the commands!

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String commandString = String.valueOf(cp.commands().get(0).getValue("command"));

        switch (commandString){
            case "START" -> renderingCommands.add(new JsonObject().put("command", "startGame"));
            case "SCORE" -> renderingCommands.add(new JsonObject().put("command", "viewHighScore"));
            case "HELP" -> renderingCommands.add(new JsonObject().put("command", "howToPlay"));
            case "BACK" -> renderingCommands.add(new JsonObject().put("command", "backToMenu"));
            case "GAME" -> renderingCommands.add(new JsonObject().put("command", "game"));
            case "NEW" -> renderingCommands.add(new JsonObject().put("command", "new"));
            case "PLAY" -> renderingCommands.add(new JsonObject().put("command", "play"));
            case "STOP" -> renderingCommands.add(new JsonObject().put("command", "stop"));
            case "EXIT" -> renderingCommands.add(new JsonObject().put("command", "exit"));
        }
        
    
        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                gameMetadata(),
                Arrays.stream(new RenderingCommand[] {
                    new NativeCommands.ShowMenuError("That name's not available")
                }).map((r) -> r.toJson()).toList()
            );
        } else {
            HangmanPlayer p = new HangmanPlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Hangman", "Hangman", name, playerName).toJson());
            
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }
    
}

