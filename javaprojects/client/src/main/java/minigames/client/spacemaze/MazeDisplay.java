package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;;

/**
 * Class to display the maze, handle key input and do basic validation
 * before letting the server know
 *
 * @authors Niraj Rana Bhat, Andrew McKenzie
 */
public class MazeDisplay extends JPanel {

    private static final Logger logger = LogManager.getLogger(MazeDisplay.class);
    SpaceMaze spaceMaze;
    private Point playerPos;
    private Point moveTo;
    private Point exitPoint;
    private char[][] mazeMap;
    int jPanelWidth = 800;
    int jPanelHeight = 600;
    int tileWidth;
    int tileHeight;

    /**
     * Constructor for MazeDisplay
     * @param mazeArray a nested char array for the map of the maze
     * @param spaceMaze a SpaceMaze object for sending commmands
     */
    public MazeDisplay (char[][] mazeArray, SpaceMaze spaceMaze) {
        this.spaceMaze = spaceMaze;
        this.mazeMap = mazeArray;
        this.playerPos = findPlayerPos(mazeArray);
        this.moveTo = new Point(playerPos);
        this.exitPoint = findExit(mazeArray);
        this.tileWidth = jPanelWidth / mazeMap[0].length;
        this.tileHeight = jPanelHeight / mazeMap.length;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(jPanelWidth, jPanelHeight));

