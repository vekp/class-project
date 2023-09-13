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

    private static final int MAX_PLAYERS = 4;

    /** A class to hold the state of players */
    private static class GameShowPlayer {
        private final String name;
        private int score;
        private boolean ready;

        public GameShowPlayer(String name) {
            this.name = name;
            this.score = 0;
            this.ready = false;
        }

        public String name() { return this.name; }
        public int score() { return this.score; }
        public boolean isReady() { return this.ready; }

        public void addToScore(int amount) { this.score += amount; }
        public void toggleReady() { this.ready = !this.ready; }

        public boolean equals(GameShowPlayer p) {
            return this.name.equals(p.name());
        }

        @Override
        public String toString() {
            return this.name + ": " + this.score;
        }
    }

    /** Uniquely identifies this game */
    String name;

    /** Holds the actual minigame instances to be played in a given instance of GameShow */
    private List<GameShowMiniGame> games;

    /** Has the game started? */
    private boolean inProgress;

    public GameShow(String name) {
        this.name = name;
        this.inProgress = false;
        this.initialiseGames();
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

    /** Are all players ready to start? */
    private boolean allPlayersReady() {
        boolean allReady = true;

        for (GameShowPlayer p : players.values()) {
            if (!p.isReady()) {
                allReady = false;
                break;
            }
        }

        return allReady;
    }

    /** Minigame instances are created on instantiation of the GameShow game; all players play the same puzzles */
    private void initialiseGames() {
        this.games = new ArrayList<>();
        this.games.add(new ImageGuesser());
        this.games.add(new WordScramble("words_easy.txt"));
        this.games.add(new ImageGuesser());
        this.games.add(new WordScramble("words_medium.txt"));
        this.games.add(new ImageGuesser());
        this.games.add(new WordScramble("words_hard.txt"));
    }

    /** Progress the game to the next round */
    public JsonObject nextRound(int round) {
        JsonObject commands = new JsonObject();
        String minigameName = this.games.get(round).getClass().getSimpleName();

        commands.put("command", "nextRound").put("minigame", minigameName);

        switch (minigameName) {
            case "ImageGuesser" -> {
                ImageGuesser minigame = (ImageGuesser) this.games.get(round);
                String image = minigame.getImageName();
                String imageFilePath = minigame.getImageFileName();

                commands.put("image", image).put("imageFilePath", imageFilePath).put("round", round);
            }
            case "WordScramble" -> {
                WordScramble minigame = (WordScramble) this.games.get(round);
                String letters = minigame.getScrambledWord();

                commands.put("letters", letters).put("round", round);
            }
        }

        return commands;
    }

    public RenderingPackage runCommands(CommandPackage cp) {
        logger.info("Received command package {}", cp);

        ArrayList<JsonObject> renderingCommands = new ArrayList<>();

        JsonObject msg = cp.commands().get(0);

        switch (msg.getString("command")) {
            case "ready" -> {
                players.get(cp.player()).toggleReady();
                if (allPlayersReady()) { // Log (for testing purposes)
                    logger.info("All players in game '{}' are ready", this.name);
                    this.inProgress = true;
                    renderingCommands.add(nextRound(0));
                } else { // Log (for testing purposes)
                    logger.info("There are players in game '{}' who are not yet ready", this.name);
                }
            }
            case "nextRound" -> renderingCommands.add(nextRound(cp.commands().get(0).getInteger("round")));
            case "startGame" -> {
                switch (msg.getString("game")) {
                    case "wordScramble" -> {
                        String fileName = "words_"
                            + cp.commands().get(0).getString("difficulty")
                            + ".txt";
                        wordScrambleGames.add(new WordScramble(fileName));
                        int gameId = wordScrambleGames.size() - 1;
                        String letters = wordScrambleGames
                            .get(gameId).getScrambledWord();
                        renderingCommands.
                            add(new JsonObject()
                                .put("command", "startGame")
                                .put("game", "wordScramble")
                                .put("letters", letters)
                                .put("gameId", gameId));
                    }
                }
                
            }
            case "imageGuesser" -> {
                imageGuesserGames.add(new ImageGuesser());
                int gameId = imageGuesserGames.size() - 1;

                String image = imageGuesserGames.get(gameId).getImageName();
                String imageFilePath = imageGuesserGames
                    .get(gameId).getImageFileName();
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "startImageGuesser")
                        .put("image", image)
                        .put("imageFilePath", imageFilePath)
                        .put("gameId", gameId));
            }
            case "guess" -> {
                String guess = msg.getString("guess");

                switch (msg.getString("game")) {
                    case "wordScramble" -> {
                        boolean correct = games.get(msg.getInteger("round")).guessIsCorrect(guess);
                        renderingCommands.
                            add(new JsonObject()
                                .put("command", "guessOutcome")
                                .put("game", "wordScramble")
                                .put("correct", correct));
                    }
                }
            }
            case "guessImage" -> {
                String guess = cp.commands().get(0).getString("guess");
                logger.info("Processing Image Guesser guess '{}' at Round {}", new Object[] {
                        guess, cp.commands().get(0).getInteger("round")
                });
                boolean outcome = this.games.get(cp.commands().get(0).getInteger("round")).guessIsCorrect(guess);
                renderingCommands.
                    add(new JsonObject()
                        .put("command", "guessImageOutcome")
                        .put("outcome", outcome));
            }
            case "quit" -> {
                players.remove(cp.player());
                logger.info("Player '{}' has quit GameShow '{}'", new Object[] { cp.player(), this.name });
                renderingCommands.add(new NativeCommands.QuitToMenu().toJson());
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
        } else if (this.inProgress) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("Game already started")
                    }).map((r) -> r.toJson()).toList()
            );
        } else if (this.players.size() == MAX_PLAYERS) {
            return new RenderingPackage(
                    gameMetadata(),
                    Arrays.stream(new RenderingCommand[]{
                            new NativeCommands.ShowMenuError("Game is full")
                    }).map((r) -> r.toJson()).toList()
            );
        } else {
            GameShowPlayer p = new GameShowPlayer(playerName);
            players.put(playerName, p);

            ArrayList<JsonObject> renderingCommands = new ArrayList<>();
            renderingCommands.add(new LoadClient("GameShow", "GameShow", name, playerName).toJson());

            return new RenderingPackage(gameMetadata(), renderingCommands);
        }

    }

}
