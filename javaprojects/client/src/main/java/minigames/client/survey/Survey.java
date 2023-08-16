package minigames.client.survey;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Survey extends JPanel {

    private JTextArea textArea;

    public Survey() {
        setLayout(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(800, 600));
        textArea.setForeground(Color.GREEN);
        textArea.setBackground(Color.BLACK);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        textArea.setText("This is a sample survey text.\nFeel free to add your questions here.");
        add(textArea);

        setPreferredSize(new Dimension(800, 800));
    }

}
