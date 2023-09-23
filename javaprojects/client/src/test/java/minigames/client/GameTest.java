package minigames.client.useraccount;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void testParameterizedConstructor() {
        String name = "TestGame";
        String score = "100";

        Game game = new Game(name, score);

        // Verify that the parameterized constructor sets the name and score correctly
        assertEquals(name, game.getName());
        assertEquals(score, game.getScore());
    }

    @Test
    void testDefaultConstructor() {
        Game game = new Game();

        // Verify that the default constructor initializes the name and score to null
        assertNull(game.getName());
        assertNull(game.getScore());
    }

}