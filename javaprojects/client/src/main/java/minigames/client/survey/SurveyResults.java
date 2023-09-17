package minigames.client.survey;

import minigames.client.MinigameNetworkClient;
import minigames.client.survey.Survey;

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

public class SurveyResults extends JPanel implements ActionListener{

    // (labels,buttons etc need to be registered here to be accessible by the methods in this class)
    private JPanel titlePanel, closePanel, gameNamePanel, surveyResultsPanelGroup, surveyResultsPanelLeft, surveyResultsPanelRight, footerPanel, overallRatingLeftPanel, feedbackPanel; 
    private JLabel counterLabel, headingLabel, testLabel, gameNameLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, gameNameTextLabel, helpLabel, uiRatingRatingLabel, enjoymentRatingLabel, functionalityRatingLabel, difficultyRatingLabel, feedbackRatingLabel, overallRatingLabelLeft, overallRatingHelpLabel, overallRatingLabelRight;
    private JButton closeButton;
    private JTextArea feedbackText;
    private JComboBox gameNameComboBox;
    private Border borderPosition, raisedBevel, loweredBevel, outerColourBorder, styledOuterBorder, innerColourBorder, styledInnerBorder, styledBorders, finalBorder;

    private Color bgColour = new Color(255,255,255); // background
    private Color fgColour = new Color(0,0,0); // foreground Black

    // Background colour of main panel
    private Color mainBgColour = new Color(46,114,173);
    private Color outerBorderLineColour = new Color(0, 0, 0); // Black
    private Color innerborderLineColour = new Color(64,28,99,255);

    private Font fontHeading = new Font("Open sans semibold", Font.BOLD, 24);
    private Font fontLabel = new Font("Open sans semibold", Font.PLAIN, 18);
    private Font fontRatingResult = new Font("Open sans semibold", Font.PLAIN, 16);
    private Font fontText = new Font("Lucida console", Font.PLAIN, 16);
    private Font fontHelp = new Font("Open sans semibold", Font.PLAIN, 14);
    private Font fontButton = new Font("Open sans semibold", Font.PLAIN, 12);
    
    private Image image;
    private final String imageFolderPath = "src/main/resources/images/backgrounds/";
    private final String background = "space_planet_asteroids.jpg";

    // Our called games names will be stored here for use in a combo box
    public String[] gameNames = {"testGame1", "testGame2", "testGame3", "testGame4"};

    public SurveyResults(MinigameNetworkClient mnClient, String gameId) {


        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridLayout(0, 1));
        this.setLayout(new BorderLayout());

        readBackgroundImage();
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
        headingLabel.setText("Survey Results");
        headingLabel.setFont(fontHeading);
        headingLabel.setHorizontalAlignment(JLabel.CENTER);

