package minigames.server.memory;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.*;

import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.common.memory.DeckOfCards;
import minigames.common.memory.DeckOfCards.PlayingCard;

import minigames.server.shufflingsystem.ShufflingFramework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.server.achievements.AchievementHandler;
import static minigames.server.memory.MemoryAchievement.*;

/**
 * Represents an actual Memory game in progress.
 * Used and adapted MuddleGame.java
 */
public class MemoryGame {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(MemoryGame.class);

    // Create a record of MemoryPlayer class
    record MemoryPlayer(String name) {
    }

    /** Uniquely identifies this game */
    String name;
    String playerName;
    AchievementHandler achievementHandler;
    PlayingCard[] playingCards;
    int previousCardIndex = -1;
    int playerScore = 0;
    int [] selectedCards = new int [2];
    boolean[] solvedCards = new boolean[18];

    public MemoryGame(String name, String playerName) {
        this.name = name;
        this.playerName = playerName;
        this.achievementHandler = new AchievementHandler(MemoryServer.class);

        // Unlock TEST_THAT_MEMORY achievement for starting a new game
        achievementHandler.unlockAchievement(playerName, TEST_THAT_MEMORY.toString());

        this.playingCards = new DeckOfCards(18, true).getCards();
        ShufflingFramework.shuffle(playingCards);
    }

    /** Achievement handler for this game */
    // Code snippet from AchievementHandler.java
    // private static final

    // Players
    HashMap<String, MemoryPlayer> players = new HashMap<>();

   /** 
    * This will check if the game is in a terminal state.
    * @author Lyam Talbot
    * If the player's current score is equal to the number of pairs the game is over.
    * This will need to be used in conjunction with a method that shows a score screen/resets the game, etc.
    */
    public boolean isTerminal(){
        return playerScore == playingCards.length / 2;
    }

