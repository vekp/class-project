package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.RenderingHints.Key;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.HashMap;

/**
 * Class to display the maze, handle key input and do basic validation
 * before letting the server know
 *
 * @authors Niraj Rana Bhat, Andrew McKenzie
 */
public class MazeDisplay extends JPanel {

    private static final Logger logger = LogManager.getLogger(MazeDisplay.class);

    // The client side controller for sending commands to server
    SpaceMaze spaceMaze;

    // The maze map for the static valid move method
    public static char[][] mazeMap;

    // Maze Points needed in this class
    private Point playerPos;
    private Point moveTo;
    private Point exitPoint;
    private Point startPos;

    // ArrayList of SpaceBot objects in the maze
    private ArrayList<SpaceBot> bots;
    // Delay in milliseconds between bot movement 300
    private int botDelay = 400;

    // Game window and tile dimensions
    private int jPanelWidth = 800;
    private int jPanelHeight = 600;
    private int tileWidth;
    private int tileHeight;

    // Direction of the player, used for selecting player image
    private String playerDirection;

    // For controlling the image displayed each cycle
    private Integer imageCycle = 1;

    // Timer for the game automation
    Timer timer;

    /**
     * Constructor for MazeDisplay
     * @param mazeArray a nested char array for the map of the maze
     * @param spaceMaze a SpaceMaze object for sending commmands
     */
    public MazeDisplay (char[][] mazeArray, SpaceMaze spaceMaze, ArrayList<SpaceBot> loadedBots) {
        this.spaceMaze = spaceMaze;
        this.mazeMap = mazeArray;
        this.playerPos = findCharOnMap(mazeMap, 'P');
        this.startPos = findCharOnMap(mazeMap, 'P');
        this.moveTo = new Point(playerPos);
        this.exitPoint = findCharOnMap(mazeMap, 'E');
        this.tileWidth = jPanelWidth / mazeMap[0].length;
        this.tileHeight = (jPanelHeight -1) / mazeMap.length;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(jPanelWidth, jPanelHeight));
        this.bots = loadedBots;
        this.startTimer();
        this.playerDirection = "Down";
        this.setBackground(Color.BLACK);

        // Focus Listener for Logging purposes
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

