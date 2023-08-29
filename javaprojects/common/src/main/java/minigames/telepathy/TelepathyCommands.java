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
            status and is ready to start playing the game.
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
    * INVALIDCOMMAND: Used to respond to an invalid CommandPackage.
    */
 
    JOINGAMESUCCESS, JOINGAMEFAIL, TOGGLEREADY, BUTTONPRESS, BUTTONUPDATE, REQUESTUPDATE, NOUPDATE, 
    GAMEOVER, SYSTEMQUIT, QUIT, INVALIDCOMMAND;

    public String toString(){
        return switch(this){
            case JOINGAMESUCCESS -> "JOINGAMESUCCESS";
            case JOINGAMEFAIL -> "JOINGAMEFAIL";
            case TOGGLEREADY -> "TOGGLEREADY";
            case BUTTONPRESS -> "BUTTONPRESS";
            case BUTTONUPDATE -> "BUTTONUPDATE";
            case REQUESTUPDATE -> "REQUESTUPDATE";
            case NOUPDATE -> "NOUPDATE";
            case GAMEOVER -> "GAMEOVER";
            case QUIT -> "QUIT";
            case SYSTEMQUIT -> "SYSTEMQUIT";
            case INVALIDCOMMAND -> "INVALIDCOMMAND";
            default -> "NO-STRING-CONFIGURED";
        };
    }
}
