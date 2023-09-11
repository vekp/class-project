package minigames.client.krumgame;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;

public class KrumGameClient implements GameClient {
    KrumGame krumGame;

    public KrumGameClient() {

    }

    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        krumGame = new KrumGame();
        krumGame.load(mnClient, game, player);
    }

    public void execute(GameMetadata game, JsonObject command) {
        if (krumGame == null) {
            System.out.println("Error: trying to execute command but krumGame is null");
            return;
        }
        krumGame.execute(game, command);
    }

    public void closeGame() {
        if (krumGame == null) {
            System.out.println("Error: trying to call closeGame but krumGame is null");
            return;
        }
        krumGame.closeGame();
        krumGame = null;
    }
    
}
