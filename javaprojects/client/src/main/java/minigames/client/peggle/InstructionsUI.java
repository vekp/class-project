package minigames.client.peggle;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InstructionsUI extends JPanel{
    private final String instructionsTextFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/instructions/instructions.txt";
    private static final String backButtonFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/buttons/backwardBTN.png";
    private static final String backgroundFilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/UI/menuBG.png";
    private final String instructionsPic1FilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/instructions/instructionspic1.gif";
    private final String instructionsPic2FilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/instructions/instructionspic2.gif";
    private final String instructionsPic3FilePath = "./javaprojects/client/src/main/java/minigames/client/peggle/assets/instructions/instructionspic3.gif";
    private MinigameNetworkClient mnClient;
    private PeggleUI peggleUI;

    public InstructionsUI(MinigameNetworkClient mnClient, PeggleUI peggleUI) {
        this.mnClient = mnClient;
        this.peggleUI = peggleUI;


        setBorder(new EmptyBorder(10,10,10,10));

        setLayout(new GridBagLayout());
        GridBagConstraints layoutConstraints = new GridBagConstraints();

        //creating return button panel with gridbaglayout options
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 0.1;
        layoutConstraints.fill = GridBagConstraints.BOTH;
        add(generateTopOptionsPanel(backButtonFilePath, mnClient, peggleUI), layoutConstraints);

        //creating text instructions panel with gridbaglayout options
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 1;
        layoutConstraints.weightx = 0.5;
        layoutConstraints.weighty = 0.9;
        add(generateInstructionsPanel(instructionsTextFilePath), layoutConstraints);

        //creating picture panel with gridbaglayout options
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 1;
        layoutConstraints.weightx = 0.5;
        layoutConstraints.weighty = 0.9;
        add(generatePictureInstructions(instructionsPic1FilePath, instructionsPic2FilePath, instructionsPic3FilePath), layoutConstraints);
    }

    private JPanel generatePictureInstructions(String instructionsPicture1FilePath, String instructionsPicture2FilePath, String instructionsPicture3FilePath){

        JPanel picturePanel = new JPanel(new GridLayout(0,1,0,50));

        JLabel picture1 = new JLabel(new ImageIcon(instructionsPicture1FilePath));
        picturePanel.add(picture1);

        JLabel picture2 = new JLabel(new ImageIcon(instructionsPicture2FilePath));
        picturePanel.add(picture2);

        JLabel picture3 = new JLabel(new ImageIcon(instructionsPicture3FilePath));
        picturePanel.add(picture3);

        picturePanel.setBorder(new EmptyBorder(10,10,10,10));

        return picturePanel;
    }

    private JPanel generateInstructionsPanel(String instructionsTextFilePath){

        JPanel instructionsPanel = new JPanel(new BorderLayout());

        JTextArea instructions = new JTextArea(getInstructionsText(instructionsTextFilePath));

        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setOpaque(false);
        instructions.setMinimumSize(new Dimension(500,500));

        instructionsPanel.add(instructions, BorderLayout.CENTER);
        instructionsPanel.setBorder(new EmptyBorder(10,10,10,10));

        return instructionsPanel;
    }

    private String getInstructionsText(String instructionsTextFilePath) {

        //Reads data from instructions.txt file found in assets
        Path path = Path.of(instructionsTextFilePath);
        java.util.List<String> fileContents = null;
        try {fileContents = Files.readAllLines(path);}
        catch (IOException io) {System.exit(0);}

        //Create string value to return to instruction panel
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fileContents.size(); i++) {
            stringBuilder.append(fileContents.get(i));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();

    }

    private JPanel generateTopOptionsPanel(String backButtonFilePath, MinigameNetworkClient mnClient, PeggleUI peggleUI){

        JPanel topOptionsPanel = new JPanel(new BorderLayout());
        topOptionsPanel.add(createImageButton(backButtonFilePath, e -> peggleUI.showMainMenu(mnClient), 0.3), BorderLayout.WEST);

        return topOptionsPanel;

    };

    private JButton createImageButton(String imagePath, ActionListener action, double scalingFactor) {
        ImageIcon icon = new ImageIcon(imagePath);
        int scaledWidth = (int) (icon.getIconWidth() * scalingFactor);
        int scaledHeight = (int) (icon.getIconHeight() * scalingFactor);
        Image scaledImage = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(action);
        return button;
    }


}
