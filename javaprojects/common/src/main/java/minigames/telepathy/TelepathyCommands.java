package minigames.telepathy;

/**
 * Enum to keep all valid values for commands used in Telepathy client/server
 * communications.
 */
public enum TelepathyCommands {
    /*
    * JOINGAMESUCCESS: Used in RenderingPackages sent from the server to signal that
    *       the client has successfully joined the game it was attempting to join.
    *       Attributes: n/a
    * JOINGAMEFAIL: Used in RenderingPackages sent from the server to signal that the
    *       the client was unable to join the game it was attempting to join. Accompanied
    *       with a 'message' specifying the reason for failure.
    *       Attributes: n/a
    * TOGGLEREADY: Used by client to inform the server the player has toggled their ready
    *       status and is ready to start playing the game.
    * ASKQUESTION: Used by the client to indicate the player has seleted a tile to ask
    *       for more information.
    *       Attributes: Tile coordinates.
    * FINALGUESS: Used by the client to indicate the player has selected a tile for their
    *       final guess.
    *       Attributes: Tile coordinates.
    * BUTTONPRESS: Used in CommandPackages sent from the client to the server when the
    *       player presses a button on the Telepathy board.
    *       Attributes: Name of button pushed.
    * BUTTONUPDATE: Used by the server to inform the client of a change in state that should
    *       be displayed to the user with an update to a button.
    *       Attributes: Name of button pushed, changes to make.
    * REQUESTUPDATE: Used by the client to request an update of the current state
    *       from the server.
    * NOUPDATE: Used by the server to inform the client there are no current updates.
    * GAMEOVER: Used by the server to inform the client the game state has changed to game
    *       over.
    * SYSTEMQUIT: Used by the client to signal that the game window has been closed
    *       on the client (sends a regular RenderingPackage).
    * QUIT: Used by the client to signal that the client has exited Telepathy but
    *       the window remains open.
    * MODIFYPLAYER: Inform the client that information about a player has been modified.
    *       Attributes: Name of the player, modifications (joined, leaving)
    * TESTCOMMAND: A simple command used for testing/debugging purposes.
    * INVALIDCOMMAND: Used to respond to an invalid CommandPackage.
    */
 
    JOINGAMESUCCESS, JOINGAMEFAIL, TOGGLEREADY, ASKQUESTION, FINALGUESS, 
    CHOOSETILE, BUTTONPRESS, BUTTONUPDATE, REQUESTUPDATE, NOUPDATE, 
    GAMESTART, GAMEOVER, SYSTEMQUIT, QUIT, MODIFYPLAYER, POPUP,
    TESTCOMMAND, INVALIDCOMMAND;

    public String toString(){
        return switch(this){
            case JOINGAMESUCCESS -> "JOINGAMESUCCESS";
            case JOINGAMEFAIL -> "JOINGAMEFAIL";
            case TOGGLEREADY -> "TOGGLEREADY";
            case BUTTONPRESS -> "BUTTONPRESS";
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
            case TESTCOMMAND -> "TESTCOMMAND";
            case INVALIDCOMMAND -> "INVALIDCOMMAND";
            default -> "NO-STRING-CONFIGURED";
        };
    }
}
