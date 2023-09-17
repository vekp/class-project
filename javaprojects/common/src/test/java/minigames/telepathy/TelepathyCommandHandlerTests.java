package minigames.telepathy;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelepathyCommandHandlerTests {

    @Test
    @DisplayName("Test making jsonCommands and getting their values")
    public void testMakeJsonCommand(){
        JsonObject renderingCommand = TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.TESTCOMMAND);
        assertTrue(renderingCommand.getString("command").equals(TelepathyCommands.TESTCOMMAND.toString()));
        assertTrue(TelepathyCommandHandler.getAttributes(renderingCommand).size() == 0);

        renderingCommand = TelepathyCommandHandler.makeJsonCommand(TelepathyCommands.TESTCOMMAND, "attribute1", "attribute2");
        System.out.println("Test command: " + renderingCommand.toString());
        assertTrue(TelepathyCommandHandler.getAttributes(renderingCommand).get(0).equals("attribute1"));
        assertTrue(TelepathyCommandHandler.getAttributes(renderingCommand).get(1).equals("attribute2"));
    }

    @Test
    @DisplayName("Test getting attributes from Telepathy commands")
    public void testGetAttributes(){
        JsonObject command = TelepathyCommandHandler.makeJsonCommand(
            TelepathyCommands.TESTCOMMAND, "attribute1", "attribute2");
        assertTrue(TelepathyCommandHandler.getAttributes(command).get(0).equals("attribute1"));
        assertTrue(TelepathyCommandHandler.getAttributes(command).get(1).equals("attribute2"));

        // Check invalid JsonObjects
        JsonObject invalidObject = new JsonObject().put("command", " ");
        TelepathyCommandException e = assertThrows(TelepathyCommandException.class, () -> {
            TelepathyCommandHandler.getAttributes(invalidObject);
        });
    }

}
