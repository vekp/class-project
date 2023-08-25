package minigames.server.battleship;

import java.util.Random;

/**
 * A data structure returned by a battleship player when processing their turn
 * @param successful whether the turn was successful (e.g. the input was valid)
 * @param message any message to display to the player (a hit message, miss message, 'you destroyed the enemy ship!',
 *               etc
 */
public record BattleShipTurnResult(
        boolean successful,
        String message
) {
    public static BattleShipTurnResult playerHitEnemy(){
        String[] playerHitEnemy = {"Enemy ship hit! Well done Sir.", "Straight into their hull!", "Direct Hit!"};
        return new BattleShipTurnResult(true, getRandomMessage(playerHitEnemy));
    }
    public static BattleShipTurnResult playerMissedEnemy(){
        String[] playerMissedEnemy = {"Salvo Missed.", "Target not hit.", "Adjust your coordinates."};
        return new BattleShipTurnResult(true, getRandomMessage(playerMissedEnemy));
    }
    public static BattleShipTurnResult enemyHitPlayer(){
        String[] enemyHitPlayer = {"Enemy has hit our fleet,", "We've been hit!", "We're under fire!"};
        return new BattleShipTurnResult(true, getRandomMessage(enemyHitPlayer));
    }
    public static BattleShipTurnResult enemyMissedPlayer(){
        String[] enemyMissedPlayer = {"Enemy has missed!", "Enemy missed another salvo.", "They missed us."};
        return new BattleShipTurnResult(true, getRandomMessage(enemyMissedPlayer));
    }

    public static BattleShipTurnResult alreadyHitCell(){
        return new BattleShipTurnResult(true, "You already hit this cell!");
    }

    static String getRandomMessage(String[] messages){
        if(messages.length == 0 ) return "";
        Random rng = new Random();
        return messages[rng.nextInt(0, messages.length)];
    }
}
