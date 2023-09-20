package minigames.client.snake;

import java.awt.Point;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * Represents the Snake in the Snake game.
 */
public class Snake {

    /**
     * LinkedList representing the snake's body.
     */
    private final LinkedList<Point> body;

    /**
     * HashSet to quickly check for collisions.
     */
    private final HashSet<Point> bodySet;

    /**
     * Current direction of the snake.
     */
    private Direction direction;

    /**
     * Flag indicating if the snake is alive.
     */
    private boolean isAlive;

    /**
     * Initializes a new Snake object.
     */
    public Snake() {
        this.body = new LinkedList<>();
        this.bodySet = new HashSet<>();
        Point startingPosition = new Point(10, 10);
        body.add(startingPosition);
        bodySet.add(startingPosition);
        this.direction = Direction.RIGHT;
        this.isAlive = true;
    }

    /**
     * Gets the body of the snake.
     *
     * @return LinkedList of Points representing the snake's body.
     */
    public LinkedList<Point> getBody() {
        return this.body;
    }

    /**
     * Gets the current direction of the snake.
     *
     * @return Direction enum indicating current direction.
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Checks if the snake is alive.
     *
     * @return True if alive, false otherwise.
     */
    public boolean isAlive() {
        return this.isAlive;
    }

    /**
     * Sets the alive status of the snake.
     *
     * @param alive New alive status.
     */
    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    /**
     * Sets a new direction for the snake.
     *
     * @param newDirection New Direction enum value.
     */
    public void setDirection(Direction newDirection) {
        if (!isOppositeDirection(newDirection)) {
            this.direction = newDirection;
        }
    }

    /**
     * Checks if the new direction is opposite to the current direction.
     *
     * @param newDirection New direction to check.
     * @return True if it's the opposite, false otherwise.
     */
    private boolean isOppositeDirection(Direction newDirection) {
        return this.direction.ordinal() == (newDirection.ordinal() + 2) % 4;
    }

    /**
     * Moves the snake one unit in its current direction.
     */
    public void move() {
        Point head = (Point) this.body.getFirst().clone();

        switch (this.direction) {
            case UP: head.translate(0, -1); break;
            case DOWN: head.translate(0, 1); break;
            case LEFT: head.translate(-1, 0); break;
            case RIGHT: head.translate(1, 0); break;
        }

        this.bodySet.remove(this.body.getLast());  // Remove tail from set before checking collision
        this.body.addFirst(head);

        if (!bodySet.add(head)) {  // If the head is already in the set, collision occurs
            this.isAlive = false;
            return;
        }

        this.body.removeLast();  // Remove tail from the linked list as it moved
    }

    public void grow() {
        Point tail = (Point) this.body.getLast().clone();
        this.body.addLast(tail);
        this.bodySet.add(tail);  // Safe to add as the snake has just eaten food
    }


    /**
     * Checks if the snake is colliding with itself.
     *
     * @return True if there's a collision, false otherwise.
     */
    public boolean isCollidingWithSelf() {
        return this.bodySet.size() != this.body.size();
    }

    /**
     * Checks if the snake is colliding with the wall.
     *
     * @param width Width of the grid.
     * @param height Height of the grid.
     * @return True if there's a collision, false otherwise.
     */
    public boolean isCollidingWithWall(int width, int height) {
        Point head = this.body.getFirst();
        return head.x < 0 || head.x >= width || head.y < 0 || head.y >= height;
    }
}