        // rating explanation help TextLabel
        helpLabel = new JLabel();
        helpLabel.setText("<html>"+ "Survey Results are calculated from an average of all the provided answers." +"</html>");
        helpLabel.setFont(fontHelp);
        helpLabel.setForeground(Color.BLACK);
        helpLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.setLayout(new GridLayout(2, 0));
        titlePanel.add(headingLabel);
        titlePanel.add(helpLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // gameName Label
        gameNameLabel = new JLabel();
        gameNameLabel.setText("Choose Game Results: ");
        gameNameLabel.setFont(fontLabel);
        gameNameLabel.setHorizontalAlignment(JLabel.CENTER);

        // User Interface Rating Label
        uiRatingLabel = new JLabel();
        uiRatingLabel.setText("User Interface: ");
        uiRatingLabel.setFont(fontLabel);
        uiRatingLabel.setHorizontalAlignment(JLabel.CENTER);

        // Enjoyment Rating Label
        enjoymentLabel = new JLabel();
        enjoymentLabel.setText("Enjoyment: ");
        enjoymentLabel.setFont(fontLabel);
        enjoymentLabel.setHorizontalAlignment(JLabel.CENTER);

        // Functionality Rating Label
        functionalityLabel = new JLabel();
        functionalityLabel.setText("Functionality: ");
        functionalityLabel.setFont(fontLabel);
        functionalityLabel.setHorizontalAlignment(JLabel.CENTER);

        // Game Difficulty Rating Label
        difficultyLabel = new JLabel();
        difficultyLabel.setText("Game Difficulty: ");
        difficultyLabel.setFont(fontLabel);
        difficultyLabel.setHorizontalAlignment(JLabel.CENTER);

        // feedback Label
        feedbackLabel = new JLabel();
        feedbackLabel.setText("Feedback: ");
        feedbackLabel.setFont(fontLabel);
        feedbackLabel.setHorizontalAlignment(JLabel.CENTER);

        // Overall Rating Label
        overallRatingLabelLeft = new JLabel();
        overallRatingLabelLeft.setText("Overall Rating: ");
        overallRatingLabelLeft.setFont(fontLabel);
        overallRatingLabelLeft.setHorizontalAlignment(JLabel.CENTER);

        // rating explanation help TextLabel
        overallRatingHelpLabel = new JLabel();
        overallRatingHelpLabel.setText("<html>"+ "Overall total rating (excludes Difficulty rating)." +"</html>");
        overallRatingHelpLabel.setFont(fontButton);
        overallRatingHelpLabel.setForeground(Color.BLACK);
        overallRatingHelpLabel.setHorizontalAlignment(JLabel.CENTER);

        overallRatingLeftPanel = new JPanel();
        overallRatingLeftPanel.setLayout(new GridLayout(2, 0));
        overallRatingLeftPanel.add(overallRatingLabelLeft);
        overallRatingLeftPanel.add(overallRatingHelpLabel);

        // surveyResultsPanelLeft (incorporates all Result titles for the survey)
        surveyResultsPanelLeft = new JPanel();
        surveyResultsPanelLeft.setLayout(new GridLayout(7, 0));
        for (Component d : new Component[] { gameNameLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, overallRatingLeftPanel, feedbackLabel }) {
            surveyResultsPanelLeft.add(d);
        }

        
        // INFO TO BE PULLED FROM DATABASE
        gameNameComboBox = new JComboBox(gameNames);
        gameNameComboBox.setFont(fontRatingResult);
        gameNameComboBox.addActionListener(this);
        uiRatingRatingLabel = new JLabel("DB result");
        uiRatingRatingLabel.setForeground(Color.BLACK);
        uiRatingRatingLabel.setFont(fontRatingResult);
        uiRatingRatingLabel.setHorizontalAlignment(JLabel.CENTER);
        enjoymentRatingLabel = new JLabel("DB result");
        enjoymentRatingLabel.setForeground(Color.BLACK);
        enjoymentRatingLabel.setFont(fontRatingResult);
        enjoymentRatingLabel.setHorizontalAlignment(JLabel.CENTER);
        functionalityRatingLabel = new JLabel("DB result");
        functionalityRatingLabel.setForeground(Color.BLACK);
        functionalityRatingLabel.setFont(fontRatingResult);
        functionalityRatingLabel.setHorizontalAlignment(JLabel.CENTER);
        difficultyRatingLabel = new JLabel("DB result");
        difficultyRatingLabel.setForeground(Color.BLACK);
        difficultyRatingLabel.setFont(fontRatingResult);
        difficultyRatingLabel.setHorizontalAlignment(JLabel.CENTER);
        overallRatingLabelRight = new JLabel("DB result");
        overallRatingLabelRight.setForeground(Color.BLACK);
        overallRatingLabelRight.setFont(fontRatingResult);
        overallRatingLabelRight.setHorizontalAlignment(JLabel.CENTER);
        feedbackPanel = new JPanel();
        feedbackText = new JTextArea();
        feedbackText.setColumns(30);
        feedbackText.setLineWrap(true);
        feedbackText.setRows(5);
        feedbackText.setEditable(false);
        feedbackText.setText("DB results listed as items 1. 2. 3. etc");
        feedbackText.setWrapStyleWord(true);
        feedbackText.setFont(fontText);
        feedbackText.setMargin(new Insets(5,15,5,5));
        feedbackText.setBackground(Color.BLACK);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        feedbackText.setBorder(BorderFactory.createCompoundBorder(border, 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        feedbackPanel.add(feedbackText);

        // surveyResultsPanelRight (incorporates all Question responses for the survey)
        surveyResultsPanelRight = new JPanel();
        surveyResultsPanelRight.setLayout(new GridLayout(7, 0));
        for (Component d : new Component[] { gameNameComboBox, uiRatingRatingLabel, enjoymentRatingLabel, functionalityRatingLabel, difficultyRatingLabel, overallRatingLabelRight, feedbackPanel }) {
            surveyResultsPanelRight.add(d);
        }

        // surveyResultsPanelGroup (incorporates all panels from the left and right groups for the survey)
        surveyResultsPanelGroup = new JPanel();
        surveyResultsPanelGroup.setLayout(new GridLayout(0, 2));
        surveyResultsPanelGroup.add(surveyResultsPanelLeft);
        surveyResultsPanelGroup.add(surveyResultsPanelRight);
        this.add(surveyResultsPanelGroup, BorderLayout.CENTER);

        // Close Button
        closePanel = new JPanel();
        closeButton = new JButton("Close");
        closeButton.setFont(fontButton);
        closeButton.addActionListener(e -> mnClient.runMainMenuSequence());
        closePanel.add(closeButton);

        // Footer Panel
        footerPanel = new JPanel();
        footerPanel.add(closePanel, BorderLayout.CENTER);
        this.add(footerPanel, BorderLayout.SOUTH);

        panelColourChange(bgColour, fgColour);
        labelColourChange(Color.WHITE, Color.BLUE);
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

    // Change colour of labels (overrides singular setting of colour) Does not include helpLabel or Rating Labels
    public void labelColourChange(Color backColour, Color foreColour) {
        JLabel labels[] = {counterLabel, headingLabel, testLabel, gameNameLabel, feedbackLabel, uiRatingLabel, enjoymentLabel, functionalityLabel, difficultyLabel, gameNameTextLabel, overallRatingLabelLeft};
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
        JPanel panels[] = {titlePanel, closePanel, feedbackPanel, surveyResultsPanelGroup, surveyResultsPanelLeft, surveyResultsPanelRight, overallRatingLeftPanel, footerPanel};
        for(JPanel panel: panels){
            if(panel != null) {
                panel.setBackground(backColour);
                panel.setForeground(foreColour);
            }
        }
    }

    // Override Functions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameNameComboBox) {
            System.out.println(gameNameComboBox.getSelectedItem().toString());
            // mnClient.setGameId(gameNameComboBox.getSelectedItem().toString());
        }
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
