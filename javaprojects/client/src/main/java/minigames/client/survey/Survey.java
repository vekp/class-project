package minigames.client.survey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Survey implements ActionListener {

    // Private variables
    private int count = 0;
    private JLabel label, headingLabel;
    private JButton button, backButton;
    
    // Public variables
    public JPanel panel;

    // Main Survey Class
    public Survey(ActionListener goBack) {

        // add new JLabels here
        headingLabel = new JLabel();
        label = new JLabel("Number of clicks: 0");

        // add new JPanels here
        panel = new JPanel();

        // add new JButtons here
        button = new JButton("Click me... I can count!");
        backButton = new JButton("Back");

        // Action Listeners
        button.addActionListener(this);
        backButton.addActionListener(goBack);

        // Set Survey Heading
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");

        // Set Survey Border
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Set Survey Layout
        panel.setLayout(new GridLayout(0, 1));

        // Panel add section
        panel.add(headingLabel);
        panel.add(button);
        panel.add(label);
        panel.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        label.setText("Number of clicks: " + count);
    }
}

