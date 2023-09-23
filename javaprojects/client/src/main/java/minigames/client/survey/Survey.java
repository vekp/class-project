package minigames.client.survey;

import minigames.client.MinigameNetworkClient;
import minigames.client.survey.SurveyResults;

import io.vertx.core.json.JsonObject;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.swing.border.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.*;

public class Survey extends JPanel implements ActionListener {

    // (labels,buttons etc need to be registered here to be accessible by the methods in this class)
    private JPanel titlePanel, backPanel, gameNamePanel, surveyQuestionsPanelGroup, surveyQuestionsPanelLeft, surveyQuestionsPanelRight, feedbackPanel, submitPanel, footerPanel, uiRatingPanel, enjoymentPanel, functionalityPanel, difficultyPanel, resultsPanel; 
    private JLabel counterLabel, headingLabel, testLabel, gameNameLabel, blankLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, gameNameTextLabel, helpLabel;
    private JButton backButton, submitButton, resultsButton;
    private JTextArea feedbackText;
    private JScrollPane feedbackScrollPane;
    private JRadioButton uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive, enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive, functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive, difficultyOne, difficultyTwo, difficultyThree, difficultyFour, difficultyFive;
    private ButtonGroup uiRatingButtonGroup, enjoymentButtonGroup, functionalityButtonGroup, difficultyButtonGroup;
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

    private Font fontHeading = new Font("Open sans semibold", Font.BOLD, 24);
    private Font fontLabel = new Font("Open sans semibold", Font.PLAIN, 20);
    private Font fontText = new Font("Lucida console", Font.PLAIN, 16);
    private Font fontHelp = new Font("Open sans semibold", Font.PLAIN, 14);
    private Font fontButton = new Font("Open sans semibold", Font.PLAIN, 12);

