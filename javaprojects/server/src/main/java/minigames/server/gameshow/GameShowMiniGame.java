package minigames.server.gameshow;

/** Indicates a class is a minigame for GameShow (at this point, just means it can process a guess) */
public interface GameShowMiniGame {

    public boolean guessIsCorrect(String guess);

}
