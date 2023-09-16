package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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

    JPanel statusBar; //Main Status Bar Panel

    JLabel gameTimer; //JLabel to display time taken
    JLabel score;     //JLabel to display score
    JLabel level;     //JLabel to display level

    Timer timer;
    SpaceMaze spaceMaze;
    Font customFont;  //Our custom font

    //Lives Panel
    JPanel livesPanel; //Main container to display lives
    JLabel livesRemaining; //A JLabel that contains just - "lives:"
    JPanel livesOnlyPanel; //Lives Only Panel - A panel to hold each individual lives remaining images.

    ImageIcon lifeImage; //ImageIcon for the spaceship image, that acts as lives.

    //Interactive Display section
    JPanel interactiveTextPanel; //Panel to display at the bottom of the status bar some interactive texts. 
    JLabel interactiveText; //texts that get updated depending on the events occurring of the game and player.

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
     * @return JPanel with components
     */
    public JPanel statusBar() {

        //Lives remaining display
        customFont = customFont.deriveFont(13f);
        livesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        livesPanel.setPreferredSize(new Dimension(250, 40));
        livesPanel.setBackground(Color.BLACK);

        //Components inside lives panel
        livesRemaining = new JLabel("lives:", SwingConstants.LEFT);
        livesRemaining.setFont(customFont);
        livesRemaining.setForeground(Color.WHITE);

        livesPanel.add(livesRemaining); //Adding "lives:" inside livesPanel 

        livesOnlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        livesOnlyPanel.setPreferredSize(new Dimension(180, 40));
        livesOnlyPanel.setBackground(Color.BLACK);

        livesPanel.add(livesOnlyPanel); //Adding panel that stores each individual lives inside livesPanel.

        //Setting up interactive text panel
        interactiveTextPanel = getInteractiveTextPanel();
        
        try {
            lifeImage = new ImageIcon(getClass().getResource("/images/spacemaze/spaceShip2aUp.png"));
            Image image = lifeImage.getImage();
            Image transform = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            lifeImage = new ImageIcon(transform);

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
        gbc.insets = new Insets(20, 20, 0, 0);
        gbc.gridx = 0;
        statusBar.add(score, gbc);

        //Initial time when game is launched
        gameTimer = new JLabel("Timer: 0", SwingConstants.CENTER);
        gameTimer.setForeground(Color.WHITE);
        gameTimer.setFont(customFont);
        gbc.insets = new Insets(20, -70, 0, 0);
        gbc.gridx = 1;
        statusBar.add(gameTimer, gbc);

        //Tracks the level the player is on
        gbc.insets = new Insets(20, 0, 0, 20);
        level = new JLabel("Level: 1 ", SwingConstants.RIGHT);
        level.setForeground(Color.WHITE);
        level.setFont(customFont);
        gbc.gridx = 2;
        statusBar.add(level, gbc);

        //Lives Panel
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 20, 0, 0);
        statusBar.add(livesPanel, gbc);

        //Interactive Panel
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.insets = new Insets(5, 20, 10, 10);
        statusBar.add(interactiveTextPanel, gbc);

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

    /**
     * Method that updates the livesOnlyPanel based on no. of remaining lives.
     * @param playerLives No.of remaining player lives 
     */
    public void updatePlayerLives(int playerLives){
        livesOnlyPanel.removeAll();
        logger.info("PlayerLives deducted: " + playerLives);
        for (int i = 0; i<playerLives; i++){
            livesOnlyPanel.add(new JLabel(lifeImage));
        }
        livesOnlyPanel.revalidate();
        livesOnlyPanel.repaint();
    }

    /**
     * @return Interactive Text Panel with its components.
     */
    public JPanel getInteractiveTextPanel(){
        JPanel rawPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        rawPanel.setPreferredSize(new Dimension(180, 40));
        rawPanel.setBackground(Color.BLACK);

        interactiveText = new JLabel("Sample Interactive Text");
        Font derivedCustomFont = customFont.deriveFont(Font.PLAIN, 13f);
        interactiveText.setFont(derivedCustomFont);
        interactiveText.setForeground(Color.WHITE);

        rawPanel.add(interactiveText);
        return rawPanel;
    }

    /**
     * Method to Update Interactive Text label.
     * @parm String to display on the Jlabel
     */
    public void setInteractiveText(String Text){
        interactiveText.setText(Text);
    }

}