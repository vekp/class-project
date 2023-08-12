package minigames.server.krumgame;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
//import minigames.rendering.NativeCommands.LoadClient;


// TODO: Change the game name
public class KrumGame{

    private static final Logger logger = LogManager.getLogger(KrumGame.class);

    HashMap<String, GameCharacter> players = new HashMap<>();
    // This is for game instance name
    private String name;
    private String currentPlayerTurn;
    private CommandProcessor commandProcessor;


    public KrumGame(String name){
        this.name = name;
        this.commandProcessor = new CommandProcessor(this);
    }

    public String[] getPlayerNames(){
        return players.keySet().toArray(String[]::new);
    }

    public GameMetadata gameMetadata(){
        return new GameMetadata("KrumGame", name, getPlayerNames(), true);
    }

    public void changeTurn(){
        String[] playerNames = getPlayerNames();
        if(playerNames.length > 1){
            this.currentPlayerTurn = currentPlayerTurn.equals(playerNames[0]) ? playerNames[1] : playerNames[0];
        }
    }

    /**
     * To join a game
     * Number of players are only 2 at the moment
     * Client UI can manage either human player or AI player to be second player 
    */
    public RenderingPackage joinGame(String playerNameWithType){
        // Since MinigameNetworkServer endpoint only allows player Name to be received
        // Using to send both player Name, type separated by space
        // Could be used for multiplayer functionality as well
        
        String[] parts = playerNameWithType.split(" ");

        if(parts.length != 2){
            return sendErrorMsg("Invalid player Name format");
        }

        String playerName = parts[0];
        String playerType = parts[1];

        if (players.containsKey(playerName)){
            return sendErrorMsg("This name is already taken");
        }
        
        // Check if game is full
        if (players.size() >= 2){
            return sendErrorMsg("The game is full");
        }

        // TODO: Get initial player location from Game character
        GameCharacter p = new GameCharacter(playerName, playerType, 0, 0, 100);

        players.put(playerName, p);

        // if 2 players set the turn
        // TODO: It might not be the first player joined due to hashmap
        if (players.size() == 2){
            this.currentPlayerTurn = players.keySet().iterator().next();
        }

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        renderingCommands.add(new JsonObject().put("command", "addPlayer").put("name", p.playerName)
                        .put("type", p.playerType).put("xPosition", p.xPosition)
                        .put("yPosition", p.yPosition).put("health", p.health));

        return new RenderingPackage(gameMetadata(), renderingCommands);
    }


    public RenderingPackage runCommands(CommandPackage cp){
        logger.info("Received command package {}", cp);

        GameCharacter p = players.get(cp.player());

        if (!p.getName().equals(currentPlayerTurn)){
            return sendErrorMsg("The game is in an invalidState");
        }

        for (JsonObject command : cp.commands()){
            processPlayerCommand(command, p);
        }

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        renderingCommands.add(new JsonObject().put("command", "updatePlayer").put("name", p.name).put("xPosition", p.xPosition)    
                                       .put("yPosition", p.yPosition).put("health", p.health).put("turn", this.currentPlayerTurn));
        // Change the turn
        this.changeTurn();
        return new RenderingPackage(gameMetadata(), renderingCommands);
    } 

    
    private RenderingPackage sendErrorMsg(String errorMsg){
        return new RenderingPackage(
            gameMetadata(),
            Arrays.stream(new RenderingCommand[]{
                new NativeCommands.ShowMenuError(errorMsg)
            }).map((r)-> r.toJson()).toList()
        );
    }


    public void processPlayerCommand(JsonObject command, GameCharacter player){
        commandProcessor.processCommand(command, player);
    }   
}
