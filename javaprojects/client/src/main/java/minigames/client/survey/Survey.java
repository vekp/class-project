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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Survey implements ActionListener {
    private int count = 0;
    private JLabel label, headingLabel;
    public JPanel panel;
    private JButton button, backButton;
    private JTextField textField;
    private JLabel feedbackLabel;

    public Survey(ActionListener goBack) {
        headingLabel = new JLabel();
        button = new JButton("Click me... I can count!");
        backButton = new JButton("Back");
        label = new JLabel("Number of clicks: 0");
        panel = new JPanel();
        textField = new JTextField(20);
        feedbackLabel = new JLabel("Enter feedback here: ");

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
        panel.add(feedbackLabel);
        panel.add(textField);
        panel.add(label);
        panel.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        label.setText("Number of clicks: " + count);

        if (e.getSource() == button) {
            String feedbackText = textField.getText();
    
            // JSON object to store the feedback data
            JSONObject feedbackObject = new JSONObject();
            feedbackObject.put("user_id", "123"); // Replace with real user ID
            feedbackObject.put("timestamp", getCurrentTimestamp());
            feedbackObject.put("feedback_text", feedbackText);
    
            // Save JSON object to a local file
            saveFeedbackToJsonFile(feedbackObject);
        }
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
