package minigames.server.krumgame;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
//import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.NativeCommands.LoadClient;

import minigames.krumgame.KrumInputFrame;

import java.util.Queue;
import java.util.LinkedList;



// TODO: Change the game name
public class KrumGame{
    static final int PLAYER_NAME_MAX_CHARS = 30;

    int playerTurn;

    private static final Logger logger = LogManager.getLogger(KrumGame.class);

    HashMap<String, GameCharacter> players = new HashMap<>();
    // This is for game instance name
    private String name;
    private String currentPlayerTurn;
    private String playerName;

    private int playerIndexCount;

    private ArrayList<Queue<KrumInputFrame>> frames;
    private int frameIndex;

    private static final int FRAMES_PER_TURN = 900;

    private long seed;

    public KrumGame(String name){
        this.name = name;
        this.playerTurn = 0;
        this.frameIndex = 0;
        this.frames = new ArrayList<Queue<KrumInputFrame>>();
        this.frames.add(new LinkedList<KrumInputFrame>());
        this.frames.add(new LinkedList<KrumInputFrame>());
        this.playerIndexCount = 0;
        this.seed = System.nanoTime();

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
        
        // Matt temporarily commented this out so we can get past the first screen
        /*
        String[] parts = playerNameWithType.split(" ");

        if(parts.length != 2){
            return sendErrorMsg("Invalid player Name format");
        }

        String playerName = parts[0];
        String playerType = parts[1];
        */

        String playerName = playerNameWithType;
        String playerType = "";

        while (players.containsKey(playerName) && playerName.length() < PLAYER_NAME_MAX_CHARS) {
            playerName += "_";
        }

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

        renderingCommands.add(new LoadClient("KrumGame", "KrumGame", name, playerName).toJson());
        
        return new RenderingPackage(gameMetadata(), renderingCommands);
    }


    public RenderingPackage runCommands(CommandPackage cp){
        //logger.info("Received command package {}", cp);
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        if (cp.commands().size() < 1) {
            logger.info("EMPTY COMMAND PACKAGE");
            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
        //logger.info("Received command package");        
        JsonObject content = cp.commands().get(0);        
        if (content.getValue("indexRequest") != null){
            int index = playerIndexCount;
            playerIndexCount++;
            JsonObject j = new JsonObject().put("playerIndexFromServer", index).put("seed", seed);
            renderingCommands.add(j);
        }
        else if (content.getValue("frameRequest") != null) {            
            int index = content.getInteger("frameRequest");
            index = 1 - index;
            if (index < 0 || index >= frames.size()) {
                logger.info("invalid frameRequest");
                return new RenderingPackage(gameMetadata(), renderingCommands);
            }
            if (frames.get(index).size() < 1) {
                return new RenderingPackage(gameMetadata(), renderingCommands);
            }
            JsonObject f = frames.get(index).remove().getJson();
            JsonObject j = new JsonObject().put("frame", f);
            renderingCommands.add(j);
        }
        else {
            int pindex;
            try {
                pindex = content.getInteger("activePlayer");
            } catch (Exception e) {
                System.out.println("unrecognised command");
                return new RenderingPackage(null, null);
            }
            if (pindex >= 2) {
                System.out.println("invalid activePlayer");
                return new RenderingPackage(null, null);
            }
            frames.get(pindex).add(new KrumInputFrame(content));
        }
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


    public void processPlayerCommand(JsonObject command, GameCharacter player, List<JsonObject> renderingCommands){
        //String commandType = command.getString("commandType");
        // GameCommand gameCommand;


        // JsonObject renderingCommand = gameCommand.execute(player, this);
        //renderingCommands.add(renderingCommand);



    }   
}
