package minigames.server.hangman;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;
import minigames.rendering.RenderingPackage;
import minigames.server.ClientType;
import minigames.server.GameServer;
import java.util.HashMap;
import java.util.Random;

/**
 * Our MuddleServer holds MuddleGames. 
 * When it receives a CommandPackage, it finds the MuddleGame and calls it.
 */
public class HangmanGameServer implements GameServer {

    static final String chars = "abcdefghijklmopqrstuvwxyz";
    HashMap<String, HangmanGame> games = new HashMap<>();


    /** A random name. We could do with something more memorable, like Docker has */
    static String randomName() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Holds the games in progress in memory (no db) */

    @Override
    public GameServerDetails getDetails() {
        return new GameServerDetails("Hangman", "Fun Puzzle Game");
    }

    @Override
    public ClientType[] getSupportedClients() {
        return new ClientType[] { ClientType.Swing, ClientType.Scalajs, ClientType.Scalafx };
    }

    @Override
    public GameMetadata[] getGamesInProgress() {
        return games.keySet().stream().map((name) -> {
            return new GameMetadata("Hangman", name, games.get(name).getPlayerNames(), true);
        }).toArray(GameMetadata[]::new);
    }

    @Override
    public Future<RenderingPackage> newGame(String playerName) {
        HangmanGame g = new HangmanGame(randomName(), playerName);
        games.put(g.name, g);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> joinGame(String game, String playerName) {
        HangmanGame g = games.get(game);
        return Future.succeededFuture(g.joinGame(playerName));
    }

    @Override
    public Future<RenderingPackage> callGame(CommandPackage cp) {
        HangmanGame g = games.get(cp.gameId());
        JsonObject cmd = cp.commands().get(0);
        switch(cmd.getString("command")){
            case "exit" -> {
                exitServer(cp.gameId());
            }
        }
        return Future.succeededFuture(g.runCommands(cp));
    }

    private void exitServer(String g){
        games.remove(g);
    }

}

