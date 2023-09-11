package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameTimer extends JPanel {
    private long startTime;
    private long timeLimit;
    private boolean isRunning;
    private Timer timer;

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

    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            timer.start();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        long remainingTime = getRemainingTime();
        int score = calculateScore();

        // Draw the remaining time and score on the JPanel
        g.setColor(Color.BLACK);
        Font pixelFont = GameShowUI.pixelFont;
        g.setFont(pixelFont.deriveFont(15f));
        g.drawString("Remaining Time: " + formatTime(remainingTime), 0, 10);
        g.drawString("Score: " + score, 250, 10);
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public long getCurrentTime() {
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        } else {
            return timeLimit - getRemainingTime();
        }
    }

    public long getRemainingTime() {
        if (isRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            return Math.max(timeLimit - elapsedTime, 0);
        } else {
            return 0;
        }
    }

    public int calculateScore() {
        long remainingTime = getRemainingTime();
        int score = (int) (remainingTime / 1000); // Convert milliseconds to seconds
        return score;
    }
}