    /**
     * @author Lyam Talbot
     * This method will check if the player has created a pair or not
     * @param cardIndex the index of the PlayingCard inside the playingCards[] array
     * If a card has been flipped already, check that array index against the array index of the newly flipped card.
     * If they match increment playerScore and set the previousFlipped card back to -1
     * If they don't match tell the user they don't match and set the previous flipped card to -1
     * If no card has been flipped previously set previousCardIndex to the index of the newly flipped card.
     * Originally I stored a copy of the card that had been flipped, but this allowed a card to match with itself. 
     * Now we can only match on card objects that are equal but also at different array indexes. 
     */
    public boolean check(int cardIndex) {
        if(solvedCards[cardIndex] == true){
            System.out.println("You have already solved this card!");
            achievementHandler.unlockAchievement(playerName, DOUBLE_FLIPPER.toString());
            return false;
        }
        if(previousCardIndex == cardIndex){
            System.out.println("Please flip a new card!");
            return false;
        }
        if (previousCardIndex != -1) {
            if (playingCards[previousCardIndex].equals(playingCards[cardIndex])) {
                playerScore++;
                System.out.println("These cards match!");
                System.out.println(playerScore);
                solvedCards[cardIndex] = true;
                solvedCards[previousCardIndex] = true;
                previousCardIndex = -1;
                achievementHandler.unlockAchievement(playerName, CARD_MATCHER.toString());
                return true;
            } else {
                previousCardIndex = -1;
                System.out.println("These cards do not match");
                achievementHandler.unlockAchievement(playerName, CARD_FLIPPER.toString());
                return false;
            }
        } else {
            previousCardIndex = cardIndex;
            return false;
        }
    }

    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** The deck of cards in use for the current game */

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("Memory", name, getPlayerNames(), true);
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);

        // Create temp JSONObject object
        ArrayList<JsonObject> renderingCommands = new ArrayList<>();
        JsonObject cmd = cp.commands().get(0);
        switch (cmd.getString("command")) {
            case "exitGame" -> {
                players.remove(cp.player());
                renderingCommands.add(new JsonObject().put("command", "showGames"));
            }
            // case "updateCards" ->{
            //     renderingCommands.add(new JsonObject().put("comand", "updateCards").put("deck", playingCards));
            // }
            case "newGame" -> {
                this.playingCards = new DeckOfCards(18, true).getCards();
                ShufflingFramework.shuffle(playingCards);
                JsonObject newCommands = new JsonObject();
                newCommands.put("command", "deckOfCards");
                newCommands.put("cards", playingCards);
                renderingCommands.add(newCommands);
            }
            case "resetCards" -> {
                renderingCommands.add(new JsonObject().put("command", "resetCards"));
            }
            case "resetToCardBacks" -> {
                renderingCommands.add(new JsonObject().put("command", "resetToCardBacks"));
            }
            case "Flip_Card_1" -> {
                if(check(0)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));            
                } else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
                JsonObject result = new JsonObject();
                result.put("command", "Flip_Card_1");
                result.put("update", "true");
                renderingCommands.add(result);


                // System.out.println(playingCards[0].getValue() + " of " + playingCards[0].getSuit());
            }
            case "Flip_Card_2" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_2"));
                // System.out.println(playingCards[1].getValue() + " of " + playingCards[1].getSuit());
                if(check(1)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));            
                }
                else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_3" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_3"));
                // System.out.println(playingCards[2].getValue() + " of " + playingCards[2].getSuit());
                if(check(2)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_4" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_4"));
                // System.out.println(playingCards[3].getValue() + " of " + playingCards[3].getSuit());
                if(check(3)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_5" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_5"));
                // System.out.println(playingCards[4].getValue() + " of " + playingCards[4].getSuit());
                if(check(4)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_6" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_6"));
                // System.out.println(playingCards[5].getValue() + " of " + playingCards[5].getSuit());
                if(check(5)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_7" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_7"));
                // System.out.println(playingCards[6].getValue() + " of " + playingCards[6].getSuit());
                if(check(6)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_8" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_8"));
                // System.out.println(playingCards[7].getValue() + " of " + playingCards[7].getSuit());
                if(check(7)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_9" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_9"));
                // System.out.println(playingCards[8].getValue() + " of " + playingCards[8].getSuit());
                if(check(8)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_10" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_10"));
                // System.out.println(playingCards[9].getValue() + " of " + playingCards[9].getSuit());
                if(check(9)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_11" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_11"));
                // System.out.println(playingCards[10].getValue() + " of " + playingCards[10].getSuit());
                if(check(10)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_12" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_12"));
                // System.out.println(playingCards[11].getValue() + " of " + playingCards[11].getSuit());
                if(check(11)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_13" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_13"));
                // System.out.println(playingCards[12].getValue() + " of " + playingCards[12].getSuit());
                if(check(12)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_14" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_14"));
                // System.out.println(playingCards[13].getValue() + " of " + playingCards[13].getSuit());
                if(check(13)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_15" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_15"));
                // System.out.println(playingCards[14].getValue() + " of " + playingCards[14].getSuit());
                if(check(14)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_16" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_16"));
                // System.out.println(playingCards[15].getValue() + " of " + playingCards[15].getSuit());
                if(check(15)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_17" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_17"));
                // System.out.println(playingCards[16].getValue() + " of " + playingCards[16].getSuit());
                if(check(16)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
            case "Flip_Card_18" -> {
                renderingCommands.add(new JsonObject().put("command", "Flip_Card_18"));
                // System.out.println(playingCards[17].getValue() + " of " + playingCards[17].getSuit());
                if(check(17)){
                    renderingCommands.add(new JsonObject().put("command", "updateScore"));
                }else {
                    if(previousCardIndex != -1){
                        renderingCommands.add(new JsonObject().put("command", "resetCards").put("solved", solvedCards));
                    }
                }
            }
        }

        return new RenderingPackage(this.gameMetadata(), renderingCommands);
    }

    /** Joins this game */
    public RenderingPackage joinGame(String playerName) {
        if (players.containsKey(playerName)) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[] {
                            new NativeCommands.ShowMenuError("That name's not available")
                    }).map((r) -> r.toJson()).toList());
        } else {
            MemoryPlayer p = new MemoryPlayer(playerName);
            players.put(playerName, p);

            // Create temp JSONObject object
            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("Memory", "Memory", name, playerName).toJson());
            renderingCommands.add(new JsonObject().put("command", "clearText"));
            // renderingCommands.add(new JsonObject().put("command", "deckOfCards").put("cards", playingCards));
            renderingCommands.add(new JsonObject().put("command", "deckOfCards").put("cards", playingCards));

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }
    }

}