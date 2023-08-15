package minigames.server.telepathy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;

import java.util.ArrayList;
import java.util.Arrays;

public class TelepathyGame {

    // Logs output
    private static final Logger logger = LogManager.getLogger(TelepathyGame.class);

    
    private String name;

    // Can have other fields later - chosen board piece, etc...
    public record Player(
        String name
    ){}

    private Player players[] =  {null, null};

    /**
     * Constructs a new game of Telepathy with a given name.
     * 
     * @param name: String with the name to use for this game.
     */
    public TelepathyGame(String name) {
        this.name = name;
        
        this.players[0] = new Player("Empty");
        this.players[1] = new Player("Empty");
    }

    /**
     * Provides metadata about this TelepathyGame.
     * 
     * @return GameMetaData object with name of the game and players, and if the
     *         game can be joined.
     */
    public GameMetadata telepathyGameMetadata() {
        String[] playerNames = {players[0].name, players[1].name};
        return new GameMetadata("Telepathy", name, playerNames, true);
    }

    public RenderingPackage runCommands(CommandPackage commandPackage) {
        logger.info("Received command package {}", commandPackage);

        // TODO handle the commands received

        // TODO Create a response to the command to send back

        // Decide what to do on receiving commands
        return new RenderingPackage(this.telepathyGameMetadata(), null);
    }

    /**
     * Adds a player to the game and makes up a RenderPackage with instructions
     * for the client.
     * 
     * @param name: Name of the player wanting to join.
     * @return RenderingPackage with instructions for the client.
     */
    public RenderingPackage joinGame(String name) {
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        // Need to add verification for player name on the server (duplicates, etc)
        if(this.players[0].name.equals("Empty")){
            this.players[0] = new Player(name);
            renderingCommands.add(new JsonObject().put("command", "joinedGameSuccess"));
        } else{
            if(this.players[0].name.equals(name)){
                // Name taken
                renderingCommands.add(new JsonObject().put("command", "joinedGameFail").put("message", "Unable to join game, name has already been taken."));
            }
            else if(!this.players[1].name.equals("Empty")){ 
            // Spot already taken
            renderingCommands.add(new JsonObject().put("command", "joinedGameFail").put("message", "Unable to join game, no spots available."));
            }
        }
        
        // TODO Create rendering commands for the package
        
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    // Accessor methods

    /**
     * Getter for current players in the game.
     * @return copy of the HashSet of players.
     */
    public Player[] getPlayers(){
        return Arrays.stream(this.players).toArray(Player[]::new);
    }

    /**
     * Getter for the name assigned to this game on creation.
     * @return String value with the name.
     */
    public String getName(){
        return this.name;
    }
}
