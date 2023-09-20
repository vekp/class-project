package minigames.client.useraccount;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {
    @JsonProperty("name")
    private String name;

    @JsonProperty("score")
    private String score;

    public Game(String name, String value) {
        this.name = name;
        this.score = value;
    }

    public Game() {}

    public String getName() {
        return this.name;
    }

    public String getScore() {
        return this.score;
    }
}