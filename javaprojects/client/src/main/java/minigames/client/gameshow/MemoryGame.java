package minigames.client.gameshow;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

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

/*
This class creates a game called Memory Game
*Step 1 present 6 pictures on the screen and start the timer
*Step 2 hide those pictures when the timer is up
*Step 3 Show one image at the top and ask the player were was that image

 */
public class MemoryGame {
    private static boolean listeningForClicks = true; // When we click it changes to false. the stops event listeners so
                                                      // that the player does not keep clicking and answers are revealed
    private static boolean startGame = false;
    private static JPanel panel; // Declare panel as a class variable
    private static JLabel instruction; // A panel that has instruction on what a player should do next as they are
                                       // playing

    private static int currentRound = 1;
    private static final int TOTAL_ROUNDS = 3;

    private static int imagesToDisplay = 6;

    private final static String dir = "./src/main/java/minigames/client/gameshow/GameShowImages/MemoryImages/"; // Folder
                                                                                                                // that
                                                                                                                // contains
                                                                                                                // the
    // pictures used in the memory
    // game
    private final static String covers = "./src/main/resources/images/hiding_cards/"; // Folder that contains pictures
                                                                                      // that will be used to hide the
                                                                                      // cards
    private final static String correct = "./src/main/resources/images/result/Correct.jpg"; // An image that that shows
                                                                                            // a tick used when the
                                                                                            // player guesses correctly
    private final static String incorrect = "./src/main/resources/images/result/Inccorrect.jpg"; // An image that that
                                                                                                 // shows an X used when
                                                                                                 // the player guesses
                                                                                                 // incorrectly

