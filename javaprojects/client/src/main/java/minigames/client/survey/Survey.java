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

    // Private variables (labels and buttons need to be registered here)
    private int count = 0;
    private JLabel counterLabel, headingLabel;
    private JButton counterButton, backButton;
    
    // Public variables
    public JPanel panel;

    // Main Survey Class
    public Survey(ActionListener goBack) {

        // add new JLabels here
        headingLabel = new JLabel();
        counterLabel = new JLabel("Number of clicks: 0");

        // add new JPanels here
        panel = new JPanel();

        // add new JButtons here
        counterButton = new JButton("Click me... I can count!");
        backButton = new JButton("Back");

        // Action Listeners
        counterButton.addActionListener(this);
        backButton.addActionListener(goBack);

        // Set Survey Heading
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");

        // Set Survey Border
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Set Survey Layout
        panel.setLayout(new GridLayout(0, 1));

        // Panel add section
        panel.add(headingLabel);
        panel.add(counterButton);
        panel.add(counterLabel);
        panel.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        counterLabel.setText("Number of clicks: " + count);
    }
}

