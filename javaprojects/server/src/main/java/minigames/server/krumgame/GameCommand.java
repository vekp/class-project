/**
 * This is Command interface which will be implemented by each command class
 */

package minigames.server.krumgame;

import io.vertx.core.json.JsonObject;

public interface GameCommand{
    JsonObject execute(GameCharacter player, KrumGame game);
}