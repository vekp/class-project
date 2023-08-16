package minigames.client.spacemaze;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;

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

public class MazeDisplay{

    JPanel mazePanel;
    JPanel elementPanel;

    JLabel countdownTimer;
    JLabel score;

    public JPanel mazePanel(){
        mazePanel = new JPanel();
        mazePanel.setPreferredSize(new Dimension(800, 600));
        mazePanel.setBackground(Color.BLACK);

        //Deserialize Maze here.

        return mazePanel;
    }

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
}