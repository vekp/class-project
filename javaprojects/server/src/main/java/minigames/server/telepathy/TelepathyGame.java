package minigames.server.telepathy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.RenderingPackage;

import java.util.HashSet;

public class TelepathyGame {

    // Logs output
    private static final Logger logger = LogManager.getLogger(TelepathyGame.class);

    private HashSet<String> players = new HashSet<>();
    private String name;


    /**
     * Constructs a new game of Telepathy using a name.
     * 
     * @param name: String with the name for this game.
     */
    public TelepathyGame(String name) {
        this.name = name;
    }

    /**
     * Provides metadata about this TelepathyGame.
     * 
     * @return GameMetaData object with name of the game and players, and if the
     *         game can be joined.
     */
    public GameMetadata telepathyGameMetadata() {

        return new GameMetadata("Telepathy", name, this.players.toArray(new String[this.players.size()]), true);
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
        // Need to add verification for player name on the server (duplicates, etc)
        players.add(name);

        // TODO Create rendering commands for the package
        return new RenderingPackage(this.telepathyGameMetadata(), null);
    }

    // Accessor methods

    /**
     * Getter for current players in the game.
     * @return copy of the HashSet of players.
     */
    public HashSet<String> getPlayers(){
        return new HashSet<>(this.players);
    }

    /**
     * Getter for the name assigned to this game on creation.
     * @return String value with the name.
     */
    public String getName(){
        return this.name;
    }
}
