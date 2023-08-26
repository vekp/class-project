package minigames.client.survey;

import minigames.client.MinigameNetworkClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.awt.BorderLayout;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import java.io.*;
import javax.swing.*;
import java.awt.*;

// import javax.swing.Dimension;

public class Survey extends JPanel implements ActionListener {

    MinigameNetworkClient mnClient;
    JSONArray feedbackArray;

    // Private variables

    // (labels and buttons need to be registered here)
    private JPanel titlePanel, counterPanel, backPanel, gameNamePanel, surveyQuestionsPanelGroup, surveyQuestionsPanelLeft, surveyQuestionsPanelRight, feedbackPanel, submitPanel, footerPanel, uiRatingPanel, enjoymentPanel, functionalityPanel; 
    private JLabel counterLabel, headingLabel, testLabel, gameNameLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, gameNameTextLabel;
    private JButton counterButton, backButton, submitButton;
    private JTextArea feedbackText;
    private JRadioButton uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive, enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive, functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive;
    private ButtonGroup uiRatingButtonGroup, enjoymentButtonGroup, functionalityButtonGroup;
    private Border borderPosition, raisedBevel, loweredBevel, outerColourBorder, styledOuterBorder, innerColourBorder, styledInnerBorder, styledBorders, finalBorder;
    // Colours
    // Colours of panels, buttons and radio buttons
    private Color bgColour = new Color(255,255,255); // background
    private Color fgColour = new Color(0,0,0); // foreground
    // Background colour of main panel
    // private Color mainBgColour = new Color(51,167,202);
    private Color mainBgColour = new Color(46,114,173);
    private Color outerBorderLineColour = new Color(22,59,121,255);
    private Color innerborderLineColour = new Color(64,28,99,255);

    private Font fontHeading = new Font("unispace", Font.BOLD, 24);
    private Font fontLabel = new Font("unispace", Font.PLAIN, 18);
    private Font fontText = new Font("unispace", Font.PLAIN, 16);
    private Font fontButton = new Font("unispace", Font.PLAIN, 12);

    // Background image variable declaration
    private Image image;
    private final String imageFolderPath = "src/main/resources/images/backgrounds/";

    // Public variables
    // Sets the Frame Title (top left corner)
    public static final String FRAME_TITLE = "Game Survey";

    // Game that calls the survey (CHANGE TO BE REUSABLE)
    public String callingGame = "GAME THAT CALLS THE SURVEY";

    // Main Survey Class
    public Survey(MinigameNetworkClient mnClient) {

        // Survey main panel layout
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        readBackgroundImage();

        //This creates a nice main Survey frame.

        // Frame size and position
        borderPosition = BorderFactory.createEmptyBorder(80, 80, 80, 80);
        // Creates a raised coloured bevel border
        raisedBevel = BorderFactory.createRaisedBevelBorder();
        outerColourBorder = BorderFactory.createLineBorder(outerBorderLineColour, 4);
        styledOuterBorder = BorderFactory.createCompoundBorder(outerColourBorder, raisedBevel);
        // Creates a lowered coloured bevel border
        loweredBevel = BorderFactory.createLoweredBevelBorder();
        innerColourBorder = BorderFactory.createLineBorder(innerborderLineColour, 3);
        styledInnerBorder = BorderFactory.createCompoundBorder(innerColourBorder, loweredBevel);
        // Combines the borders and sets them
        styledBorders = BorderFactory.createCompoundBorder(outerColourBorder, innerColourBorder);
        finalBorder = BorderFactory.createCompoundBorder(borderPosition, styledBorders);
        this.setBorder(finalBorder);

        // Title Panel
        titlePanel = new JPanel();
        headingLabel = new JLabel();
        headingLabel.setText("Game Survey");
        headingLabel.setFont(fontHeading);

        titlePanel.add(headingLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // gameName Label
        gameNameLabel = new JLabel();
        gameNameLabel.setText("Game Name: ");
        gameNameLabel.setFont(fontLabel);

        // gameName TextLabel
        gameNameTextLabel = new JLabel();
        gameNameTextLabel.setText(callingGame);
        gameNameTextLabel.setFont(fontText);

        // User Interface Rating Label
        uiRatingLabel = new JLabel();
        uiRatingLabel.setText("User Interface Rating: ");
        uiRatingLabel.setFont(fontLabel);

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
        uiRatingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Initialise the feedbackArray
        feedbackArray = new JSONArray();

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
        enjoymentLabel.setFont(fontLabel);

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
        enjoymentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        enjoymentButtonGroup= new ButtonGroup();
        enjoymentButtonGroup.add(enjoymentOne);
        enjoymentButtonGroup.add(enjoymentTwo);
        enjoymentButtonGroup.add(enjoymentThree);
        enjoymentButtonGroup.add(enjoymentFour);
        enjoymentButtonGroup.add(enjoymentFive);

        // Functionality Rating Label
        functionalityLabel = new JLabel();
        functionalityLabel.setText("Functionality Rating: ");
        functionalityLabel.setFont(fontLabel);

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
        functionalityPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        functionalityButtonGroup= new ButtonGroup();
        functionalityButtonGroup.add(functionalityOne);
        functionalityButtonGroup.add(functionalityTwo);
        functionalityButtonGroup.add(functionalityThree);
        functionalityButtonGroup.add(functionalityFour);
        functionalityButtonGroup.add(functionalityFive);

        // feedback Label
        feedbackLabel = new JLabel();
        feedbackLabel.setText("Feedback: ");
        feedbackLabel.setFont(fontLabel);

        // feedback Panel
        feedbackPanel = new JPanel();
        feedbackText = new JTextArea();
        feedbackText.setColumns(30);
        feedbackText.setLineWrap(true);
        feedbackText.setRows(5);
        feedbackText.setWrapStyleWord(true);
        feedbackText.setFont(fontText);
        feedbackText.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
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
        surveyQuestionsPanelRight.add(gameNameTextLabel);
        surveyQuestionsPanelRight.add(uiRatingPanel);
        surveyQuestionsPanelRight.add(enjoymentPanel);
        surveyQuestionsPanelRight.add(functionalityPanel);
        surveyQuestionsPanelRight.add(feedbackPanel);

        // surveyQuestionsPanelGroup (incorporates all panels from the left and right groups for the survey)
        surveyQuestionsPanelGroup = new JPanel();
        surveyQuestionsPanelGroup.setLayout(new GridLayout(0, 2));
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelLeft);
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelRight);
        this.add(surveyQuestionsPanelGroup, BorderLayout.CENTER);


