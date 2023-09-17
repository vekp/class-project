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
    private JPanel titlePanel, closePanel, gameNamePanel, surveyResultsPanelGroup, surveyResultsPanelLeft, surveyResultsPanelRight, feedbackPanel, footerPanel, uiRatingPanel, enjoymentPanel, functionalityPanel, difficultyPanel, overallRatingPanel; 
    private JButton closeButton;
    private JLabel headingLabel;
    private JComboBox gameNameComboBox;
    private Border borderPosition, raisedBevel, loweredBevel, outerColourBorder, styledOuterBorder, innerColourBorder, styledInnerBorder, styledBorders, finalBorder;

    private Color bgColour = new Color(255,255,255); // background
    private Color fgColour = new Color(0,0,0); // foreground

    // Background colour of main panel
    private Color mainBgColour = new Color(46,114,173);
    private Color outerBorderLineColour = new Color(0, 0, 0);
    private Color innerborderLineColour = new Color(64,28,99,255);

    private Font fontHeading = new Font("Open sans semibold", Font.BOLD, 24);
    private Font fontLabel = new Font("Open sans semibold", Font.PLAIN, 20);
    private Font fontText = new Font("Lucida console", Font.PLAIN, 16);
    private Font fontHelp = new Font("Open sans semibold", Font.PLAIN, 14);
    private Font fontButton = new Font("Open sans semibold", Font.PLAIN, 12);
    
    private Image image;
    private final String imageFolderPath = "src/main/resources/images/backgrounds/";
    private final String background = "space_planet_asteroids.jpg";

    // Our called games names will be stored here for use in a combo box
    public String[] gameNames = {};

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
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setBackground(Color.WHITE);

        titlePanel.add(headingLabel);
        this.add(titlePanel, BorderLayout.NORTH);
        this.setVisible(true);

        gameNameComboBox = new JComboBox(gameNames);
        gameNameComboBox.addActionListener(this);

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
