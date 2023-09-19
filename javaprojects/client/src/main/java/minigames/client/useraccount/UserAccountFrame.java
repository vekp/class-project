package minigames.client.useraccount;
import io.vertx.core.Future;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import minigames.client.useraccount.FileHandler.*;
import minigames.client.MinigameNetworkClient;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Future;

public class UserAccountFrame extends JFrame implements ActionListener {

        private JTextField emailField;
        private JPasswordField pinField;
        private JButton enterButton;
        private JButton skipButton;
        private MinigameNetworkClient userClient;
        
        public void userLogin(MinigameNetworkClient client){
                //set up the frame
                setTitle("Login");
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setSize(300,150);
                setResizable(false);
                setLocationRelativeTo(null);
                userClient = client;

                //initialise components required for the window
                JLabel emailLabel = new JLabel("Email: ");
                JLabel pinLabel = new JLabel("Pin: ");
                emailField = new JTextField(20);
                pinField = new JPasswordField(4);
                enterButton = new JButton("Enter");
                skipButton = new JButton("Skip"); // allows the user to skip without logging in.

                // initialise panel that contains all componenets
                JPanel panel = new JPanel();
                JPanel labels = new JPanel(new GridLayout(0,1));
                JPanel input = new JPanel(new GridLayout(0,1));
                panel.add(labels, BorderLayout.WEST);
                panel.add(input, BorderLayout.CENTER);
                labels.add(emailLabel);
                input.add(emailField);
                labels.add(pinLabel);
                input.add(pinField);
                panel.add(skipButton,BorderLayout.SOUTH);
                panel.add(enterButton, BorderLayout.SOUTH);

                // add panel to the frame.
                add(panel);

                enterButton.addActionListener(this);
                skipButton.addActionListener(e -> { this.dispose(); });
        }

        @Override
        public void actionPerformed(ActionEvent e){
                String emailInput = emailField.getText();
                String pinInput = pinField.getText();                
                UserAccountSchema schema = new UserAccountSchema();                
                if (schema.isValid("email", emailInput)) {
                        String username = emailInput.split("@")[0];
                        userClient.login(username);                        
                        String path = "src/main/java/minigames/client/useraccount/" + username + ".json";
                        File file = new File(path);
                        if(file.exists()){
                                Future<String> userFromServer = userClient.userNameGet();
                                userFromServer.onSuccess(userFromServerResult -> {
                                        FileHandler user = new FileHandler(userFromServerResult);
                                        System.out.println("Server user is: " + userFromServerResult);
                                        user.addSession(file, path, userFromServerResult); 
                                        user.addGame("gameName", "score or any random value");
                                    }).onFailure(error -> {
                                        System.err.println("An error occurred: " + error.getMessage());
                                    });                                                                  
                        }else{
                                FileHandler user = new FileHandler(username);
                                user.generateUser(emailInput, pinInput);                                
                        }                        
                        this.dispose();
                } else { System.out.println("Please enter a valid email address."); }
        }   
}

