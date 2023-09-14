package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemoryGame {
    private static boolean listeningForClicks = true;
    private static boolean startGame = false;
    private static JPanel panel; // Declare panel as a class variable
    private static JLabel instruction;

    private static int currentRound = 1;
    private static final int TOTAL_ROUNDS = 3;

    private final static String dir = "./src/main/resources/images/memory_game_pics/";
    private final static String covers = "./src/main/resources/images/hiding_cards/";
    private final static String correct = "./src/main/resources/images/result/Correct.jpg";
    private final static String incorrect = "./src/main/resources/images/result/Inccorrect.jpg";

    public static void main(GameShow main) {
        // Provide the correct path using forward slashes
        File folder = new File(dir);
        File[] files = folder
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (files != null && files.length > 0) {
            // Create a list of image files
            List<File> imageFiles = new ArrayList<>();
            for (File file : files) {
                imageFiles.add(file);
            }

            JFrame frame = new JFrame("Memory Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Set the window size
            instruction = new JLabel("Memorise Images");
            JPanel instructionPanel = new JPanel();
            instructionPanel.setPreferredSize(new Dimension(700, 100));
            instructionPanel.add(instruction);

            JLabel timerLabel = new JLabel("Time Left: 10 seconds"); // Initial time, adjust as needed
            instructionPanel.add(timerLabel);

            JPanel panel = new JPanel(new GridLayout(2, 3));
            panel.setPreferredSize(new Dimension(800, 500));

            // Define the desired image width and height
            int desiredWidth = 150;
            int desiredHeight = 100;

            List<File> imageList = new ArrayList<>();
            // Display multiple images (adjust the number as needed)
            for (int i = 0; i < 6; i++) {
                int randomIndex = (int) (Math.random() * imageFiles.size());
                File randomImage = imageFiles.get(randomIndex);
                imageFiles.remove(randomIndex); // Remove the used image from the list

                // Resize the image to the desired dimensions
                ImageIcon imageIcon = resizeImage(randomImage, desiredWidth, desiredHeight);
                // Create a JLabel for the image
                imageList.add(randomImage);
                JLabel label = new JLabel(imageIcon);
                panel.add(label);
            }

            int randomIndexF = (int) (Math.random() * imageList.size());
            File randomImageF = imageList.get(randomIndexF);
            ImageIcon imageIconF = resizeImage(randomImageF, desiredWidth, desiredHeight);
            JLabel labeF = new JLabel(imageIconF);
            System.out.println("Random number is " + randomIndexF);

            JPanel allEntries = new JPanel();
            allEntries.setLayout(new GridLayout(2, 1));
            allEntries.add(instructionPanel);
            allEntries.add(panel);

            // Add the panel to the frame
            frame.add(allEntries);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            // Make the window visible
            frame.setVisible(true);

            final int[] remainingTimeInSeconds = { 10 }; // Initial time, adjust as needed
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (remainingTimeInSeconds[0] > 0) {
                        remainingTimeInSeconds[0]--;
                        timerLabel.setText("Time Left: " + remainingTimeInSeconds[0] + " seconds");
                    } else {
                        ((Timer) e.getSource()).stop();
                        timerLabel.setText("Time's up!");
                        replaceImages(panel, randomIndexF);
                        updateInstructionText("Where was it");
                        instructionPanel.add(labeF);

                        // Add game-over logic here if needed
                    }

                }
            });

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                    timer.start();
                }
            });

        }

        else {
            System.out.println("No image files found in the folder.");
        }

        System.out.println("test");
    }

    // Method to resize an image to the desired dimensions
    private static ImageIcon resizeImage(File imageFile, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void replaceImages(JPanel panel, int correctNumber) {

        panel.removeAll(); // Remove existing components from the panel
        File folderCover = new File(covers);
        File[] filesCover = folderCover
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        List<File> imageFilesCover = new ArrayList<>();
        List<JLabel> imageLabels = new ArrayList<>();
        int desiredWidth = 150;
        int desiredHeight = 100;
        JLabel correctImage = null;

        if (filesCover != null && filesCover.length > 0) {
            // Create a list of image files
            for (File file : filesCover) {
                imageFilesCover.add(file);
            }

            // Ensure that correctNumber is within a valid range
            if (correctNumber >= 0 && correctNumber < imageFilesCover.size()) {
                for (int i = 0; i < 6; i++) {
                    File imageIcon = imageFilesCover.get(i);
                    int imageCovernumber = i;
                    ImageIcon imageIconCover = resizeImage(imageIcon, desiredWidth, desiredHeight);
                    if (i == correctNumber) {
                        correctImage = new JLabel(imageIconCover);
                    }

                    JLabel label = new JLabel(imageIconCover);
                    panel.add(label);
                    imageLabels.add(label);

                    // Add a mouse listener to the label to handle mouse click events
                    JLabel finalCorrectImage = correctImage;
                    label.addMouseListener(new MouseListener() {
                        private final boolean clicksEnabled = listeningForClicks;

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // Handle the mouse click event for the image here
                            // For example, you can display a message or perform an action
                            if (listeningForClicks) {
                                if (correctNumber == imageCovernumber) {
                                    System.out.println("Correct Image.");
                                    System.out.println("Icon nummer is " + imageCovernumber);
                                    panel.remove(imageLabels.get(correctNumber));
                                    File newImageFile = new File(
                                            correct);
                                    ImageIcon newImageIcon = resizeImage(newImageFile, desiredWidth, desiredHeight);
                                    // Create a new JLabel with the replacement image
                                    JLabel newLabel = new JLabel(newImageIcon);

                                    // Add the new JLabel to the panel at the same position
                                    panel.add(newLabel, correctNumber);
                                    imageLabels.set(correctNumber, newLabel);
                                    panel.revalidate();
                                    panel.repaint();

                                } else {
                                    System.out.println("Not Correct Image." + imageCovernumber);
                                    panel.remove(imageLabels.get(imageCovernumber));
                                    File newImageFile = new File(
                                            incorrect);
                                    ImageIcon newImageIcon = resizeImage(newImageFile, desiredWidth, desiredHeight);
                                    // Create a new JLabel with the replacement image
                                    JLabel newLabel = new JLabel(newImageIcon);

                                    // Add the new JLabel to the panel at the same position
                                    panel.add(newLabel, imageCovernumber);
                                    imageLabels.set(imageCovernumber, newLabel);
                                    panel.revalidate();
                                    panel.repaint();

                                }
                                listeningForClicks = false;

                            } // enf it // Remove the mouse listener after handling the click even

                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                        }
                    });
                }
            } else {
                System.out.println("Invalid correctNumber. It should be within the range of available images.");
            }
        }

        // Repaint the panel to reflect the changes
        // panel.revalidate();
        // panel.repaint();
    }

    private static void updateInstructionText(String newText) {
        instruction.setText(newText);
    }
}