        //Focus Listener for Logging purposes
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                logger.info("MazeDisplay gained focus.");
            }

            @Override
            public void focusLost(FocusEvent e) {
                logger.info("MazeDisplay lost focus.");
            }
        });

        //Action Listener
        this.addKeyListener(new KeyListener() {
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
    public void requestFocusInPanel(){
        //Have to set the focus on mazePanel inorder for the key presses to work
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    /**
     * Called from SpaceMaze class when a command is received to updateMaze
     * @param mazeMap char[][] of the current maze layout
     */
    public void updateMaze(char[][] mazeMap){
        this.mazeMap = mazeMap;
        repaint();
    }

    /**
     * To be called when we start a new level.
     * @param mazeMap the new level
     */
    public void newLevel(char[][] mazeMap){
        this.mazeMap = mazeMap;
        this.playerPos = findPlayerPos(mazeMap);
        this.exitPoint = findExit(mazeMap);
        repaint();
    }

    /**
     * Called to update the recorded players postion in this MazeDisplay object
     * So we don't have to keep calling findPlayerPos()
     */
    public void updatePlayerPoint() {
        this.playerPos.x = moveTo.x;
        this.playerPos.y = moveTo.y;
    }

    /**
     * Checks if the player is on the exit
     */
    public void checkForExit() {
        if (playerPos.equals(exitPoint)) {
            logger.info("playerPos == exit");
            spaceMaze.sendCommand("onExit");
        }
    }

    /**
     * Moves the player image in the array
     */
    public void movePlayerImage() {
        mazeMap[playerPos.y][playerPos.x] = '.';
        mazeMap[moveTo.y][moveTo.x] = 'P';
        repaint();
    }

    /**
     * Handles key pressed and if the movement is valid, gets spaceMaze to send to server
     * @param e the Event
     */
    public void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                logger.info("Info up:");
                if (isMoveValid(getMazeMap(), "up")){
                    // These commands currently only tell the server where to move the player
                    spaceMaze.sendCommand("keyUp");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        // This is to get the server to send back the new maze array
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        // Moves the player image
                        movePlayerImage();
                    }
                    // Updates our recorded player point
                    updatePlayerPoint();
                    checkForExit();
                }
                break;
            case KeyEvent.VK_DOWN:
                logger.info("Info Down:");
                if (isMoveValid(getMazeMap(), "down")){
                    spaceMaze.sendCommand("keyDown");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
                    checkForExit();
                }
                break;
            case KeyEvent.VK_LEFT:
                logger.info("Info left:");
                if (isMoveValid(getMazeMap(), "left")){
                    spaceMaze.sendCommand("keyLeft");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
                    checkForExit();
                }
                break;
            case KeyEvent.VK_RIGHT:
                logger.info("Info right:");
                if (isMoveValid(getMazeMap(), "right")){
                    spaceMaze.sendCommand("keyRight");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
                    checkForExit();
                }
                break;
        }
    }

    /**
     * Renders the maze, Using paintComponent now because it sounds better if we want to
     * get more creative later. Also it's easy to update the display.
     * @param g2 Graphics object
     */
    @Override
    public void paintComponent(Graphics g2) {
        super.paintComponent(g2);

        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                charToImage(g2, r, c);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    /**
     * For setting each tile in the maze to a Colour and later an image
     * (Images will have to be loaded somewhere first)
     * @param g2 Graphics object
     * @param r row number
     * @param c column number
     */
    public void charToImage(Graphics g2, int r, int c){
        switch (mazeMap[r][c]) {
            case 'W':
                g2.setColor(Color.BLACK);
                // "c * tileWidth" to give the top left corner pixel location
                // Might have to adjust depending if the images are the same size as the grid tiles
                //g2.drawImage(wallImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'S':
                g2.setColor(Color.GRAY);
                //g2.drawImage(wallImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case '.':
                g2.setColor(Color.WHITE);
                //g2.drawImage(pathImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'K':
                g2.setColor(Color.YELLOW);
                //g2.drawImage(keyImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'P':
                g2.setColor(Color.BLUE);
                //g2.drawImage(playerImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'E':
                g2.setColor(Color.RED);
                //g2.drawImage(lockedExitImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'U':
                g2.setColor(Color.GREEN);
                //g2.drawImage(unlockedExitImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
            case 'B':
                g2.setColor(Color.ORANGE);
                //g2.drawImage(unlockedExitImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, this);
                break;
        }
    }

    /**
     * Method to find the player in the maze
     * @param mazeMap char[][] of the maze layout
     * @return a new point of the players position
     */
    public Point findPlayerPos(char[][] mazeMap) {
        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                if (mazeMap[r][c] == 'P') {
                    return new Point(c, r);
                }
            }
        }
        logger.info("No player found in the maze");
        return null; // No player in the maze
    }

    public Point findExit(char[][] mazeMap) {
        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                if (mazeMap[r][c] == 'E') {
                    return new Point(c, r);
                }
            }
        }
        logger.info("No Exit found in the maze");
        return null; // No Exit in the maze
    }

    /**
     * @return A nested char array of the maze map
     */
    public char[][] getMazeMap() {
        return this.mazeMap;
    }

    /**
     * Method to check whether a move is valid
     * Used to save time by not sending invalid moves to the server
     * @param mazeMap char[][] of the maze
     * @param direction requested direction from key input
     * @return boolean of true if valid or false if not a valid move
     */
    public boolean isMoveValid(char[][] mazeMap, String direction) {

        boolean isWallOrExit = true;

        switch(direction) {
            case "up":
                moveTo.y = playerPos.y-1;
                moveTo.x = playerPos.x;
                break;
            case "down":
                moveTo.y = playerPos.y+1;
                moveTo.x = playerPos.x;
                break;
            case "left":
                moveTo.x = playerPos.x-1;
                moveTo.y = playerPos.y;
                break;
            case "right":
                moveTo.x = playerPos.x+1;
                moveTo.y = playerPos.y;
                break;
            default:
                return false;
        }

        boolean outOfBounds = (moveTo.x < 0 || moveTo.y < 0
                || moveTo.y >= mazeMap.length || moveTo.x >= mazeMap[0].length);

        if (!outOfBounds) {
            isWallOrExit = (mazeMap[moveTo.y][moveTo.x] == 'W' || mazeMap[moveTo.y][moveTo.x] == 'E');
        }

        return (!outOfBounds && !isWallOrExit);
    }
}