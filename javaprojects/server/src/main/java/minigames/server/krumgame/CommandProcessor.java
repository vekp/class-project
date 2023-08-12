package minigames.server.krumgame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;

/* Set up a separate class to keep KrumGame class short */

public class CommandProcessor {

    private static final Logger logger = LogManager.getLogger(CommandProcessor.class);

    private KrumGame game;

    //TODO: Instead of passing Game Instance we can create static methods called from the game
    public CommandProcessor(KrumGame game){
        this.game = game;
    }

    // TODO: Need to make process commands simpler and modular
    public void processCommand(JSONObject command, GameCharacter player) {
        // Extract the command type or other identifying information
        String commandType = command.getString("commandType");

        // Handle the command based on its type
        switch (commandType) {
              // TODO: Add commands and functionality
        }
    }
}

