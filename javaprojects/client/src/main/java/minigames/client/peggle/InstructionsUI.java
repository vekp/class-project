import javax.swing.*;
import java.awt.*;

public class InstructionsUI extends Container {
    private final String instructionsText = "tesbnfisdbnigjkbn";

    public InstructionsUI(JFrame mainWindow) {

        generateInstructionsPanel();

    }

    private JPanel generateInstructionsPanel(){
        JPanel instructionsPanel = new JPanel(new GridLayout(0,1,0,10));
        instructionsPanel.setBackground(Color.BLACK);

        System.out.println("sbighsdfghbiasdf");


        JTextArea instructions = new JTextArea(instructionsText);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        instructionsPanel.add(instructions);

        return instructionsPanel;
    }


}
