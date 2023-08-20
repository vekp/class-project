package minigames.client.peggle;


import javax.swing.*;
import java.awt.*;

public class InstructionsUI extends JPanel{
    private final String instructionsText = "tesbnfisdbnigjkbn";

    public InstructionsUI() {
        JPanel instructionsPanel = generateInstructionsPanel();
        this.add(instructionsPanel);


    }

    private JPanel generateInstructionsPanel(){
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBackground(Color.BLACK);

        JTextArea instructions = new JTextArea(instructionsText);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        instructionsPanel.add(instructions, BorderLayout.CENTER);

        return instructionsPanel;
    }


}
