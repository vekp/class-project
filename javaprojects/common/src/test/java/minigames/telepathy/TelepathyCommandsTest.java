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
        assertTrue(TelepathyCommands.BUTTONUPDATE.toString().equals("BUTTONUPDATE"));
        assertTrue(TelepathyCommands.REQUESTUPDATE.toString().equals("REQUESTUPDATE"));
        assertTrue(TelepathyCommands.NOUPDATE.toString().equals("NOUPDATE"));
        assertTrue(TelepathyCommands.INVALIDCOMMAND.toString().equals("INVALIDCOMMAND"));
        assertTrue(TelepathyCommands.JOINGAMEFAIL.toString().equals("JOINGAMEFAIL"));
        assertTrue(TelepathyCommands.JOINGAMESUCCESS.toString().equals("JOINGAMESUCCESS"));
        assertTrue(TelepathyCommands.GAMEOVER.toString().equals("GAMEOVER"));
        assertTrue(TelepathyCommands.CHOOSETILE.toString().equals("CHOOSETILE"));
        assertTrue(TelepathyCommands.ASKQUESTION.toString().equals("ASKQUESTION"));
        assertTrue(TelepathyCommands.FINALGUESS.toString().equals("FINALGUESS"));
        assertTrue(TelepathyCommands.POPUP.toString().equals("POPUP"));

        assertTrue(TelepathyCommands.TESTCOMMAND.toString().equals("TESTCOMMAND"));

        // The default case needs to create an error string
        assertTrue(TelepathyCommands.DEFAULT.toString().equals("NO-STRING-CONFIGURED"));
    }
}