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

import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.File;

import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to display the maze, handle key input and do basic validation
 * before letting the server know
 *
 * @authors Niraj Rana Bhat, Andrew McKenzie
 */
public class MazeDisplay extends JPanel {

    private static final Logger logger = LogManager.getLogger(MazeDisplay.class);
    SpaceMaze spaceMaze;
    private Image playerImage;
    private Point playerPos;
    private Point moveTo;
    private Point exitPoint;
    public static char[][] mazeMap;
    private ArrayList<SpaceBot> bots;
    int jPanelWidth = 800;
    int jPanelHeight = 600;
    int tileWidth;
    int tileHeight;
    int botDelay = 800;

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
        /*
        // Starting the bot timers here.
        for (SpaceBot bot : bots) {
            bot.startTimer(1000);
        }*/

        playerImage = new ImageIcon(getClass().getResource("/images/spacemaze/alien1a.png")).getImage();


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
                    bot.moveBot();
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
        this.bots =
        this.bots = newBots;
        this.startTimer();
        /*
        // Starting the bot timers here.
        for (SpaceBot bot : bots) {
            bot.startTimer(1000);
        }*/

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
            /*
            // Stopping the old bot timers.
            for (SpaceBot bot : bots) {
                bot.stopTimer();
            }*/
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
                logger.info("Info up:");
                nextPoint = moveTo("up");
                if (isMoveValid(nextPoint)){
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
                }
                break;
            case KeyEvent.VK_DOWN:
                logger.info("Info Down:");
                nextPoint = moveTo("down");
                if (isMoveValid(nextPoint)){
                    spaceMaze.sendCommand("keyDown");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
                }
                break;
            case KeyEvent.VK_LEFT:
                logger.info("Info left:");
                nextPoint = moveTo("left");
                if (isMoveValid(nextPoint)){
                    spaceMaze.sendCommand("keyLeft");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
                }
                break;
            case KeyEvent.VK_RIGHT:
                logger.info("Info right:");
                nextPoint = moveTo("right");
                if (isMoveValid(nextPoint)){
                    spaceMaze.sendCommand("keyRight");
                    if (mazeMap[moveTo.y][moveTo.x] == 'K') {
                        spaceMaze.sendCommand("updateMaze");
                    } else {
                        movePlayerImage();
                    }
                    updatePlayerPoint();
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                charToImage(g, r, c);
            }
        }
        for (SpaceBot bot : bots ) {
            Point botLoc = new Point(bot.getLocation());
            g.setColor(Color.WHITE);
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
                g2.setColor(Color.BLACK);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                // "c * tileWidth" to give the top left corner pixel location
                //g2.drawImage(wallImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'S':
                g2.setColor(Color.WHITE);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                //g2.drawImage(wallImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case '.':
                g2.setColor(Color.WHITE);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                //g2.drawImage(pathImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'K':
                g2.setColor(Color.YELLOW);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                //g2.drawImage(keyImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'P':
                g2.setColor(Color.WHITE);
                g2.drawImage(playerImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'E':
                g2.setColor(Color.RED);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                //g2.drawImage(lockedExitImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
                break;
            case 'U':
                g2.setColor(Color.GREEN);
                g2.fillRect(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                //g2.drawImage(unlockedExitImage, c * tileWidth, r * tileHeight, tileWidth, tileHeight, null);
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
}