    public static void main(GameShow main) {
        GameShowUI.gameContainer.removeAll(); // remove all the components that are currently added to the gameContainer
        File folder = new File(dir); // dir is the file path
        // only add to the array those with names ending in ".jpg" or ".png".
        File[] files = folder
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        // Create an ArrayList called imageFiles from an array above called files. It is
        // easier to manipulate an ArrayList than Array
        if (files != null && files.length > 0) {
            List<File> imageFiles = new ArrayList<>();
            for (File file : files) {
                imageFiles.add(file);
            }
            JPanel textHolder = new JPanel();
            // textHolder.setLayout(new BoxLayout(textHolder, BoxLayout.Y_AXIS));
            textHolder.setPreferredSize(new Dimension(200, 80));

            instruction = new JLabel("Memorise Images"); // Tell players what to do
            Font pixelFont = GameShowUI.pixelFont;
            instruction.setFont(pixelFont.deriveFont(15f));
            // instruction.setHorizontalAlignment(SwingConstants.EAST);
            textHolder.add(instruction);

            JLabel timerLabel = new JLabel("Time Left: 10 seconds"); // Initial time that will be displays
            timerLabel.setFont(pixelFont.deriveFont(15f));
            // timerLabel.setHorizontalAlignment(SwingConstants.EAST);
            textHolder.add(timerLabel);

            JPanel instructionPanel = new JPanel(); // create a panel where we store instructions for players. It will
                                                    // on the top of the page
            instructionPanel.setLayout(new BorderLayout());
            instructionPanel.setPreferredSize(new Dimension(200, 290));
            instructionPanel.add(textHolder, BorderLayout.PAGE_START);

            JPanel imagePanel = new JPanel(new GridLayout(2, 3));// create a panel that will contain images where the
                                                                 // player will make guesses
            imagePanel.setPreferredSize(new Dimension(600, 340));

            // Define the desired image width and height
            int desiredWidth = 130;
            int desiredHeight = 130;

            // randomImageList will contain images chosen randomly from imageFiles.
            List<File> randomImageList = new ArrayList<>();
            for (int i = 0; i < imagesToDisplay; i++) {
                int randomIndex = (int) (Math.random() * imageFiles.size()); // random number generation based on the
                                                                             // size of the file
                File randomImage = imageFiles.get(randomIndex); // random number use as index to fetch the image from
                                                                // imageFiles
                imageFiles.remove(randomIndex); // Remove the used image from the imageFiles so that it is not chosen
                                                // more than twice
                ImageIcon imageIcon = resizeImage(randomImage, desiredWidth, desiredHeight); // Resize the image to the
                                                                                             // desired dimensions
                randomImageList.add(randomImage);
                JLabel label = new JLabel(imageIcon); // Create a JLabel for the image
                imagePanel.add(label);
            }
            // generate a random number based randomImageList size so that we can randomly
            // choose the picture that we want to unhide
            int randomIndexF = (int) (Math.random() * randomImageList.size());
            File randomImageF = randomImageList.get(randomIndexF); // that random number will be used to fetch the image
            ImageIcon imageIconF = resizeImage(randomImageF, desiredWidth, desiredHeight);
            JLabel targetImage = new JLabel(imageIconF); // image that will be shown to the player so that he can guesse
                                                         // where it was
            targetImage.setBorder(new EmptyBorder(0, 0, 80, 0));

            JPanel allEntries = new JPanel(); // create a panel that will have all the panels we have create
            allEntries.setLayout(new BoxLayout(allEntries, BoxLayout.X_AXIS));
            allEntries.add(instructionPanel);
            allEntries.add(imagePanel);

            // Add the panel to the frame
            GameShowUI.gameContainer.add(allEntries, BorderLayout.CENTER); // add to the gameContainer, which is a panel
                                                                           // that will be show to
            // the player

            final int[] remainingTimeInSeconds = { 10 }; // Initial time, adjust as needed

            // Create events around the timer. Therefore, after 10 seconds replace all the
            // images in ImagePanel with images that covers
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (remainingTimeInSeconds[0] > 0) {
                        remainingTimeInSeconds[0]--;
                        timerLabel.setText("Time Left: " + remainingTimeInSeconds[0] + " seconds");
                    } else {
                        ((Timer) e.getSource()).stop();
                        timerLabel.setText("Where was it");
                        replaceImages(imagePanel, randomIndexF);// when times is up, cover the images by replacing them
                                                                // with new images using a method called replaceImages
                        updateInstructionText("Time's up!"); // update instructions using a method called
                                                             // updateInstructionText
                        textHolder.validate();
                        textHolder.repaint();
                        instructionPanel.add(targetImage, BorderLayout.CENTER);

                    }

                }
            });
            timer.start(); // start the tim
        }

        else {
            System.out.println("No image files found in the folder.");
        }
        JPanel memoryHeader = GameShowUI.generateMemoryHeader();
        GameShowUI.gameContainer.add(memoryHeader, BorderLayout.NORTH);

        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }

    /**
     *
     * @param imageFile
     * @param width     d
     * @param height
     * @return an ImageIcon with the right size
     */
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

    /**
     * a method that replaces images when the time is up and over cover those images
     * so a player can guesse where the image was
     * the method is used to detect whether the player has guessed correctly
     * 
     * @param panel         that contains the images
     * @param correctNumber that is the index number of the correct image chosen as
     *                      the challenge where the player has guess where the image
     *                      is among the picture
     */

    public static void replaceImages(JPanel panel, int correctNumber) {

        panel.removeAll(); // Remove existing images/components from the panel
        File folderCover = new File(covers); // covers is the file path to were image covers are stored
        File[] filesCover = folderCover
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        List<JLabel> imageLabels = new ArrayList<>();
        int desiredWidth = 150;
        int desiredHeight = 100;
        JLabel correctImage = null;

        // Create an ArrayList called imageLabels from an array above called
        // folderCover. It is easier to manipulate an ArrayList than Array
        List<File> imageFilesCover = new ArrayList<>();
        if (filesCover != null && filesCover.length > 0) {
            for (File file : filesCover) {
                imageFilesCover.add(file);
            }

            // Ensure that correctNumber is within a valid range
            if (correctNumber >= 0 && correctNumber < imageFilesCover.size()) {
                for (int i = 0; i < 6; i++) {
                    File imageIcon = imageFilesCover.get(i);
                    int imageCovernumber = i;
                    ImageIcon imageIconCover = resizeImage(imageIcon, desiredWidth, desiredHeight);
                    JLabel label = new JLabel(imageIconCover);
                    panel.add(label);
                    imageLabels.add(label);

                    // add an action listener to each image. Whatever image is clicked it will
                    // either be replaced
                    // by "correct" image or "incorrect" image. therefore you need to update the
                    // panel
                    label.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (listeningForClicks) {
                                if (correctNumber == imageCovernumber) { // If the image cover index is the same as the
                                                                         // correctNumber then the player has clicked
                                                                         // the right image
                                    System.out.println("Correct Image.");
                                    System.out.println("Icon nummer is " + imageCovernumber);
                                    panel.remove(imageLabels.get(correctNumber));
                                    File newImageFile = new File(
                                            correct); // show the image that says correct
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

    /**
     * Method to update the instructions
     * 
     * @param newText a string with desired instructions
     */
    private static void updateInstructionText(String newText) {
        instruction.setText(newText);
    }

    private static void clearGameContainer() {
        GameShowUI.gameContainer.removeAll();
        GameShowUI.gameContainer.validate();
        GameShowUI.gameContainer.repaint();
    }
}
