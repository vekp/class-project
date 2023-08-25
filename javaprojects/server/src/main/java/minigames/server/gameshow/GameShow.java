package minigames.server.gameshow;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonObject;
import minigames.commands.CommandPackage;
import minigames.rendering.*;
import minigames.rendering.NativeCommands.LoadClient;

/**
 * Represents an actual GameShow game in progress
 */
public class GameShow {

    /** A logger for logging output */
    private static final Logger logger = LogManager.getLogger(GameShow.class);

    static int WIDTH = 2;
    static int HEIGHT = 2;

    record GameShowPlayer(
        String name,
        int x, int y,
        List<String> inventory
    ) {
    }

    /** Uniquely identifies this game */
    String name;

    public GameShow(String name) {
        this.name = name;
    }

    HashMap<String, GameShowPlayer> players = new HashMap<>();

    List<WordScramble> wordScrambleGames = new ArrayList<>();
    List<ImageGuesser> imageGuesserGames = new ArrayList<>();


    /** The players currently playing this game */
    public String[] getPlayerNames() {
        return players.keySet().toArray(String[]::new);
    }

    /** Metadata for this game */
    public GameMetadata gameMetadata() {
        return new GameMetadata("GameShow", name, getPlayerNames(), true);
    }

    /** Describes the state of a player */
    private String describeState(GameShowPlayer p) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%d,%d] \n\n", p.x, p.y));

        return sb.toString();
    }


    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        String cmd = cp.commands().get(0).getString("command");

        switch (cmd) {
            case "wordScramble" -> {
                wordScrambleGames.add(new WordScramble("wordList.txt"));
                int gameId = wordScrambleGames.size() - 1;
                String scrambledWord = wordScrambleGames.get(gameId).getScrambledWord();
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "startWordScramble")
                        .put("scrambledWord", scrambledWord)
                        .put("gameId", gameId));
            }
            case "imageGuesser" -> {
                imageGuesserGames.add(new ImageGuesser());
                int gameId = imageGuesserGames.size() - 1;

                String image = imageGuesserGames.get(gameId).getImageName();
                String imageFilePath = imageGuesserGames.get(gameId).getImageFileName();
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "startImageGuesser")
                        .put("image", image)
                        .put("imageFilePath", imageFilePath)
                        .put("gameId", gameId));
            }
            case "guess" -> {
                String guess = cp.commands().get(0).getString("guess");
                int gameId = (int) cp.commands().get(0).getInteger("gameId");
                boolean outcome = wordScrambleGames.get(gameId).guess(guess);
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "guessOutcome")
                        .put("outcome", outcome));
            }
            case "guessImage" -> {
                String guess = cp.commands().get(0).getString("guess");
                int gameId = (int) cp.commands().get(0).getInteger("gameId");
                boolean outcome = imageGuesserGames.get(gameId).guess(guess);
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "guessImageOutcome")
                        .put("outcome", outcome));
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
                }).map((r) -> r.toJson()).toList()
            );
        } else {
            GameShowPlayer p = new GameShowPlayer(playerName, 0, 0, List.of());
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("GameShow", "GameShow", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}
