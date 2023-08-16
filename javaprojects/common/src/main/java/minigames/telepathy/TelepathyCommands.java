package minigames.telepathy;

/**
 * Enum to keep all valid values for commands used in Telepathy client/server
 * communications.
 * 
 * JOINGAMESUCCESS: Used in RenderingPackages sent from the server to signal that
 *      the client has successfully joined the game it was attempting to join.
 * JOINGAMEFAIL: Used in RenderingPackages sent from the server to signal that the
 *      the client was unable to join the game it was attempting to join. Accompanied
 *      with a 'message' specifying the reason for failure.
 */
public enum TelepathyCommands {
    JOINGAMESUCCESS, JOINGAMEFAIL;

    public String toString(){
        return switch(this){
            case JOINGAMESUCCESS -> "JOINGAMESUCCESS";
            case JOINGAMEFAIL -> "JOINGAMEFAIL";
            default -> "null";
        };
    }
}
