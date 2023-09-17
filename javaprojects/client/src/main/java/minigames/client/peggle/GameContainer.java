package minigames.client.peggle;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameContainer extends JPanel {
    private MinigameNetworkClient mnClient;
    private PeggleUI peggleUI;
    private static ImageIcon ballImage;
    private int remainingBalls = 5;
    private JLabel scoreLabel;
    private int score = 0;



    static {
        ballImage = new ImageIcon("./javaprojects/client/src/main/java/minigames/client/peggle/assets/objects/ball.png");
    }


    GameContainer(MinigameNetworkClient mnClient, PeggleUI peggleUI){
        this.mnClient = mnClient;
        this.peggleUI = peggleUI;

        setLayout(new BorderLayout());

        InGameUI inGameUI = new InGameUI(this);

        add(inGameUI, BorderLayout.CENTER);
        add(generateMenuButtons(peggleUI), BorderLayout.NORTH);
        add(generateBallChamber(remainingBalls), BorderLayout.WEST);

    }


    private JPanel generateMenuButtons (PeggleUI peggleUI){
        JPanel menuButtons = new JPanel();
//        menuButtons.setLayout(new BorderLayout()); // Use flow layout for now, update if needed

        //Main menu button
        JButton returnButton = new JButton("Return to Main Menu");
        ActionListener returnActionListener = e -> peggleUI.showMainMenu(mnClient);
        returnButton.addActionListener(returnActionListener);
        menuButtons.add(returnButton);


        //Score label
        scoreLabel = new JLabel("Score: " + score);
        menuButtons.add(scoreLabel);

        menuButtons.setPreferredSize(new Dimension(1080,100));

        menuButtons.setBackground(Color.BLACK);

        return menuButtons;
    }



    private JPanel generateBallChamber (int remainingBalls){
        JPanel ballChamber = new JPanel();
        ballChamber.setLayout(new GridLayout(remainingBalls, 1,5,5));
        ballChamber.setBorder(new EmptyBorder(300,30,20,10));

        for (int i = 0; i < remainingBalls; i++) {
            JLabel remainingBallsLabel = new JLabel((Icon) ballImage);
            ballChamber.add(remainingBallsLabel);
        }

        ballChamber.setPreferredSize(new Dimension(100,1820));
        ballChamber.setBackground(Color.BLACK);

        return ballChamber;
    }


   public void setRemainingBalls(int remainingBalls) {
        generateBallChamber(remainingBalls);
   }


   public void incrementScore(int points) {
        score += points;
        scoreLabel.setText("Score: " + score);

   }

}
