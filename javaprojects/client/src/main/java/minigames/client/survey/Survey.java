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
    private int count = 0;
    private JLabel label, headingLabel;
    public JPanel panel;
    private JButton button, backButton;

    public Survey(ActionListener goBack) {
        headingLabel = new JLabel();
        button = new JButton("Click me... I can count!");
        backButton = new JButton("Back");
        label = new JLabel("Number of clicks: 0");
        panel = new JPanel();

        // Actions
        button.addActionListener(this);
        backButton.addActionListener(goBack);

        // Set
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setLayout(new GridLayout(0, 1));

        // Add
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

