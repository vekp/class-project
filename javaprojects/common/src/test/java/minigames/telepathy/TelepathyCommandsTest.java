import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertTrue;

import minigames.telepathy.TelepathyCommands;

public class TelepathyCommandsTest {
    @Test
    @DisplayName("Test TelepathyCommands string representation")
    public void testToString(){
        assertTrue(TelepathyCommands.TOGGLEREADY.toString().equals("TOGGLEREADY"));
        assertTrue(TelepathyCommands.MODIFYPLAYER.toString().equals("MODIFYPLAYER"));
        assertTrue(TelepathyCommands.QUIT.toString().equals("QUIT"));
        assertTrue(TelepathyCommands.SYSTEMQUIT.toString().equals("SYSTEMQUIT"));
        assertTrue(TelepathyCommands.BUTTONPRESS.toString().equals("BUTTONPRESS"));
        assertTrue(TelepathyCommands.BUTTONUPDATE.toString().equals("BUTTONUPDATE"));
        assertTrue(TelepathyCommands.REQUESTUPDATE.toString().equals("REQUESTUPDATE"));
        assertTrue(TelepathyCommands.NOUPDATE.toString().equals("NOUPDATE"));
        assertTrue(TelepathyCommands.INVALIDCOMMAND.toString().equals("INVALIDCOMMAND"));
        assertTrue(TelepathyCommands.JOINGAMEFAIL.toString().equals("JOINGAMEFAIL"));
        assertTrue(TelepathyCommands.JOINGAMESUCCESS.toString().equals("JOINGAMESUCCESS"));
        assertTrue(TelepathyCommands.GAMEOVER.toString().equals("GAMEOVER"));

        assertTrue(TelepathyCommands.TESTCOMMAND.toString().equals("TESTCOMMAND"));
    }
}