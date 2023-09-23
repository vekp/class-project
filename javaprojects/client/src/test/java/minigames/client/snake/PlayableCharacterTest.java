package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class PlayableCharacterTest {

    private PlayableCharacter playableCharacter;
    private GameBoard mockGameBoard;

    @BeforeEach
    public void setUp() {
        mockGameBoard = Mockito.mock(GameBoard.class);
        playableCharacter = new PlayableCharacter(mockGameBoard);
    }


    /**
     * Test that the playable character throws an exception when moving out of bounds.
     */
    @Test
    public void testMoveOutOfBounds() {
        // Simulate a move that goes out of bounds
        assertThrows(OutOfBoundsException.class, () -> {
            playableCharacter.move(Direction.LEFT);
        });
    }

    /**
     * Test that the playable character throws an exception when colliding with itself.
     */


    /**
     * Test that the playable character can grow in size.
     */
    @Test
    public void testGrow() {
        int initialBodyParts = playableCharacter.getBodyParts();
        playableCharacter.grow();
        assertEquals(initialBodyParts + 1, playableCharacter.getBodyParts());
    }

}