    private final PolicyFactory sanitiser = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING);
    // Background image variable declaration
    private Image image;
    private final String imageFolderPath = "src/main/resources/images/backgrounds/";
    private final String background = "nebula.jpg";
    // Public variables
    // Sets the Frame Title (top left corner)
    public static final String FRAME_TITLE = "Game Survey";

    // Main Survey Class
    public Survey(MinigameNetworkClient mnClient, String gameId, String gameName) {

        // Survey main panel layout
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());
        readBackgroundImage();

        //This creates a nice main Survey frame.

        // Frame size and position
        borderPosition = BorderFactory.createEmptyBorder(10, 70, 10, 70);
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
        gameNameLabel.setHorizontalAlignment(JLabel.CENTER);

        // Blank Label (currently allows the grid items the correct spacing until a refactor occurs)
        blankLabel = new JLabel();

        // gameName TextLabel
        gameNameTextLabel = new JLabel();
        gameNameTextLabel.setText(gameName);
        gameNameTextLabel.setFont(fontText);
        gameNameTextLabel.setHorizontalAlignment(JLabel.CENTER);

        // rating explanation help TextLabel
        helpLabel = new JLabel();
        helpLabel.setText("<html>"+ "Using a scale of 1: Very Low to 5: Very High. Please rate the following questions." +"</html>");
        helpLabel.setFont(fontHelp);
        helpLabel.setForeground(Color.YELLOW);
        helpLabel.setHorizontalAlignment(JLabel.CENTER);

        // User Interface Rating Label
        uiRatingLabel = new JLabel();
        uiRatingLabel.setText("User Interface: ");
        uiRatingLabel.setFont(fontLabel);
        uiRatingLabel.setHorizontalAlignment(JLabel.CENTER);

        // User Interface Rating Panel
        uiRatingPanel = new JPanel();
        uiRatingPanel.setLayout(new GridLayout(0, 5));
        uiRatingOne = new JRadioButton("1");
        uiRatingTwo = new JRadioButton("2");
        uiRatingThree = new JRadioButton("3");
        uiRatingFour = new JRadioButton("4");
        uiRatingFive = new JRadioButton("5");
        uiRatingThree.setSelected(true);
        for (Component c : new Component[] { uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive }) {
            uiRatingPanel.add(c);
        }
        uiRatingPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 1));

        // Ensures only one of the radio buttons are selected at a time
        uiRatingButtonGroup= new ButtonGroup();
        uiRatingButtonGroup.add(uiRatingOne);
        uiRatingButtonGroup.add(uiRatingTwo);
        uiRatingButtonGroup.add(uiRatingThree);
        uiRatingButtonGroup.add(uiRatingFour);
        uiRatingButtonGroup.add(uiRatingFive);

        // Enjoyment Rating Label
        enjoymentLabel = new JLabel();
        enjoymentLabel.setText("Enjoyment: ");
        enjoymentLabel.setFont(fontLabel);
        enjoymentLabel.setHorizontalAlignment(JLabel.CENTER);

        // Enjoyment Rating Panel
        enjoymentPanel = new JPanel();
        enjoymentPanel.setLayout(new GridLayout(0, 5));
        enjoymentOne = new JRadioButton("1");
        enjoymentTwo = new JRadioButton("2");
        enjoymentThree = new JRadioButton("3");
        enjoymentFour = new JRadioButton("4");
        enjoymentFive = new JRadioButton("5");
        enjoymentThree.setSelected(true);
        for (Component c : new Component[] { enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive }) {
            enjoymentPanel.add(c);
        }
        enjoymentPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 1));


        enjoymentButtonGroup= new ButtonGroup();
        enjoymentButtonGroup.add(enjoymentOne);
        enjoymentButtonGroup.add(enjoymentTwo);
        enjoymentButtonGroup.add(enjoymentThree);
        enjoymentButtonGroup.add(enjoymentFour);
        enjoymentButtonGroup.add(enjoymentFive);

        // Functionality Rating Label
        functionalityLabel = new JLabel();
        functionalityLabel.setText("Functionality: ");
        functionalityLabel.setFont(fontLabel);
        functionalityLabel.setHorizontalAlignment(JLabel.CENTER);

        // Functionality Rating Panel
        functionalityPanel = new JPanel();
        functionalityPanel.setLayout(new GridLayout(0, 5));
        functionalityOne = new JRadioButton("1");
        functionalityTwo = new JRadioButton("2");
        functionalityThree = new JRadioButton("3");
        functionalityFour = new JRadioButton("4");
        functionalityFive = new JRadioButton("5");
        functionalityThree.setSelected(true);
        for (Component c : new Component[] { functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive }) {
            functionalityPanel.add(c);
        }
        functionalityPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 1));


        functionalityButtonGroup= new ButtonGroup();
        functionalityButtonGroup.add(functionalityOne);
        functionalityButtonGroup.add(functionalityTwo);
        functionalityButtonGroup.add(functionalityThree);
        functionalityButtonGroup.add(functionalityFour);
        functionalityButtonGroup.add(functionalityFive);

        // Game Difficulty Rating Label
        difficultyLabel = new JLabel();
        difficultyLabel.setText("Game Difficulty: ");
        difficultyLabel.setFont(fontLabel);
        difficultyLabel.setHorizontalAlignment(JLabel.CENTER);

        // Game Difficulty Rating Panel
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(0, 5));
        difficultyOne = new JRadioButton("1");
        difficultyTwo = new JRadioButton("2");
        difficultyThree = new JRadioButton("3");
        difficultyFour = new JRadioButton("4");
        difficultyFive = new JRadioButton("5");
        difficultyThree.setSelected(true);
        for (Component c : new Component[] { difficultyOne, difficultyTwo, difficultyThree, difficultyFour, difficultyFive }) {
            difficultyPanel.add(c);
        }
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 1));

        // Ensures only one of the radio buttons are selected at a time
        difficultyButtonGroup= new ButtonGroup();
        difficultyButtonGroup.add(difficultyOne);
        difficultyButtonGroup.add(difficultyTwo);
        difficultyButtonGroup.add(difficultyThree);
        difficultyButtonGroup.add(difficultyFour);
        difficultyButtonGroup.add(difficultyFive);

        // feedback Label
        feedbackLabel = new JLabel();
        feedbackLabel.setText("Feedback: ");
        feedbackLabel.setFont(fontLabel);
        feedbackLabel.setHorizontalAlignment(JLabel.CENTER);

        // feedback Panel
        feedbackPanel = new JPanel();
        feedbackText = new JTextArea();
        feedbackText.setColumns(30);
        feedbackText.setLineWrap(true);
        feedbackText.setRows(5);
        feedbackText.setWrapStyleWord(true);
        feedbackText.setFont(fontText);
        feedbackText.setMargin(new Insets(5,15,5,5));
        feedbackText.setBackground(Color.WHITE);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        feedbackText.setBorder(BorderFactory.createCompoundBorder(border, 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        // feedbackScrollPane = new JScrollPane(feedbackText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // feedbackPanel.add(feedbackScrollPane);
        feedbackPanel.add(feedbackText);

        // surveyQuestionsPanelLeft (incorporates all Question titles for the survey)
        surveyQuestionsPanelLeft = new JPanel();
        surveyQuestionsPanelLeft.setLayout(new GridLayout(7, 0));
        for (Component c : new Component[] { gameNameLabel, blankLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, feedbackLabel }) {
            surveyQuestionsPanelLeft.add(c);
        }
        // surveyQuestionsPanelRight (incorporates all Question responses for the survey)
        surveyQuestionsPanelRight = new JPanel();
        surveyQuestionsPanelRight.setLayout(new GridLayout(7, 0));
        for (Component c : new Component[] { gameNameTextLabel, helpLabel, uiRatingPanel, enjoymentPanel, functionalityPanel, difficultyPanel, feedbackPanel }) {
            surveyQuestionsPanelRight.add(c);
        }
        // surveyQuestionsPanelGroup (incorporates all panels from the left and right groups for the survey)
        surveyQuestionsPanelGroup = new JPanel();
        surveyQuestionsPanelGroup.setLayout(new GridLayout(0, 2));
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelLeft);
        surveyQuestionsPanelGroup.add(surveyQuestionsPanelRight);
        this.add(surveyQuestionsPanelGroup, BorderLayout.CENTER);

        // Back Button
        backPanel = new JPanel();
        backButton = new JButton("Exit");
        backButton.setFont(fontButton);
        backButton.addActionListener(e -> mnClient.runMainMenuSequence());
        backPanel.add(backButton);
        
        // Submit Button
        submitPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.setFont(fontButton);
        submitButton.addActionListener(e -> submit(mnClient, gameId));
        submitPanel.add(submitButton);

        // Results Button
        resultsPanel = new JPanel();
        resultsButton = new JButton("Results");
        resultsButton.setFont(fontButton);
        resultsButton.addActionListener(e -> {
            mnClient.getMainWindow().clearAll();
            JPanel results = new SurveyResults(mnClient, gameId);
            // frame.setTitle(Survey.FRAME_TITLE);
            mnClient.getMainWindow().addCenter(results);
            mnClient.getMainWindow().pack();
        });
        resultsPanel.add(resultsButton);
        
        // Footer Panel
        footerPanel = new JPanel();
        footerPanel.add(backPanel, BorderLayout.WEST);
        footerPanel.add(submitPanel, BorderLayout.CENTER);
        footerPanel.add(resultsPanel, BorderLayout.EAST);
        this.add(footerPanel, BorderLayout.SOUTH);
        
        // Calling this here will override any other colour change calls
        panelColourChange(mainBgColour, fgColour);
        labelColourChange(Color.WHITE, Color.WHITE);
    }

    // Public Functions

    public Image readBackgroundImage() {
        try
        {
            image = javax.imageio.ImageIO.read(new File(imageFolderPath + background));
        }
        catch (Exception e) { e.printStackTrace(); /*handled in paintComponent()*/ }
        return(image);
    }

    // WIP creating dynamic radio buttons

    // public void generateRadioGroup(String name, int totalButtons, String panelName) {
    //     panelName.setLayout(new GridLayout(0, totalButtons));
        
        // uiRatingPanel = new JPanel();
        // uiRatingPanel.setLayout(new GridLayout(0, 5));
        // uiRatingOne = new JRadioButton("1");
        // uiRatingTwo = new JRadioButton("2");
        // uiRatingThree = new JRadioButton("3");
        // uiRatingFour = new JRadioButton("4");
        // uiRatingFive = new JRadioButton("5");
        // uiRatingThree.setSelected(true);
        // uiRatingPanel.add(uiRatingOne);
        // uiRatingPanel.add(uiRatingTwo);
        // uiRatingPanel.add(uiRatingThree);
        // uiRatingPanel.add(uiRatingFour);
        // uiRatingPanel.add(uiRatingFive);
        // uiRatingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // // Ensures only one of the radio buttons are selected at a time
        // uiRatingButtonGroup= new ButtonGroup();
        // uiRatingButtonGroup.add(uiRatingOne);
        // uiRatingButtonGroup.add(uiRatingTwo);
        // uiRatingButtonGroup.add(uiRatingThree);
        // uiRatingButtonGroup.add(uiRatingFour);
        // uiRatingButtonGroup.add(uiRatingFive);
    // }

    public void submit(MinigameNetworkClient mnClient, String gameId) {
        String text = feedbackText.getText();

        // Validate the feedbackText input
        if (!isValidText(text)) {
            // Display an error message or handle the invalid input as needed
            JOptionPane.showMessageDialog(this, "Invalid feedback text. Please enter valid text.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sanitise the feedbackText to remove any potentially harmful content
        String sanitisedText = sanitiseText(text);

        // Get the selected values from the radio button groups
        int uiRating = Integer.parseInt(getSelectedRadioButtonValue(uiRatingButtonGroup));
        int enjoymentRating = Integer.parseInt(getSelectedRadioButtonValue(enjoymentButtonGroup));
        int functionalityRating = Integer.parseInt(getSelectedRadioButtonValue(functionalityButtonGroup));
        int difficultyRating = Integer.parseInt(getSelectedRadioButtonValue(difficultyButtonGroup));

        JsonObject surveyData = new JsonObject()
            .put("game_id", gameId)
            .put("ui_rating", uiRating)
            .put("enjoyment_rating", enjoymentRating)
            .put("functionality_rating", functionalityRating)
            .put("difficulty_rating", difficultyRating)
            .put("feedback_text", sanitisedText);

        mnClient.sendSurveyData(surveyData).onSuccess(e -> {
            mnClient.getMainWindow().clearAll();
            JPanel results = new SurveyResults(mnClient, gameId);
            // frame.setTitle(Survey.FRAME_TITLE);
            mnClient.getMainWindow().addCenter(results);
            mnClient.getMainWindow().pack();
        });
    }

    // Change colour of labels (overrides singular setting of colour) Does not include helpLabel
    public void labelColourChange(Color backColour, Color foreColour) {
        JLabel labels[] = {counterLabel, headingLabel, testLabel, gameNameLabel, blankLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, gameNameTextLabel};
        for(JLabel label: labels){
            if(label != null) {
                label.setBackground(backColour);
                label.setForeground(foreColour);
            }
        }
    }
    
    // Change colour of panels (overrides singular setting of colour)
    public void panelColourChange(Color backColour, Color foreColour) {
        // footerPanel breaks the code, if clause sorts it
        JPanel panels[] = {titlePanel, backPanel, surveyQuestionsPanelGroup, surveyQuestionsPanelLeft, surveyQuestionsPanelRight, feedbackPanel, submitPanel, resultsPanel, uiRatingPanel, enjoymentPanel, functionalityPanel, difficultyPanel, footerPanel};
        for(JPanel panel: panels){
            if(panel != null) {
                panel.setBackground(backColour);
                panel.setForeground(foreColour);
            }
        }
    }

    // Change colour of radio buttons (overrides singular setting of colour)
    public void radioColourChange(Color backColour, Color foreColour) {
        JRadioButton rbuttons[] = {uiRatingOne, uiRatingTwo, uiRatingThree, uiRatingFour, uiRatingFive, enjoymentOne, enjoymentTwo, enjoymentThree, enjoymentFour, enjoymentFive, functionalityOne, functionalityTwo, functionalityThree, functionalityFour, functionalityFive, difficultyOne, difficultyTwo, difficultyThree, difficultyFour, difficultyFive};
        for(JRadioButton rb: rbuttons){
            if(rb != null) {
                rb.setBackground(backColour);
                rb.setForeground(foreColour);
            }
        }
    }

    // Change colour of buttons (overrides singular setting of colour)
    public void buttonColourChange(Color backColour, Color foreColour) {
        JButton buttons[] = {backButton, submitButton, resultsButton};
        for(JButton button: buttons){
            if(button != null) {
                button.setBackground(backColour);
                button.setForeground(foreColour);
            }
        }
    }

    // Private Functions

    // Validate the feedbackText input
    private boolean isValidText(String text) {
        // Check for common SQL injection patterns
        if (text != null && text.matches("(?i).*\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER)\\b.*")) {
            return false;
        }
        
        // Allow only alphanumeric characters and common punctuation
        if (text != null && !text.matches("^[a-zA-Z0-9 .,!?'\"()%\\-]+$")) {
            return false;
        }

        return true;
    }

    private String sanitiseText(String text) {
        return sanitiser.sanitize(text);
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

    // Protected Functions

    @Override
    public void actionPerformed(ActionEvent e) {
    }

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