        // Action Listener
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
     * Timer for automating parts of the game
     * Primarily the bots movement
     */
    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (SpaceBot bot : bots ) {
                    bot.move(playerPos);
                    detectAndSendCollisions();
                }
                // For controlling the cycling of images with multiple pngs
                if (imageCycle < 60) {
                    imageCycle++;
                } else {
                    imageCycle = 1;
                }
                repaint();
            }
            }, 0, botDelay);
    }

    /**
     * Stops the timer that automates parts of the game
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * Handles key pressed and if the movement is valid, gets spaceMaze to send to server
     * @param e the Event
     */
    public void requestFocusInPanel(){
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    /**
     * Called from SpaceMaze class when a command is received to updateMaze
     * @param mazeMap char[][] of the current maze layout
     */
    public void updateMaze(char[][] mazeArray){
        this.mazeMap = mazeArray;
        playerPos = findCharOnMap(mazeArray, 'P');
        mazeMap[startPos.y][startPos.x] = 'S';
        repaint();
    }

    /**
     * To be called when we start a new level.
     * @param mazeMap the new level
     */
    public void newLevel(char[][] mazeArray, ArrayList<SpaceBot> newBots){
        this.mazeMap = mazeArray;
        this.startPos = findCharOnMap(mazeMap, 'P');
        this.playerPos = findCharOnMap(mazeMap, 'P');
        this.exitPoint = findCharOnMap(mazeMap, 'E');
        this.bots = newBots;
        this.startTimer();
        repaint();
    }

    /**
     * Called to update the recorded players postion in this MazeDisplay object
     * So we don't have to keep calling findPlayerPos()
     */
    public void updatePlayerPoint() {
        this.playerPos.x = moveTo.x;
        this.playerPos.y = moveTo.y;

        if (playerPos.equals(exitPoint)) {
            logger.info("playerPos == exit");
            SpaceMazeSound.play("newlevel");
            spaceMaze.sendCommand("onExit");
            this.stopTimer();
        }
    }

    /**
     * Moves the player image in the array
     */
    public void movePlayerImage() {
        if (playerPos.equals(startPos)) {
            mazeMap[playerPos.y][playerPos.x] = 'S';
            mazeMap[moveTo.y][moveTo.x] = 'P';
        } else {
            mazeMap[playerPos.y][playerPos.x] = '.';
            mazeMap[moveTo.y][moveTo.x] = 'P';
        }
        repaint();
    }

    /**
     * Handles key pressed and if the movement is valid, gets spaceMaze to send to server
     * @param e the Event
     */
    public void handleKeyPressed(KeyEvent e) {
        Point nextPoint;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                nextPoint = moveTo("up");
                handleDirection("Info Up: ", "Up", nextPoint);
                break;
            case KeyEvent.VK_DOWN:
                nextPoint = moveTo("down");
                handleDirection("Info Down: ", "Down", nextPoint);
                break;
            case KeyEvent.VK_LEFT:
                nextPoint = moveTo("left");
                handleDirection("Info Left: ", "Left", nextPoint);
                break;
            case KeyEvent.VK_RIGHT:
                nextPoint = moveTo("right");
                handleDirection("Info Right: ", "Right", nextPoint);
                break;
            case KeyEvent.VK_W:
                nextPoint = moveTo("up");
                handleDirection("Info Up: ", "Up", nextPoint);
                break;
            case KeyEvent.VK_S:
                nextPoint = moveTo("down");
                handleDirection("Info Down: ", "Down", nextPoint);
                break;
            case KeyEvent.VK_D:
                nextPoint = moveTo("right");
                handleDirection("Info Right: ", "Right", nextPoint);
                break;
            case KeyEvent.VK_A:
                nextPoint = moveTo("left");
                handleDirection("Info Left: ", "Left", nextPoint);
                break;
        }
    }

    /**
     * Method to handle the process when the player attempts to move.
     * @param info String for logging purposes
     * @param direction Direction the player is moving
     * @param nextPoint Next point the player is moving to
     */
    public void handleDirection(String info, String direction, Point nextPoint){
        logger.info(info);
        if (isMoveValid(nextPoint)){
            // This command currently only tells the server where the player is moving
            spaceMaze.sendCommand("playerMoved" + direction);
            if ((mazeMap[moveTo.y][moveTo.x] != '.') && (mazeMap[moveTo.y][moveTo.x] != 'U')){
                playSound();
                spaceMaze.sendCommand("collision");
            } else {
                // Moves the player image
                movePlayerImage();
            }
            // Updates our recorded player point
            updatePlayerPoint();
            // For the player image direction
            playerDirection = direction;
            // Checking for collisions
            detectAndSendCollisions();
        }

    }

    /**
     * Renders the maze and bots
     * @param g Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draws the maze and components
        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                charToImage(g, r, c);
            }
        }
        // Draws the bots
        for (SpaceBot bot : bots ) {
            Point botLoc = new Point(bot.getLocation());
            g.drawImage(bot.getBotImage(), botLoc.x * tileWidth, botLoc.y * tileHeight, tileWidth, tileHeight, null);
        }
    }

    /**
     * For setting each tile in the maze to an image
     * @param g Graphics object
     * @param r row number
     * @param c column number
     */
    public void charToImage(Graphics g, int r, int c){
        switch (mazeMap[r][c]) {
            case 'W':
                g.drawImage(Images.getImage("wallImages", (r+c) % 4), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'S':
                g.drawImage(Images.getImage("startImage"), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case '.':
                g.setColor(Color.BLACK);
                g.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                break;
            case 'K':
                g.drawImage(Images.getImage("keyImages", imageCycle % Images.getSize("keyImages")),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'P':
                g.drawImage(Images.getPlayerImage(playerDirection), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'E':
                g.drawImage(Images.getImage("lockedImages", imageCycle % Images.getSize("lockedImages")),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'U':
                g.drawImage(Images.getImage("unlockedImages", imageCycle % Images.getSize("unlockedImages")),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'M':
                g.drawImage(Images.getImage("bombImages", imageCycle % Images.getSize("bombImages")),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case '$':
                g.drawImage(Images.getImage("chestImage"), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'H':
                g.drawImage(Images.getImage("wormHoleImage"), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
        }
    }

    /**
     * Method to find a char in the maze
     * @param mazeMap char[][] of the maze layout
     * @param letter the char to find
     * @return a new point of the chars position
     */
    public Point findCharOnMap(char[][] mazeMap, char letter) {
        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                if (mazeMap[r][c] == letter) {
                    return new Point(c, r);
                }
            }
        }
        logger.info("character not found in the maze");
        return null;
    }

    /**
     * @return A nested char array of the maze map
     */
    public char[][] getMazeMap() {
        return this.mazeMap;
    }

    /**
     * Method to update the nextPoint
     * @param direction String of the direction the object is moving
     * @return new Point
     */
    public Point moveTo(String direction) {
        switch (direction) {
            case "up":
                moveTo.y = playerPos.y - 1;
                moveTo.x = playerPos.x;
                break;
            case "down":
                moveTo.y = playerPos.y + 1;
                moveTo.x = playerPos.x;
                break;
            case "left":
                moveTo.x = playerPos.x - 1;
                moveTo.y = playerPos.y;
                break;
            case "right":
                moveTo.x = playerPos.x + 1;
                moveTo.y = playerPos.y;
                break;
        }
        return moveTo;
    }

    /**
     * Method to play a sound based on collision character
     */
    private void playSound() {
        switch (mazeMap[moveTo.y][moveTo.x]) {
            case 'K':
                SpaceMazeSound.play("key");
                break;
            case '$':
                SpaceMazeSound.play("chest");
                break;
            case 'H':
                SpaceMazeSound.play("wormhole");
                break;
            case 'M':
                SpaceMazeSound.play("bomb");
                break;
            default:
                // do nothing
        }
    }

    /**
     * Method to check whether a move is valid
     * Used to save time by not sending invalid moves to the server
     * @param moveTo point to move to
     * @return boolean of true if valid or false if not a valid move
     */
    public static boolean isMoveValid(Point moveTo) {

        boolean isWallOrExit = true;

        boolean outOfBounds = (moveTo.x < 0 || moveTo.y < 0
                || moveTo.y >= mazeMap.length || moveTo.x >= mazeMap[0].length);

        if (!outOfBounds) {
            isWallOrExit = (mazeMap[moveTo.y][moveTo.x] == 'W' || mazeMap[moveTo.y][moveTo.x] == 'E');
        }

        return (!outOfBounds && !isWallOrExit);
    }

    /**
     * Method for detecting collisions between bots and players.
     */
    public void detectAndSendCollisions() {
        // iterate through the bots and compare position with the players.
            for (SpaceBot bot : bots) {
                Point botPosition = bot.getLocation();

                if(playerPos.equals(botPosition)) {
                    logger.info("Collision detected");
                    SpaceMazeSound.play("lifedown");
                    spaceMaze.sendCommand("botCollision");
                    bot.reset();
                }
            }
    }
}