package minigames.telepathy;

/**
 * Exception to throw for invalid TelepathyCommands.
 */
public class TelepathyCommandException extends RuntimeException {

    /**
     * Throw a new TelepathyCommandException with an error message describing the
     * problem and the command that was used.
     * @param command: The command that triggered the exception.
     * @param errorMessage Error message describing the problem.
     */
    public TelepathyCommandException(String command, String errorMessage){
        super(errorMessage + ".Command used: " + command);
    }
}
