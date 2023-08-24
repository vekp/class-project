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
    * BUTTONPRESS: Used in CommandPackages sent from the client to the server when the
    *       player presses a button on the Telepathy board.
    *       Attributes: Coordinates of button pushed.
    * UPDATECLIENT: Used by the client to request an update of the current state
    *       from the server.
    * SYSTEMQUIT: Used by the client to signal that the game window has been closed
    *       on the client (sends a regular RenderingPackage).
    * QUIT: Used by the client to signal that the client has exited Telepathy but
    *       the window remains open.
    */
 
    JOINGAMESUCCESS, JOINGAMEFAIL, BUTTONPRESS, UPDATECLIENT, SYSTEMQUIT, QUIT;

    public String toString(){
        return switch(this){
            case JOINGAMESUCCESS -> "JOINGAMESUCCESS";
            case JOINGAMEFAIL -> "JOINGAMEFAIL";
            case BUTTONPRESS -> "BUTTONPRESS";
            case UPDATECLIENT -> "UPDATECLIENT";
            case QUIT -> "QUIT";
            case SYSTEMQUIT -> "SYSTEMQUIT";
            default -> "null";
        };
    }
}
