package minigames.server.telepathy;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands;
import minigames.rendering.RenderingCommand;
import minigames.rendering.RenderingPackage;
import minigames.rendering.NativeCommands.QuitToMenu;
import minigames.telepathy.TelepathyCommands;

import java.sql.Array;
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
    }

    /**
     * Provides metadata about this TelepathyGame.
     * 
     * @return GameMetaData object with name of the game and players, and if the
     *         game can be joined.
     */
    public GameMetadata telepathyGameMetadata() {
        ArrayList<String> playerNames = new ArrayList<>();
        for(Player player : this.players){
            if(player != null){
                playerNames.add(player.name());
            }
        }
        return new GameMetadata("Telepathy", name, playerNames.toArray(new String[playerNames.size()]), true);
    }

    public RenderingPackage runCommands(CommandPackage commandPackage) {
        logger.info("Received command package {}", commandPackage);

        // The response that is to be sent back to client
        RenderingPackage response;

        // The TelepathyCommand used to specify how to handle the package
        TelepathyCommands command = TelepathyCommands.valueOf(commandPackage.commands().get(0).getString("command"));
       
        // Switch case to choose which function to call - default case returns empty RenderingPackage
        switch(command){
            case QUIT -> response = quitGame(commandPackage);
            case SYSTEMQUIT -> response = fullQuitGame(commandPackage);
            default -> {
                ArrayList<JsonObject> renderingCommands = new ArrayList<>();
                response = new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
            }
        }

        // Return the response
        return response;
    }

    /**
     * Adds a player to the game and makes up a RenderPackage with instructions
     * for the client.
     * 
     * @param playerName: Name of the player wanting to join.
     * @return RenderingPackage with instructions for the client.
     */
    public RenderingPackage joinGame(String playerName) {
        logger.info(playerName + " wants to join Telepathy game"+ this.name);
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();


        if(!validName(playerName)){
            renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Not a valid name"));
        } else if(this.players[0] == null || this.players[1] == null){
            if(this.players[0] == null){
                if(this.players[1] != null && this.players[1].name.equals(playerName)){
                    renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Name taken"));
                } else{
                    this.players[0] = new Player(playerName);
                    renderingCommands.add(new NativeCommands.LoadClient("Telepathy", "Telepathy", this.name, playerName).toJson());
                    renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMESUCCESS));
                }
            } else{
                if(this.players[0] != null && this.players[0].name.equals(playerName)){
                    renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "Name taken"));
                } else{
                    this.players[1] = new Player(playerName);
                    renderingCommands.add(new NativeCommands.LoadClient("Telepathy", "Telepathy", this.name, playerName).toJson());
                    renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMESUCCESS));
                }
            }
        } else{
            renderingCommands.add(new JsonObject().put("command", TelepathyCommands.JOINGAMEFAIL).put("message", "No space available"));
        }
        
        // NOTE: The rendering commands used are temporary and can be changed in the future   
        // Possibly use an enum to represent the value to assign commands?     
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    public RenderingPackage quitGame(CommandPackage commandPackage){
        ArrayList<JsonObject> renderingCommands =  new ArrayList<>();
        
        String leavingPlayer = commandPackage.player();
        removePlayer(leavingPlayer);
        
        renderingCommands.add(new NativeCommands.QuitToMenu().toJson());
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    public RenderingPackage fullQuitGame(CommandPackage commandPackage){
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        String leavingPlayer = commandPackage.player();
        removePlayer(leavingPlayer);

        renderingCommands.add(new JsonObject().put("command", TelepathyCommands.QUIT));
        return new RenderingPackage(this.telepathyGameMetadata(), renderingCommands);
    }

    private void removePlayer(String playerName){
        for(int i = 0; i < this.players.length; i++){
            if(this.players[i] == null) continue;
            if(this.players[i].name().equals(playerName)){
                this.players[i] = null;
                continue;
            }
        }
    }


    /**
     * Checks if a name is valid for use.
     * 
     * Invalid names include:
     *  Empty strings
     *  Names only containing white space
     *  Names containing spaces
     * @param name String containing name to be validated
     * @return Boolean value with result of validation
     */
    private boolean validName(String name){
        if(name == null) return false;
        if(name.isBlank()) return false;
        if(name.contains(" ")) return false;

        return true;
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
