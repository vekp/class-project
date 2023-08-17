package minigames.client.spacemaze;

import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Class to abstract some client side logic out of SpaceMaze
 *
 * @author Andrew McKenzie
 */
public class ClientControl {

    private MazeDisplay maze;
    private SpaceMaze spaceMaze;

    /**
     * Constructor, adds key listeners to the maze panel
     * @param maze a MazeDisplay object that the below SpaceMaze is using
     * @param spaceMaze Should be the one creating this controller
     */
    public ClientControl(MazeDisplay maze, SpaceMaze spaceMaze) {

        this.maze = maze;
        this.spaceMaze = spaceMaze;

        maze.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }

    /**
     * Handles key pressed and if the movement is valid, gets spaceMaze to send to server
     * @param e the Event
     */
    public void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (isMoveValid(maze.getMazeMap(), "up")){
                    spaceMaze.sendCommand("keyup");
                }
                break;
            case KeyEvent.VK_DOWN:
                if (isMoveValid(maze.getMazeMap(), "down")){
                    spaceMaze.sendCommand("keydown");
                }
                break;
            case KeyEvent.VK_LEFT:
                if (isMoveValid(maze.getMazeMap(), "left")){
                    spaceMaze.sendCommand("keyleft");
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (isMoveValid(maze.getMazeMap(), "right")){
                    spaceMaze.sendCommand("keyright");
                }
                break;
        }
    }

    /**
     * Method to check whether a move is valid
     * Used to save time by not sending invalid moves to the server
     * @param mazeMap char[][] of the maze
     * @param direction requested direction from key input
     * @return boolean of true if valid or false if not a valid move
     */
    public boolean isMoveValid(char[][] mazeMap, String direction) {

        Point playerPos = new Point();
        Point moveTo = new Point();

        for (int i = 0; i < mazeMap.length; i++) {
            for (int j = 0; j < mazeMap[i].length; j++) {
                if (mazeMap[i][j] == 'P') {
                    playerPos.x = i;
                    playerPos.y = j;
                }
            }
        }

        switch(direction) {
            case "up":
                moveTo.y = playerPos.y-1;
                break;
            case "down":
                moveTo.y = playerPos.y+1;
                break;
            case "left":
                moveTo.x = playerPos.x-1;
                break;
            case "right":
                moveTo.x = playerPos.x+1;
                break;
            default:
                return false;
        }

        boolean inBounds = !(moveTo.x < 0 || moveTo.y < 0
                || moveTo.y >= mazeMap.length || moveTo.x >= mazeMap[0].length);

        boolean isNotWall = mazeMap[moveTo.x][moveTo.y] != 'W';

        return (inBounds && isNotWall);
    }
}