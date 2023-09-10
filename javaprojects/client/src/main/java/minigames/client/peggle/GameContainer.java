package minigames.client.peggle;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameContainer extends JPanel {
    private InGameUI gameUI;
    private MinigameNetworkClient mnClient;
    private PeggleUI peggleUI;

    private static ImageIcon ballImage;

    private int remainingBalls = 5;

    static {
        ballImage = new ImageIcon("./javaprojects/client/src/main/java/minigames/client/peggle/assets/objects/ball.png");
    }


    GameContainer(MinigameNetworkClient mnClient, PeggleUI peggleUI){
        this.mnClient = mnClient;
        this.peggleUI = peggleUI;

        setLayout(new BorderLayout());

        InGameUI inGameUI = new InGameUI();

        add(inGameUI, BorderLayout.CENTER);
        add(generateMenuButtons(peggleUI), BorderLayout.NORTH);
        add(generateBallChamber(), BorderLayout.WEST);

    }


    private JPanel generateMenuButtons (PeggleUI peggleUI){
        JPanel menuButtons = new JPanel();
        menuButtons.setLayout(new BorderLayout());
        JButton returnButton = new JButton("Return to Main Menu");
        ActionListener returnActionListener = e -> peggleUI.showMainMenu(mnClient);
        returnButton.addActionListener(returnActionListener);
        menuButtons.add(returnButton, BorderLayout.WEST);

        menuButtons.setPreferredSize(new Dimension(1080,100));
        menuButtons.setBackground(Color.BLACK);

        return menuButtons;
    }



    private JPanel generateBallChamber (){
        JPanel ballChamber = new JPanel();
        ballChamber.setLayout(new GridLayout(remainingBalls, 1,5,5));

        for (int i = 0; i < remainingBalls; i++) {
            JLabel remainingBallsLabel = new JLabel((Icon) ballImage);
            ballChamber.add(remainingBallsLabel);
        }

        ballChamber.setPreferredSize(new Dimension(100,1820));
        ballChamber.setBackground(Color.BLACK);

        return ballChamber;
    }




}
