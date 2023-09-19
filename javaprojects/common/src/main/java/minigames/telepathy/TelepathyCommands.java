package minigames.telepathy;

/**
 * Enum to keep all valid values for commands used in Telepathy client/server
 * communications.
 */
public enum TelepathyCommands {
    /*
    * JOINGAMESUCCESS: Used to signal the client that it has successfully connected 
            to the server.
    *       Attributes: n/a
    * JOINGAMEFAIL: Used to signal the client that it has failed to connected to 
            the server.
    *       Attributes: n/a
    * TOGGLEREADY: Used by client to inform the server the player has toggled their
    *        ready status and is ready to start playing the game.
    * ASKQUESTION: Used by the client to indicate the player has seleted a tile to ask
    *       for more information.
    *       Attributes: Tile coordinates as separate integer values.
    * FINALGUESS: Used by the client to indicate the player has selected a tile for their
    *       final guess.
    *       Attributes: Tile coordinates as separate integer values.
    * BUTTONUPDATE: Used by the server to inform the client of a change in state that should
    *       be displayed to the user with an update to a button.
    *       Attributes: Name of button or group of buttons, changes to make.
    * REQUESTUPDATE: Used by the client to request any pending updates.
            Attributes: n/a
    * NOUPDATE: Used by the server to inform the client there are no current updates.
            Attributes: n/a
    * GAMEOVER: Used by the server to inform the client the game state has changed to game
    *       over.
    *       Attributes: n/a
    * SYSTEMQUIT: Used by the client to signal that the game window has been closed
    *       on the client (sends a regular RenderingPackage).
    *       Attributes: n/a
    * QUIT: Used by the client to signal that the client has exited Telepathy but
    *       the window remains open.
    *       Attributes: n/a
    * MODIFYPLAYER: Inform the client that information about a player has been modified.
    *       Attributes: Name of the player, modifications (joined, leaving)
    *       Attributes: n/a
    * PARTIALMATCH: Inform the client that their question tile had a match with
    *       the target tile.
    *       Attributes: The X and Y coordinate of the question tile used.
    * TESTCOMMAND: A simple command used for testing/debugging purposes.
    * INVALIDCOMMAND: Used to respond to an invalid CommandPackage.
    */
 
    JOINGAMESUCCESS, JOINGAMEFAIL, TOGGLEREADY, ASKQUESTION, FINALGUESS, 
    CHOOSETILE, BUTTONUPDATE, REQUESTUPDATE, NOUPDATE, GAMEOVER, SYSTEMQUIT, 
    ELIMINATETILES, QUIT, MODIFYPLAYER, POPUP, PARTIALMATCH, TESTCOMMAND, 
    INVALIDCOMMAND, DEFAULT;

    public String toString(){
        return switch(this){
            case JOINGAMESUCCESS -> "JOINGAMESUCCESS";
            case JOINGAMEFAIL -> "JOINGAMEFAIL";
            case TOGGLEREADY -> "TOGGLEREADY";
            case CHOOSETILE -> "CHOOSETILE";
            case ASKQUESTION -> "ASKQUESTION";
            case FINALGUESS -> "FINALGUESS";
            case BUTTONUPDATE -> "BUTTONUPDATE";
            case REQUESTUPDATE -> "REQUESTUPDATE";
            case NOUPDATE -> "NOUPDATE";
            case GAMEOVER -> "GAMEOVER";
            case QUIT -> "QUIT";
            case SYSTEMQUIT -> "SYSTEMQUIT";
            case MODIFYPLAYER -> "MODIFYPLAYER";
            case POPUP -> "POPUP";
            case PARTIALMATCH -> "PARTIALMATCH";
            case ELIMINATETILES -> "ELIMINATETILES";
            case INVALIDCOMMAND -> "INVALIDCOMMAND";
            case TESTCOMMAND -> "TESTCOMMAND";
            default -> "NO-STRING-CONFIGURED";
        };
    }
}
