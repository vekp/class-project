package minigames.client.survey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;

// import javax.swing.Dimension;

public class Survey extends JPanel implements ActionListener {

    // Private variables 
    // (labels and buttons need to be registered here)
    private JPanel titlePanel, counterPanel, backPanel, gameNamePanel, surveyQuestionsPanel, commentsPanel; 
    private JLabel counterLabel, headingLabel, testLabel, gameNameLabel, commentsLabel;
    private JButton counterButton, backButton;
    private JTextField gameNameText, commentsText;
    private int count = 0;

    // Public variables

    // Sets the Frame Title (top left corner)
    public static final String FRAME_TITLE = "Game Survey";

    // Main Survey Class
    public Survey(ActionListener goBack) {

        // Survey main panel layout

        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLUE);
        this.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title Panel
        titlePanel = new JPanel();
        headingLabel = new JLabel();
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");
        titlePanel.add(headingLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // gameName Panel
        gameNamePanel = new JPanel();
        gameNameLabel = new JLabel();
        gameNameText = new JTextField(20);
        
        gameNameLabel.setText("Name: ");
        gameNameLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
        gameNameText.setFont(new Font("Calibri", Font.PLAIN, 16));
        gameNamePanel.add(gameNameLabel, BorderLayout.WEST);
        gameNamePanel.add(gameNameText, BorderLayout.EAST);
        // this.add(gameNamePanel, BorderLayout.CENTER);

        // comments Panel
        commentsPanel = new JPanel();
        commentsLabel = new JLabel();
        commentsText = new JTextField(20);
        
        commentsLabel.setText("Comments: ");
        commentsLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
        commentsText.setFont(new Font("Calibri", Font.PLAIN, 16));
        commentsPanel.add(commentsLabel, BorderLayout.WEST);
        commentsPanel.add(commentsText, BorderLayout.EAST);

        // surveyQuestionsPanel (incorporates all panels for the survey)
        surveyQuestionsPanel = new JPanel();
        surveyQuestionsPanel.setLayout(new GridLayout(4, 0));
        surveyQuestionsPanel.add(gameNamePanel);
        surveyQuestionsPanel.add(commentsPanel);
        this.add(surveyQuestionsPanel);

        // Back Button
        backPanel = new JPanel();
        backButton = new JButton("Back");
        backButton.addActionListener(goBack);
        backPanel.add(backButton);
        this.add(backPanel, BorderLayout.SOUTH);


        // ADD REQUEST TO ENDPOINTS HERE!!!



    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        counterLabel.setText("Number of clicks: " + count);
    }
}

