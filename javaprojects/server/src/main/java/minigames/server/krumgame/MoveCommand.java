/**
 * This class is for processing Move command
 */
package minigames.server.krumgame;

import io.vertx.core.json.JsonObject;

public class MoveCommand implements GameCommand {
    private int x, y;

    public MoveCommand(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public JsonObject execute(GameCharacter player, KrumGame game){
        player.setXPosition(x);
        player.setYPosition(y);

        return new JsonObject().put("commandType", "move").put("name", player.name)
            .put("xPosition", x).put("yPosition", y);
    }
}