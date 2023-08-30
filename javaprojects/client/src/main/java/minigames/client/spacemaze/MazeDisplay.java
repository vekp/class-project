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

import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.File;

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
    SpaceMaze spaceMaze;
    private Point playerPos;
    private Point moveTo;
    private Point exitPoint;
    public static char[][] mazeMap;
    private ArrayList<SpaceBot> bots;
    int jPanelWidth = 800;
    int jPanelHeight = 600;
    int tileWidth;
    int tileHeight;
    private int botDelay = 300;
    private String playerDirection;

    // For controlling the image displayed each cycle
    private Integer imageCycle = 1;

    // Hashmaps of various images to cycle through
    private HashMap<String, Image> playerImages = new HashMap<String, Image>();
    private HashMap<Integer, Image> wallImages = new HashMap<Integer, Image>();
    private HashMap<Integer, Image> keyImages = new HashMap<Integer, Image>();
    private HashMap<Integer, Image> lockedExitImages = new HashMap<Integer, Image>();
    private HashMap<Integer, Image> unlockedExitImages = new HashMap<Integer, Image>();

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
        this.moveTo = new Point(playerPos);
        this.exitPoint = findCharOnMap(mazeMap, 'E');
        this.tileWidth = jPanelWidth / mazeMap[0].length;
        this.tileHeight = jPanelHeight / mazeMap.length;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(jPanelWidth, jPanelHeight));
        this.bots = loadedBots;
        this.startTimer();
        this.loadImages();
        this.playerDirection = "Down";
        this.setBackground(Color.BLACK);

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
     *
     */
    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (SpaceBot bot : bots ) {
                    // moving randomly - gets stuck at deadends.
                    /*
                    Random ran = new Random();
                    bot.moveRandom(ran);
                    */
                    // always moving closer
                    bot.moveCloser(playerPos);
                   
                    detectAndSendCollisions();
                }

                if (imageCycle < 49) {
                    imageCycle++;
                } else {
                    imageCycle = 1;
                }

                repaint();
            }
            }, 0, botDelay);
    }

    /**
     *
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
    public void newLevel(char[][] mazeMap, ArrayList<SpaceBot> newBots){
        this.mazeMap = mazeMap;
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
            spaceMaze.sendCommand("onExit");
            this.stopTimer();
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

    public void handleDirection(String info, String direction, Point nextPoint){
        logger.info(info);
        if (isMoveValid(nextPoint)){
            // This command currently only tells the server where to move the player
            spaceMaze.sendCommand("key" + direction);
            if (mazeMap[moveTo.y][moveTo.x] == 'K'){
                spaceMaze.sendCommand("updateMaze");
            } else {
                // Moves the player image
                movePlayerImage();
            }
            // Updates our recorded player point
            updatePlayerPoint();
            playerDirection = direction;
            detectAndSendCollisions();
        }

    }

    /**
     * Renders the maze, Using paintComponent now because it sounds better if we want to
     * get more creative later. Also it's easy to update the display.
     * @param g2 Graphics object
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                charToImage(g, r, c);
            }
        }
        for (SpaceBot bot : bots ) {
            Point botLoc = new Point(bot.getLocation());
            g.drawImage(bot.getBotImage(), botLoc.x * tileWidth, botLoc.y * tileHeight, tileWidth, tileHeight, null);
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
                g2.drawImage(wallImages.get((r+c)%4), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'S':
                g2.setColor(Color.BLACK);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                break;
            case '.':
                g2.setColor(Color.BLACK);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                break;
            case 'K':
                g2.drawImage(keyImages.get(imageCycle % keyImages.size()),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'P':
                g2.drawImage(playerImages.get(playerDirection), c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'E':
                g2.drawImage(lockedExitImages.get(imageCycle % lockedExitImages.size()),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'U':
                g2.drawImage(unlockedExitImages.get(imageCycle % unlockedExitImages.size()),
                        c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
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

    public void loadImages(){

        // Player images
        Image playerImage1 = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aUp.png")).getImage();
        Image playerImage2 = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aDown.png")).getImage();
        Image playerImage3 = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aRight.png")).getImage();
        Image playerImage4 = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aLeft.png")).getImage();
        playerImages.put("Up", playerImage1);
        playerImages.put("Down", playerImage2);
        playerImages.put("Right", playerImage3);
        playerImages.put("Left", playerImage4);

        // Wall images
        Image wallImage1 = new ImageIcon(getClass().getResource("/images/spacemaze/asteriodNoB1.png")).getImage();
        Image wallImage2 = new ImageIcon(getClass().getResource("/images/spacemaze/asteriodNoB2.png")).getImage();
        Image wallImage3 = new ImageIcon(getClass().getResource("/images/spacemaze/asteriodNoB3.png")).getImage();
        Image wallImage4 = new ImageIcon(getClass().getResource("/images/spacemaze/asteriodNoB4.png")).getImage();
        wallImages.put(0, wallImage1);
        wallImages.put(1, wallImage2);
        wallImages.put(2, wallImage3);
        wallImages.put(3, wallImage4);

        // Key images
        Image keyImage1 = new ImageIcon(getClass().getResource("/images/spacemaze/KeyNoB1a.png")).getImage();
        Image keyImage2 = new ImageIcon(getClass().getResource("/images/spacemaze/keyNoB1.png")).getImage();
        keyImages.put(0, keyImage1);
        keyImages.put(1, keyImage2);

        // Locked exit images
        Image lockedImage1 = new ImageIcon(getClass().getResource("/images/spacemaze/LockedExitNoB1.png")).getImage();
        Image lockedImage2 = new ImageIcon(getClass().getResource("/images/spacemaze/LockedExitNoB2.png")).getImage();
        Image lockedImage3 = new ImageIcon(getClass().getResource("/images/spacemaze/LockedExitNoB3.png")).getImage();
        lockedExitImages.put(0, lockedImage1);
        lockedExitImages.put(1, lockedImage2);
        lockedExitImages.put(2, lockedImage3);

        // Unlocked exit images
        Image unlockedImage1 = new ImageIcon(getClass().getResource("/images/spacemaze/UnlockedExitNoB1.png")).getImage();
        Image unlockedImage2 = new ImageIcon(getClass().getResource("/images/spacemaze/UnlockedExitNoB2.png")).getImage();
        Image unlockedImage3 = new ImageIcon(getClass().getResource("/images/spacemaze/UnlockedExitNoB3.png")).getImage();
        unlockedExitImages.put(0, unlockedImage1);
        unlockedExitImages.put(1, unlockedImage2);
        unlockedExitImages.put(2, unlockedImage3);
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
    /*
     * Method for detecting collisions between bots and players.
     * 
     */
    public void detectAndSendCollisions() {

        // iterate through the bots and compare position with the players.
            for (SpaceBot bot : bots) {
                Point botPosition = bot.getLocation();

                if(playerPos.equals(botPosition)) {
                    logger.info("Collision detected");
                    spaceMaze.sendCommand("botCollision");
                }
                
            }
    }
}