        // Back Button
        backPanel = new JPanel();
        backButton = new JButton("Back");
        backButton.setFont(fontButton);
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
        backPanel.add(backButton);
        
        // Submit Button
        submitPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.setFont(fontButton);
        submitButton.addActionListener(e -> submit());
        submitPanel.add(submitButton);

        panelColourChange(mainBgColour, fgColour);
        // buttonColourChange(bgColour, fgColour);

        // Footer Panel
        footerPanel = new JPanel();
        // Have to set footer panel bg manually
        footerPanel.setBackground(mainBgColour);
        footerPanel.add(backPanel, BorderLayout.WEST);
        footerPanel.add(submitPanel, BorderLayout.EAST);
        this.add(footerPanel, BorderLayout.SOUTH);

        // ADD REQUEST TO ENDPOINTS HERE!!!
    }

    // Public Functions
    public Image readBackgroundImage() {
        try
        {
            image = javax.imageio.ImageIO.read(new File(imageFolderPath + "nebula.jpg"));
        }
        catch (Exception e) { e.printStackTrace(); /*handled in paintComponent()*/ }
        return(image);
    }

    public void submit() {
        String text = feedbackText.getText();
    
        // JSON object to store the feedback data
        JSONObject feedbackObject = new JSONObject();
        feedbackObject.put("user_id", "123"); // Replace with real user ID
        feedbackObject.put("timestamp", getCurrentTimestamp());
        feedbackObject.put("feedback_text", text);
    
        feedbackArray.add(feedbackObject);

        // Save JSON object to a local file
        saveFeedbackToJsonFile(feedbackArray);
    }

    // Change colour of panels (overrides singular setting of colour)
    public void panelColourChange(Color backColour, Color foreColour) {
        // footerPanel breaks the code, if clause sorts it
        JPanel panels[] = {titlePanel, backPanel, surveyQuestionsPanelGroup, surveyQuestionsPanelLeft, surveyQuestionsPanelRight, feedbackPanel, submitPanel, uiRatingPanel, enjoymentPanel, functionalityPanel, footerPanel};
        for(JPanel panel: panels){
            if(panel != null) {
            panel.setBackground(backColour);
            panel.setForeground(foreColour);
            }
        }
    }

    // Change colour of radio buttons (overrides singular setting of colour)
    public void radioColourChange(Color backColour, Color foreColour) {
        JRadioButton rbuttons[] = {uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive, enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive, functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive};
        for(JRadioButton rb: rbuttons){
            if(rb != null) {
            rb.setBackground(backColour);
            rb.setForeground(foreColour);
            }
        }
    }

    // Change colour of buttons (overrides singular setting of colour)
    public void buttonColourChange(Color backColour, Color foreColour) {
        JButton buttons[] = {backButton, submitButton};
        for(JButton button: buttons){
            if(button != null) {
            button.setBackground(backColour);
            button.setForeground(foreColour);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    // Private Functions
    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    private void saveFeedbackToJsonFile(JSONArray feedbackArray) {
        try (FileWriter fileWriter = new FileWriter("feedback.json", false)) {
            // Append feedback to existing JSON file, otherwise create new one
            fileWriter.write(feedbackArray.toJSONString());
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Add proper exception handling
        }
    }

    // Protected Functions
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(image != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
        else {
            System.out.println("no image to process");
        }
    }
}
