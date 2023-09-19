package minigames.server.battleship;

import java.util.Random;

/**
 * A data structure returned by a battleship player when processing their turn
 *
 * @param successful      whether the turn was successful (e.g. the input was valid)
 * @param shipHit         whether the input coordinates hit an enemy ship
 * @param playerMessage   the message to display to the player who took this turn
 * @param opponentMessage the message to display in the opponent's log about this result
 */
public record BattleshipTurnResult(
        boolean successful,
        boolean shipHit,
        boolean shipSunk,
        String playerMessage,
        String opponentMessage
) {
    public static BattleshipTurnResult firstInstruction() {
        return new BattleshipTurnResult(true, false, false,
                doubleBreak + "To fire at the enemy, enter grid coordinates: (eg, " + "A4)" + inputPending, "");
    }

    public static BattleshipTurnResult invalidInput(String input) {
        String message = input.isBlank() ?
                "Input cannot be blank, please enter a coordinate in the form 'A7'" + inputPrompt()
                :
                input + " is an invalid entry. Please enter a coordinate in the form 'A7'" + inputPrompt();

        return new BattleshipTurnResult(false, false, false,
                message, "");
    }

    //result when the current player hits their target. The player gets a hit success message, the opponent gets a
    // 'enemy hit us!' message
    public static BattleshipTurnResult hitTarget(String inputCoords, boolean sunk) {
        return new BattleshipTurnResult(true, true, sunk, endTurnPrompt(true, sunk, inputCoords),
                beginTurnPrompt(true, sunk, inputCoords));
    }

    //result when the current player missed their target. Player gets a 'we missed' message, opponent gets a 'they
    // missed us' message
    public static BattleshipTurnResult missTarget(String inputCoords) {
        return new BattleshipTurnResult(true, false, false, endTurnPrompt(false, false, inputCoords),
                beginTurnPrompt(false, false, inputCoords));
    }

    //called when a player shoots a cell they already hit. They lose their turn and the enemy gets told to ready
    // their turn
    public static BattleshipTurnResult alreadyHitCell(String inputCoords) {
        return new BattleshipTurnResult(true, false, false, doubleBreak + "You already hit this cell! " + incoming,
                beginTurnPrompt(false, false, inputCoords));
    }

    public static BattleshipTurnResult missionSuccess() {
        return new BattleshipTurnResult(true, false, false, getRandomMessage(missionSuccessMessages), getRandomMessage(missionFailMessages));
    }
    public static BattleshipTurnResult missionFail() {
        return new BattleshipTurnResult(true, false, false, getRandomMessage(missionFailMessages), getRandomMessage(missionSuccessMessages));
    }


    // Response strings
    static String lineBreak = "\n";
    static String doubleBreak = "\n\n";
    static String inputPending = "\n...";
    static String enterCoords = "Enter grid coordinates: ";
    static String incoming = "Prepare for incoming fire!";
    static String coordTag = "Return fire!";

    static String[] playerHitMessages = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
    static String[] enemyHitMessages = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
    static String[] playerMissMessages = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
    static String[] enemyMissMessages = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};
    static String[] shipSunkMessages = {"Vessel has been destroyed.", "Another one sunk Sir!", "To the depths she goes."};

    static String[] missionSuccessMessages = {"Victory! Enemy fleet has been destroyed.", "Mission Success! All ships destroyed.", "Voyage Successful! Enemy vessels destroyed."};
    static String[] missionFailMessages = {"We have been defeated.", "Enemy has sunk our fleet.", "Mission Failed. Fleet destroyed."};

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

    /**
     * Method to return a message to the player TODO: Finish
     *
     * @param hitOrMiss
     * @param inputCoordinates
     * @return
     */
    static String beginTurnPrompt(boolean hitOrMiss, boolean sunk, String inputCoordinates) {
        String message;
        if (sunk) {
            message = getRandomMessage(shipSunkMessages);
        } else {
            message = getRandomMessage(hitOrMiss ? enemyHitMessages : enemyMissMessages);
        }
        return lineBreak
                + message
                + " Enemy fired at coordinates: [" + inputCoordinates + "]"
                + doubleBreak
                + coordTag + " "
                + enterCoords
                + inputPending;
    }

    /**
     * TODO: Finish
     *
     * @param hitOrMiss
     * @param inputCoordinates
     * @return
     */
    static String endTurnPrompt(boolean hitOrMiss, boolean sunk, String inputCoordinates) {
        String message;
        if (sunk) {
            message = getRandomMessage(shipSunkMessages);
        } else {
            message = getRandomMessage(hitOrMiss ? playerHitMessages : playerMissMessages);
        }
        return doubleBreak
                + message
                + " "
                + incoming;
    }

    static String inputPrompt() {
        return doubleBreak + enterCoords + inputPending;
    }

}
