package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
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
    Timer timer;
    SpaceMaze spaceMaze;

    /**
     * Constructor for the Status Bar
     * @param spaceMaze the client side controller to receive messages from here
     */
    public StatusBar(SpaceMaze spaceMaze) {
        this.spaceMaze = spaceMaze;
        statusBar();
        timer = new Timer();
    }

    /**
     * Panel below the maze for holding maze information
     *
     * @return Jpanel with components
     */
    public JPanel statusBar() {
        statusBar = new JPanel();
        statusBar.setPreferredSize(new Dimension(800, 200));
        statusBar.setBackground(Color.BLACK);

        //Initial time when game is launched
        gameTimer = new JLabel("Time: 0");
        gameTimer.setForeground(Color.GREEN);
        gameTimer.setFont(new Font("Monospaced", Font.PLAIN, 18));

        //Dummy score display (Possibly only update this between levels?)
        score = new JLabel("Score: 0");
        score.setForeground(Color.GREEN);
        score.setFont(new Font("Monospaced", Font.PLAIN, 18));

        statusBar.add(gameTimer);
        statusBar.add(score);

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
            gameTimer.setText("Time: " + currentTime + "  ");
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
        }
    }
}