package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for the status bar in the bottom of the window
 *
 * @author Niraj Rana Bhat
 */
public class StatusBar extends JPanel {

    private static final Logger logger = LogManager.getLogger(StatusBar.class);

    JPanel statusBar;
    JLabel gameTimer;
    JLabel score;
    JLabel level;
    JLabel tipLabel;
    Timer timer;
    SpaceMaze spaceMaze;
    Font customFont;

    //Lives Panel
    JPanel livesPanel; //Main container to display lives
    JLabel livesRemaining;
    JPanel lifeImage;  //sub containers for individual lives
    JPanel lifeImage1;
    JPanel lifeImage2;
    JPanel lifeImage3;
    JPanel lifeImage4;
    

    /**
     * Constructor for the Status Bar
     * @param spaceMaze the client side controller to receive messages from here
     */
    public StatusBar(SpaceMaze spaceMaze) {
        this.spaceMaze = spaceMaze;
        customFont = spaceMaze.getCustomFont();
        statusBar();
        timer = new Timer();
    }

    /**
     * Panel below the maze for holding maze information
     *
     * @return Jpanel with components
     */
    public JPanel statusBar() {

        //Sample Lives remaining display, Need to fetch no. of lives remaining from the server,
        //and loop the Image labels 
        //Todo, implement loops 
        customFont = customFont.deriveFont(13f);
        livesPanel = new JPanel(new FlowLayout());
        livesPanel.setPreferredSize(new Dimension(120, 40));
        livesPanel.setBackground(Color.BLACK);
        livesRemaining = new JLabel("lives: ", SwingConstants.LEFT);
        livesRemaining.setFont(customFont);
        livesRemaining.setForeground(Color.WHITE);
        livesPanel.add(livesRemaining);

        
        try {
            ImageIcon lifeImage = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aUp.png"));
            Image image = lifeImage.getImage();
            Image transform = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            lifeImage = new ImageIcon(transform);
            JLabel picLabel = new JLabel(lifeImage);
            JLabel picLabel1 = new JLabel(lifeImage);
            JLabel picLabel2 = new JLabel(lifeImage);
            JLabel picLabel3 = new JLabel(lifeImage);

            livesPanel.add(picLabel);
            livesPanel.add(picLabel1);
            livesPanel.add(picLabel2);
            livesPanel.add(picLabel3);

        } catch (Exception e){
            logger.error(e);
        }

        statusBar = new JPanel(new GridBagLayout());
        statusBar.setPreferredSize(new Dimension(800, 180));
        statusBar.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        //Dummy score display (Possibly only update this between levels?)
        score = new JLabel("Score: 0", SwingConstants.LEFT);
        score.setForeground(Color.WHITE);
        score.setFont(customFont);
        gbc.insets = new Insets(-100, 20, 0, 0);
        gbc.gridx = 0;
        statusBar.add(score, gbc);

        //Initial time when game is launched
        gameTimer = new JLabel("Timer: 0", SwingConstants.CENTER);
        gameTimer.setForeground(Color.WHITE);
        gameTimer.setFont(customFont);
        gbc.insets = new Insets(-100, 0, 0, 0);
        gbc.gridx = 1;
        statusBar.add(gameTimer, gbc);

        //Tracks the level the player is on
        gbc.insets = new Insets(-100, 0, 0, 20);
        level = new JLabel("Level: 1 ", SwingConstants.RIGHT);
        level.setForeground(Color.WHITE);
        level.setFont(customFont);
        gbc.gridx = 2;
        statusBar.add(level, gbc);

        //Lives Panel
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 0);
        statusBar.add(livesPanel, gbc);


        return statusBar;
    }

    /**
     * @return Returns the Status Bar JPanel
     */
    public JPanel getStatusBar() {
        return statusBar;
    }

    /**
     * Method to update the JLabel with the timer
     * @param currentTime time the game has been running as a String
     */
    public void updateTimer(String currentTime) {
            gameTimer.setText("Timer: " + currentTime + "  ");
            statusBar.revalidate();
            statusBar.repaint();
    }

    /**
     * Method ot update the JLabel with the current level
     * @param currentLevel int for current level
     */
    public void updateLevel(String currentLevel) {
        level.setText("Level: " + currentLevel + "  ");
    }

    /**
     * Method to update the displayed score
     * @param newScore String of the current score
     */
    public void updateScore(String newScore) {
        if (Integer.parseInt(newScore) < 0) {
            score.setText("Score: 0 ");
        } else {
            score.setText("Score: " + newScore + " ");
        }
        statusBar.revalidate();
        statusBar.repaint();
    }

    /**
     * Starts sending the command to the server every second to get the current time
     * This way we are in-sync with the server when the game is paused etc
     */
    public void startTimer(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                spaceMaze.sendCommand("gameTimer");
            }
        }, 0, 1000);
    }

    /**
     * Stops sending the command to the server to get the current time.
     * Called when the client receives gameOver command
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}