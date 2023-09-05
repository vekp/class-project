package minigames.client.survey;

import minigames.client.MinigameNetworkClient;

import io.vertx.core.json.JsonObject;

// import java.awt.BorderLayout;
// import javax.swing.BorderFactory; 
import javax.swing.border.Border;
// import javax.swing.border.TitledBorder;
// import javax.swing.border.EtchedBorder;
// import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.awt.Color;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Survey extends JPanel implements ActionListener {

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
        headingLabel.setForeground (Color.WHITE);

        titlePanel.add(headingLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // gameName Label
        gameNameLabel = new JLabel();
        gameNameLabel.setText("Game Name: ");
        gameNameLabel.setFont(fontLabel);
        gameNameLabel.setForeground (Color.WHITE);

        // gameName TextLabel
        gameNameTextLabel = new JLabel();
        gameNameTextLabel.setText(callingGame);
        gameNameTextLabel.setFont(fontText);
        gameNameTextLabel.setForeground (Color.WHITE);

        // User Interface Rating Label
        uiRatingLabel = new JLabel();
        uiRatingLabel.setText("User Interface Rating: ");
        uiRatingLabel.setFont(fontLabel);
        uiRatingLabel.setForeground (Color.WHITE);

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
        enjoymentLabel.setForeground (Color.WHITE);

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
        functionalityLabel.setForeground (Color.WHITE);

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
        feedbackLabel.setForeground (Color.WHITE);

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
        submitButton.addActionListener(e -> submit(mnClient));
        submitPanel.add(submitButton);
        
        // Footer Panel
        footerPanel = new JPanel();
        // Have to set footer panel bg manually
        footerPanel.add(backPanel, BorderLayout.WEST);
        footerPanel.add(submitPanel, BorderLayout.EAST);
        this.add(footerPanel, BorderLayout.SOUTH);
        
        panelColourChange(mainBgColour, fgColour);
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

    public void submit(MinigameNetworkClient mnClient) {
        String text = feedbackText.getText();
        
        // Get the selected values from the radio button groups
        int uiRating = Integer.parseInt(getSelectedRadioButtonValue(uiRatingButtonGroup));
        int enjoymentRating = Integer.parseInt(getSelectedRadioButtonValue(enjoymentButtonGroup));
        int functionalityRating = Integer.parseInt(getSelectedRadioButtonValue(functionalityButtonGroup));

        JsonObject surveyData = new JsonObject()
            .put("user_id", 111)
            .put("ui_rating", uiRating)
            .put("enjoyment_rating", enjoymentRating)
            .put("functionality_rating", functionalityRating)
            .put("feedback_text", text);

        mnClient.sendSurveyData(surveyData).onSuccess(e -> mnClient.runMainMenuSequence());
    }

    // Get the selected radio button value from a ButtonGroup
    private String getSelectedRadioButtonValue(ButtonGroup buttonGroup) {
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return "";
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
