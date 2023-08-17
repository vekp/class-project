package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Point;

import java.util.List;
import io.vertx.core.json.JsonArray;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.Box;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 *
 *
 * @authors Niraj Rana Bhat, Andrew McKenzie
 */
public class MazeDisplay extends JPanel{
    private static final Logger logger = LogManager.getLogger(MazeDisplay.class);

    JPanel mazePanel;
    JPanel elementPanel;
    SpaceMaze spaceMaze;

    JLabel countdownTimer;
    JLabel score;

    // The players position (if we go two player, does this work?)
    private Point playerPos;
    private char[][] mazeMap;

    /**
     * Constructor for MazeDisplay
     * @param mazeArray a nested char array for the map of the maze
     */
    public MazeDisplay (char[][] mazeArray, Point playerStartPos, SpaceMaze spaceMaze) {
        this.mazeMap = mazeArray;
        this.playerPos = playerStartPos;
        this.spaceMaze = spaceMaze;
        mazePanel = new JPanel();

        //Focus Listener for Logging purposes
        mazePanel.addFocusListener(new FocusListener() {
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
        mazePanel.addKeyListener(new KeyListener() {
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
        mazePanel.setFocusable(true);
        mazePanel.requestFocusInWindow();
    }

    /**
     * Handles key pressed and if the movement is valid, gets spaceMaze to send to server
     * @param e the Event
     */
    public void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                logger.info("Info KEYUP:");
                if (isMoveValid(getMazeMap(), "up")){
                    spaceMaze.sendCommand("keyUp");
                }
                break;
            case KeyEvent.VK_DOWN:
                    logger.info("Info Down:");
                if (isMoveValid(getMazeMap(), "down")){
                    spaceMaze.sendCommand("keyDown");
                }
                break;
            case KeyEvent.VK_LEFT:
                logger.info("Info left:");
                if (isMoveValid(getMazeMap(), "left")){
                    spaceMaze.sendCommand("keyLeft");
                }
                break;
            case KeyEvent.VK_RIGHT:
                logger.info("Info right:");
                if (isMoveValid(getMazeMap(), "right")){
                    spaceMaze.sendCommand("keyRight");
                }
                break;
        }
    }

    /**
     * Renders the maze
     * @param mazeMap Nested char array to display as the maze
     * @return Jpanel with the maze
     */
    public JPanel mazePanel(char[][] mazeMap){

        int jPanelWidth = 800;
        int jPanelHeight = 600;

        mazePanel.setPreferredSize(new Dimension(jPanelWidth, jPanelHeight));
        mazePanel.setLayout(new GridBagLayout());

        int tileWidth = jPanelWidth / mazeMap[0].length;
        int tileHeight = jPanelHeight / mazeMap.length;

        // Each Grid bag component associates an instance of this
        GridBagConstraints gbc = new GridBagConstraints();

        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                JLabel mazeTile = new JLabel();
                // Abstract this out at some point
                switch(mazeMap[r][c]) {
                    case 'W':
                        mazeTile.setBackground(Color.BLACK);
                        break;
                    case '.':
                        mazeTile.setBackground(Color.WHITE);
                        break;
                    case 'K':
                        mazeTile.setBackground(Color.YELLOW);
                        break;
                    case 'P':
                        mazeTile.setBackground(Color.BLUE);
                        break;
                    case 'E':
                        mazeTile.setBackground(Color.RED);
                        break;
                    case 'U':
                        mazeTile.setBackground(Color.GREEN);
                        break;
                }
                mazeTile.setOpaque(true);
                mazeTile.setPreferredSize(new Dimension(tileWidth, tileHeight));
                gbc.gridx = c;
                gbc.gridy = r;
                mazePanel.add(mazeTile, gbc);
            }
        }
        return mazePanel;
    }

    /**
     * Panel below the maze for holding maze information
     * @return Jpanel with components
     */
    public JPanel elementPanel(){
        elementPanel = new JPanel();
        elementPanel.setPreferredSize(new Dimension(800, 200));
        elementPanel.setBackground(Color.BLACK);

        //Dummy Timer display
        countdownTimer = new JLabel("Time Remaning: 0");
        countdownTimer.setForeground(Color.GREEN);
        countdownTimer.setFont(new Font("Monospaced", Font.PLAIN, 18));
        
        //Dummy score display
        score = new JLabel("Score: 0");
        score.setForeground(Color.GREEN);
        score.setFont(new Font("Monospaced", Font.PLAIN, 18));

        elementPanel.add(countdownTimer);
        elementPanel.add(score);

        return elementPanel;
    }

    //Dummy Timer
    public void updateTimer(int newTimer){
        if(newTimer >= 0){
            String myString = "Time Remaning: " + String.valueOf(newTimer);
            countdownTimer.setText(myString);
        } else {
            countdownTimer.setText("Game Over!");
        }
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

        Point playerPos = new Point();
        Point moveTo = new Point();

        boolean isWallOrExit = true;

        for (int r = 0; r < mazeMap.length; r++) {
            for (int c = 0; c < mazeMap[r].length; c++) {
                if (mazeMap[r][c] == 'P') {
                    playerPos.x = c;
                    playerPos.y = r;
                }
            }
        }

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