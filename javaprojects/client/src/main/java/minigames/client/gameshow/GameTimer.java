package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents a game timer component for tracking time and score in a game.
 */
public class GameTimer extends JPanel {
    public long startTime;            // The time when the timer was started
    public long timeLimit;            // The maximum time limit for the game
    public boolean isRunning;         // Flag to indicate whether the timer is running
    public Timer timer;               // Swing Timer to trigger periodic updates
    public long remainingTime;        // The remaining time on the timer

    /**
     * Constructs a new GameTimer with the specified time limit.
     *
     * @param timeLimit The time limit in milliseconds for the game.
     */
    public GameTimer(long timeLimit) {
        this.timeLimit = timeLimit;
        isRunning = false;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint(); // Trigger repaint every second
            }
        });
    }

    /**
     * Starts the game timer.
     */
    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            timer.start();
        }
    }

    /**
     * Stops the game timer.
     */
    public void stop() {
        if (isRunning) {
            isRunning = false;
            timer.stop();
        }
    }

    /**
     * Paints the game timer component.
     *
     * @param g The Graphics context used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        remainingTime = getRemainingTime();
        int score = calculateScore();

        // Draw the remaining time and score on the JPanel
        g.setColor(Color.BLACK);
        Font pixelFont = GameShowUI.pixelFont;
        g.setFont(pixelFont.deriveFont(15f));
        g.drawString("Remaining Time: " + formatTime(remainingTime), 0, 10);
        g.drawString("Score: " + score, 250, 10);
    }

    /**
     * Formats time in milliseconds into a "MM:SS" string.
     *
     * @param milliseconds The time in milliseconds to be formatted.
     * @return The formatted time string.
     */
    public String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Gets the current time on the game timer.
     *
     * @return The current time in milliseconds.
     */
    public long getCurrentTime() {
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        } else {
            return timeLimit - getRemainingTime();
        }
    }

    /**
     * Gets the remaining time on the game timer.
     *
     * @return The remaining time in milliseconds.
     */
    public long getRemainingTime() {
        if (isRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            return Math.max(timeLimit - elapsedTime, 0);
        } else {
            return remainingTime;
        }
    }

    /**
     * Gets the time limit for the game timer.
     *
     * @return The time limit in milliseconds.
     */
    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * Calculates the score based on the remaining time.
     *
     * @return The calculated score.
     */
    public int calculateScore() {
        remainingTime = getRemainingTime();
        int score = (int) (remainingTime / 1000); // Convert milliseconds to seconds
        return score;
    }
}
