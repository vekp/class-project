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
    private JLabel counterLabel, headingLabel, testLabel;
    private JButton counterButton, backButton;
    
    // Public variables
    public JPanel mainPanel;

    // Main Survey Class
    public Survey(ActionListener goBack) {

        // add new JLabels here
        headingLabel = new JLabel();
        counterLabel = new JLabel("Number of clicks: 0");
        // testLabel = new JLabel("Number of clicks: 0");

        // add new JPanels here
        mainPanel = new JPanel();

        // add new JButtons here
        counterButton = new JButton("Click me... I can count!");
        backButton = new JButton("Back");

        // Action Listeners
        counterButton.addActionListener(this);
        backButton.addActionListener(goBack);

        // Set Survey Heading
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");

        // Set Survey Border
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Set Survey Layout
        mainPanel.setLayout(new GridLayout(0, 1));

        // mainPanel add section
        mainPanel.add(headingLabel);
        mainPanel.add(counterButton);
        mainPanel.add(counterLabel);
        mainPanel.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        counterLabel.setText("Number of clicks: " + count);
    }
}

