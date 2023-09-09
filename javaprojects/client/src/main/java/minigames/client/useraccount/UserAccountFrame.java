package minigames.client.useraccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import minigames.client.useraccount.User.*;
import minigames.client.useraccount.GenerateUser.*;
import minigames.client.useraccount.GenerateJSON.*;



public class UserAccountFrame extends JFrame implements ActionListener {

        private JTextField emailField;
        private JPasswordField pinField;
        private JButton enterButton;
        private JButton skipButton;
        
        public void UserAccount(){
                //set up the frame
                setTitle("Login");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(300,150);
                setResizable(false);
                setLocationRelativeTo(null);

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
                skipButton.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e){
                String emailInput = emailField.getText();
                GenerateJSON json = new GenerateJSON();
                if (emailInput.contains("@myune.edu.au")) {
                        System.out.println("Welcome, " + emailInput + ", enjoy your session!"); 
                        json.addUser(emailInput);
                        GenerateUser user = new GenerateUser();
                        user.generateUsers(json.getJSONArray());
                        this.dispose();
                } else {
                        System.out.println("Please enter a valid email address.");
                }
        }     
}

