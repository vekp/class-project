package minigames.server.battleship;

import org.apache.derby.catalog.Statistics;

import java.util.Random;

/**
 * A data structure returned by a battleship player when processing their turn
 *
 * @param successful      whether the turn was successful (e.g. the input was valid)
 * @param playerMessage   the message to display to the player who took this turn
 * @param opponentMessage the message to display in the opponent's log about this result
 */
public record BattleshipTurnResult(
        boolean successful,
        boolean shipHit,
        String playerMessage,
        String opponentMessage
) {
    public static BattleshipTurnResult firstInstruction() {
        return new BattleshipTurnResult(true, false,
                lineBreak + "To fire at the enemy, enter grid coordinates: (eg, " + "A4)" + inputPending, "");
    }

    //result when the current player hits their target. The player gets a hit success message, the opponent gets a
    // 'enemy hit us!' message
    public static BattleshipTurnResult hitTarget(String inputCoords) {
        return new BattleshipTurnResult(true, true, endTurnPrompt(true, inputCoords),
                beginTurnPrompt(true, inputCoords));
    }

    //result when the current player missed their target. Player gets a 'we missed' message, opponent gets a 'they
    // missed us' message
    public static BattleshipTurnResult missTarget(String inputCoords) {
        return new BattleshipTurnResult(true, false, endTurnPrompt(false, inputCoords),
                beginTurnPrompt(false, inputCoords));
    }

    //called when a player shoots a cell they already hit. They lose their turn and the enemy gets told to ready
    // their turn
    public static BattleshipTurnResult alreadyHitCell(String inputCoords) {
        return new BattleshipTurnResult(true, false, lineBreak + "You already hit this cell! " + incoming,
                beginTurnPrompt(false, inputCoords));
    }

    // Response strings
    static String lineBreak = "\n\n";
    static String inputPending = "\n...";
    static String enterCoords = "It is your turn! Enter grid coordinates: ";
    static String incoming = "Prepare for incoming fire!";
    static String coordTag = "Return fire!";

    static String[] playerHitMessages = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
    static String[] enemyHitMessages = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
    static String[] playerMissMessages = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
    static String[] enemyMissMessages = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};

    /**
     * Method to pick a random string from a list
     *
     * @param messages String[] containing message options
     * @return the randomly chosen String
     */
    static String getRandomMessage(String[] messages) {
        if (messages.length == 0) return "";
        Random rng = new Random();
        return messages[rng.nextInt(0, messages.length)];
    }

    static String beginTurnPrompt(boolean hitOrMiss, String inputCoordinates) {
        String message = getRandomMessage(hitOrMiss ? enemyHitMessages : enemyMissMessages);
        return lineBreak
                + message
                + " Enemy fired at coordinates: [" + inputCoordinates + "]"
                + lineBreak
                + coordTag + " "
                + enterCoords
                + inputPending;
    }

    static String endTurnPrompt(boolean hitOrMiss, String inputCoordinates) {
        String message = getRandomMessage(hitOrMiss ? playerHitMessages : playerMissMessages);
        return lineBreak
                + message
                + " "
                + incoming;
    }

}
