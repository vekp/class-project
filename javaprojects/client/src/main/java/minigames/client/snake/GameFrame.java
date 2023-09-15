package minigames.client.snake;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import minigames.client.snake.*;

public class GameFrame extends JFrame {

    private final JButton startButton;
    private boolean gameStarted = false;
    private final GameDisplay gameDisplay;

    GameFrame() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        ScorePanel scorePanel = new ScorePanel();
        gameDisplay = new GameDisplay(scorePanel);

        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted) {
                    gameStarted = true;
                    startButton.setEnabled(false);
                    gameDisplay.startGame();
                }
            }
        });

        Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(174, 52, 255));
        gameDisplay.setBorder(border);

        setLayout(new BorderLayout());
        add(scorePanel, BorderLayout.NORTH);
        add(gameDisplay, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
