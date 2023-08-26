package minigames.server.battleship;

import java.util.Random;

/**
 * A data structure returned by a battleship player when processing their turn
 * @param successful whether the turn was successful (e.g. the input was valid)
 * @param message any message to display to the player (a hit message, miss message, 'you destroyed the enemy ship!',
 *               etc
 */
public record BattleshipTurnResult(
        boolean successful,
        boolean shipHit,
        String message
) {
    public static BattleshipTurnResult firstInstruction(){
        return new BattleshipTurnResult(true, false, lineBreak+"To fire at the enemy, enter grid coordinates: (eg, A4)"+inputPending);
    }
    public static BattleshipTurnResult instruction(){
        return new BattleshipTurnResult(true, false, lineBreak+coordTag+" "+enterCoords+inputPending);
    }
    public static BattleshipTurnResult playerHitEnemy(){
        String[] playerHitEnemy = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
        return new BattleshipTurnResult(true, true, lineBreak+getRandomMessage(playerHitEnemy)+" "+incoming);
    }
    public static BattleshipTurnResult playerMissedEnemy(){
        String[] playerMissedEnemy = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
        return new BattleshipTurnResult(true, false, lineBreak+getRandomMessage(playerMissedEnemy)+" "+incoming);
    }
    public static BattleshipTurnResult enemyHitPlayer(String input){
        String[] enemyHitPlayer = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
        return new BattleshipTurnResult(true, true, lineBreak+getRandomMessage(enemyHitPlayer)+" Enemy fired at coordinates: ["+input+"]"+instruction().message());
    }
    public static BattleshipTurnResult enemyMissedPlayer(String input){
        String[] enemyMissedPlayer = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};
        return new BattleshipTurnResult(true, false, lineBreak+getRandomMessage(enemyMissedPlayer)+" Salvo fired at coordinates: ["+input+"]"+instruction().message());
    }

    public static BattleshipTurnResult alreadyHitCell(){
        return new BattleshipTurnResult(true, false, lineBreak+"You already hit this cell!");
    }

    // Response strings
    static String lineBreak = "\n\n";
    static String inputPending = "\n...";
    static String enterCoords = "Enter grid coordinates: ";
    static String incoming = "Prepare for incoming fire!";
    static String coordTag = "Return fire!";

    /**
     * Method to pick a random string from a list
     * @param messages String[] containing message options
     * @return the randomly chosen String
     */
    static String getRandomMessage(String[] messages){
        if(messages.length == 0 ) return "";
        Random rng = new Random();
        return messages[rng.nextInt(0, messages.length)];
    }
}
