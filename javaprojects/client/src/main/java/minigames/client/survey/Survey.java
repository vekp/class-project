package minigames.client.survey;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private JPanel titlePanel, counterPanel, backPanel, gameNamePanel, surveyQuestionsPanelGroup, surveyQuestionsPanelLeft, surveyQuestionsPanelRight, feedbackPanel, submitPanel, footerPanel, uiRatingPanel, enjoymentPanel, functionalityPanel; 
    private JLabel counterLabel, headingLabel, testLabel, gameNameLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel;
    private JButton counterButton, backButton, submitButton;
    private JTextField gameNameText;
    private JTextArea feedbackText;
    private JRadioButton uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive, enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive, functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive;
    private ButtonGroup uiRatingButtonGroup, enjoymentButtonGroup, functionalityButtonGroup;

    // Public variables
    // Sets the Frame Title (top left corner)
    public static final String FRAME_TITLE = "Game Survey";

    // Main Survey Class
    public Survey(ActionListener goBack) {

        // Survey main panel layout
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.CYAN);
        this.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title Panel
        titlePanel = new JPanel();
        headingLabel = new JLabel();
        headingLabel.setText("<html><h1 style='color: blue;'}>Game Survey</h1></html>");
        titlePanel.add(headingLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // gameName Label
        gameNameLabel = new JLabel();
        gameNameLabel.setText("Name: ");
        gameNameLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        // gameName Panel
        gameNamePanel = new JPanel();
        gameNameText = new JTextField(20);
        gameNameText.setFont(new Font("Calibri", Font.PLAIN, 16));
        gameNameText.setBounds(0,0,50,50);
        gameNamePanel.add(gameNameText);
        gameNamePanel.setBackground(Color.LIGHT_GRAY);


        // User Interface Rating Label
        uiRatingLabel = new JLabel();
        uiRatingLabel.setText("User Interface Rating: ");
        uiRatingLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        // User Interface Rating Panel
        uiRatingPanel = new JPanel();
        uiRatingPanel.setLayout(new GridLayout(0, 5));
        uiRatingOne = new JRadioButton("1");
        uiRatingTwo = new JRadioButton("2");
        uiRatingThree = new JRadioButton("3");
        uiRatingFour = new JRadioButton("4");
        uiRatingFive = new JRadioButton("5");
        uiRatingThree.setSelected(true);
        uiRatingPanel.add(uiRatingOne);
        uiRatingPanel.add(uiRatingTwo);
        uiRatingPanel.add(uiRatingThree);
        uiRatingPanel.add(uiRatingFour);
        uiRatingPanel.add(uiRatingFive);

        // Ensures only one of the radio buttons are selected at a time
        uiRatingButtonGroup= new ButtonGroup();
        uiRatingButtonGroup.add(uiRatingOne);
        uiRatingButtonGroup.add(uiRatingTwo);
        uiRatingButtonGroup.add(uiRatingThree);
        uiRatingButtonGroup.add(uiRatingFour);
        uiRatingButtonGroup.add(uiRatingFive);

        // Enjoyment Rating Label
        enjoymentLabel = new JLabel();
        enjoymentLabel.setText("Enjoyment Rating: ");
        enjoymentLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        // Enjoyment Rating Panel
        enjoymentPanel = new JPanel();
        enjoymentPanel.setLayout(new GridLayout(0, 5));
        enjoymentOne = new JRadioButton("1");
        enjoymentTwo = new JRadioButton("2");
        enjoymentThree = new JRadioButton("3");
        enjoymentFour = new JRadioButton("4");
        enjoymentFive = new JRadioButton("5");
        enjoymentThree.setSelected(true);
        enjoymentPanel.add(enjoymentOne);
        enjoymentPanel.add(enjoymentTwo);
        enjoymentPanel.add(enjoymentThree);
        enjoymentPanel.add(enjoymentFour);
        enjoymentPanel.add(enjoymentFive);

        enjoymentButtonGroup= new ButtonGroup();
        enjoymentButtonGroup.add(enjoymentOne);
        enjoymentButtonGroup.add(enjoymentTwo);
        enjoymentButtonGroup.add(enjoymentThree);
        enjoymentButtonGroup.add(enjoymentFour);
        enjoymentButtonGroup.add(enjoymentFive);

        // Functionality Rating Label
        functionalityLabel = new JLabel();
        functionalityLabel.setText("Functionality Rating: ");
        functionalityLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        // Functionality Rating Panel
        functionalityPanel = new JPanel();
        functionalityPanel.setLayout(new GridLayout(0, 5));
        functionalityOne = new JRadioButton("1");
        functionalityTwo = new JRadioButton("2");
        functionalityThree = new JRadioButton("3");
        functionalityFour = new JRadioButton("4");
        functionalityFive = new JRadioButton("5");
        functionalityThree.setSelected(true);
        functionalityPanel.add(functionalityOne);
        functionalityPanel.add(functionalityTwo);
        functionalityPanel.add(functionalityThree);
        functionalityPanel.add(functionalityFour);
        functionalityPanel.add(functionalityFive);

        functionalityButtonGroup= new ButtonGroup();
        functionalityButtonGroup.add(functionalityOne);
        functionalityButtonGroup.add(functionalityTwo);
        functionalityButtonGroup.add(functionalityThree);
        functionalityButtonGroup.add(functionalityFour);
        functionalityButtonGroup.add(functionalityFive);

        // feedback Label
        feedbackLabel = new JLabel();
        feedbackLabel.setText("Feedback: ");
        feedbackLabel.setFont(new Font("Calibri", Font.PLAIN, 18));

        // feedback Panel
        feedbackPanel = new JPanel();
        feedbackText = new JTextArea();
        feedbackText.setColumns(20);
        feedbackText.setLineWrap(true);
        feedbackText.setRows(5);
        feedbackText.setWrapStyleWord(true);
        feedbackText.setFont(new Font("Calibri", Font.PLAIN, 16));
        feedbackPanel.add(feedbackText);

        // surveyQuestionsPanelLeft (incorporates all Question titles for the survey)
        surveyQuestionsPanelLeft = new JPanel();
        surveyQuestionsPanelLeft.setLayout(new GridLayout(5, 0));
        surveyQuestionsPanelLeft.add(gameNameLabel);
        surveyQuestionsPanelLeft.add(uiRatingLabel);
        surveyQuestionsPanelLeft.add(enjoymentLabel);
        surveyQuestionsPanelLeft.add(functionalityLabel);
        surveyQuestionsPanelLeft.add(feedbackLabel);

        // surveyQuestionsPanelRight (incorporates all Question responses for the survey)
        surveyQuestionsPanelRight = new JPanel();
        surveyQuestionsPanelRight.setLayout(new GridLayout(5, 0));
        surveyQuestionsPanelRight.add(gameNamePanel);
        surveyQuestionsPanelRight.add(uiRatingPanel);
        surveyQuestionsPanelRight.add(enjoymentPanel);
        surveyQuestionsPanelRight.add(functionalityPanel);
        surveyQuestionsPanelRight.add(feedbackPanel);

        // surveyQuestionsPanelGroup (incorporates all panels from the left and right groups for the survey)
        surveyQuestionsPanelGroup = new JPanel();
        surveyQuestionsPanelGroup.setLayout(new GridLayout(0, 2));
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelLeft);
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelRight);
        // surveyQuestionsPanelGroup.add(uiRatingPanel);
        // surveyQuestionsPanelGroup.add(feedbackPanel);
        this.add(surveyQuestionsPanelGroup, BorderLayout.CENTER);


        // Back Button
        backPanel = new JPanel();
        backButton = new JButton("Back");
        backButton.addActionListener(goBack);
        backPanel.add(backButton);
        
        // Submit Button
        submitPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());
        submitPanel.add(submitButton);

        // Footer Panel
        footerPanel = new JPanel();
        footerPanel.add(backPanel, BorderLayout.WEST);
        footerPanel.add(submitPanel, BorderLayout.EAST);
        this.add(footerPanel, BorderLayout.SOUTH);

        // ADD REQUEST TO ENDPOINTS HERE!!!



    }

    public void submit() {
        String text = feedbackText.getText();
    
        // JSON object to store the feedback data
        JSONObject feedbackObject = new JSONObject();
        feedbackObject.put("user_id", "123"); // Replace with real user ID
        feedbackObject.put("timestamp", getCurrentTimestamp());
        feedbackObject.put("feedback_text", text);
    
        // Save JSON object to a local file
        saveFeedbackToJsonFile(feedbackObject);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    private void saveFeedbackToJsonFile(JSONObject feedbackObject) {
        try (FileWriter fileWriter = new FileWriter("feedback.json", true)) {
            // Append feedback to existing JSON file, otherwise create new one
            fileWriter.write(feedbackObject.toJSONString());
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Add proper exception handling
        }
    }
}
