//package minigames.client.peggle;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.Image;
//import java.awt.event.ActionListener;
//
///*
// * PeggleUI class that provides a user interface for the Peggle mini-game
// */
//public class Peggle {
//    private static final String gameName = "Peggle MiniGame";
//    // File paths for images
//    private static final String iconFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/gameIcon.png";
//    private static final String backgroundFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/menuBG.png";
//    private static final String startButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/startBTN.png";
//    private static final String exitButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/exitBTN.png";
//    private static final String instructionsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/instructionsBTN.png";
//    private static final String achievementsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/achievementsBTN.png";
//    private static final String leaderboardButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/leaderboardBTN.png";
//    private static final String settingsButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/settingsBTN.png";
//
//    private static JFrame mainWindow = null;
//
//    // Main function initialises the UI and starts the game
//    public Peggle() {
//        launchGame();
//    }
//
//    private void launchGame() {
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                mainWindow = new JFrame(gameName);
//                mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mainWindow.setPreferredSize(new Dimension(1000, 750));
//                mainWindow.setIconImage(new ImageIcon(iconFilePath).getImage());
//
//                JPanel titleScreen = generateMainMenu();
//                mainWindow.setContentPane(titleScreen);
//                mainWindow.pack();
//                mainWindow.setVisible(true);
//            }
//        });
//    }
//
//
//
//
//    // Generates the title screen main menu of the game
//
//
//    // Generates the main menu buttons (start, exit)
//
//
//    // Generate the top center buttons (achievements, leaderboard, instructions, settings)
//
//
//    // Creates a button with an image, associated action and scaling
//
//
//
//
//
//
//
//    // Starts the game by switching to the InGameUI
//
//
//    // Checks achievements and displays them to the player
//
//
//    // Pause the game and display the pause menu. TODO: To be implemented
//    private void pauseMenu(){
//        System.out.println("Pause Menu");
//    }
//}