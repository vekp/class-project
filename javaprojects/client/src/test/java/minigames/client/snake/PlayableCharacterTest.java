package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayableCharacterTest {
    private PlayableCharacter snake;
    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        gameBoard = mock(GameBoard.class);
        snake = new PlayableCharacter(gameBoard);
    }

    /**
     * Test for initial direction of the snake.
     */
    @Test
    void testInitialDirection() {
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    /**
     * Test for setting the direction of the snake.
     */
    @Test
    void testSetDirection() {
        snake.setDirection(Direction.UP);
        assertEquals(Direction.UP, snake.getDirection());
    }

    /**
     * Test for snake moving out of bounds.
     */
    @Test
    void testOutOfBoundsMove() {
        when(gameBoard.getWidth()).thenReturn(10);
        when(gameBoard.getHeight()).thenReturn(10);

        snake.setDirection(Direction.RIGHT);

        assertThrows(OutOfBoundsException.class, () -> {
            for (int i = 0; i < 15; i++) {
                snake.move(snake.getDirection());
            }
        });
    }

    /**
     * Test for snake colliding with itself.
     */
    @Test
    @Disabled
    void testSelfCollision() {
        when(gameBoard.getItemTypeAt(anyInt(), anyInt())).thenReturn(ItemType.SNAKE);

        assertThrows(SelfCollisionException.class, () -> snake.move(Direction.RIGHT));
    }

    /**
     * Test for snake colliding with food.
     */
    @Test
    @Disabled
    void testFoodCollision() {
        when(gameBoard.getItemTypeAt(anyInt(), anyInt())).thenReturn(ItemType.APPLE);

        assertThrows(FoodCollisionException.class, () -> snake.move(Direction.RIGHT));
    }
}
