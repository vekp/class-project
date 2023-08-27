package minigames.client.gameshow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MemoryGame {

    public static void main(String[] args) {
        // Provide the correct path using forward slashes
        File folder = new File("C:/Users/Admin/OneDrive - University of New England/Documents/Jenifer/Masters in IT/COSC220/Assignment 3 - Gameshow/classproject/javaprojects/client/src/main/resources/images/memory_game_pics");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (files != null && files.length > 0) {
            // Create a list of image files
            List<File> imageFiles = new ArrayList<>();
            for (File file : files) {
                imageFiles.add(file);
            }

            JFrame frame = new JFrame("Memory Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Set the window size
            JLabel instruction = new JLabel("Memorise Images");
            JPanel instructionPanel = new JPanel();
            instructionPanel.setPreferredSize(new Dimension(700, 100));
            instructionPanel .add(instruction );

            JLabel timerLabel = new JLabel("Time Left: 10 seconds"); // Initial time, adjust as needed
            instructionPanel.add(timerLabel);

            JPanel panel = new JPanel(new GridLayout(2, 3));
            panel.setPreferredSize(new Dimension(800, 500));

            // Define the desired image width and height
            int desiredWidth = 150;
            int desiredHeight = 100;

            // Display multiple images (adjust the number as needed)
            for (int i = 0; i < 6; i++) {
                int randomIndex = (int) (Math.random() * imageFiles.size());
                File randomImage = imageFiles.get(randomIndex);
                imageFiles.remove(randomIndex); // Remove the used image from the list

                // Resize the image to the desired dimensions
                ImageIcon imageIcon = resizeImage(randomImage, desiredWidth, desiredHeight);

                // Create a JLabel for the image
                JLabel label = new JLabel(imageIcon);
                panel.add(label);
            }
            JPanel allEntries = new JPanel();
            allEntries.setLayout(new GridLayout(2,1));
            allEntries.add(instructionPanel);
            allEntries.add(panel);

            // Add the panel to the frame
            frame.add(allEntries);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            // Make the window visible
            frame.setVisible(true);

            final int[] remainingTimeInSeconds = {10}; // Initial time, adjust as needed
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (remainingTimeInSeconds[0] > 0) {
                        remainingTimeInSeconds[0]--;
                        timerLabel.setText("Time Left: " + remainingTimeInSeconds[0] + " seconds");
                    } else {
                        ((Timer) e.getSource()).stop();
                        timerLabel.setText("Time's up!");
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
}
