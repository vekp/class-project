package useraccount;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class UserAccountFrame extends JFrame implements ActionListener {

        private JTextField emailField;
        //private JPasswordField passwordField = new JPasswordField();
        private JButton enterButton;
        private JButton skipButton;
        
        public void UserAccount(){
                //set up the frame
                setTitle("Login");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(300,150);
                setLocationRelativeTo(null);

                //initialise components required for the window
                JLabel emailLabel = new JLabel("Email");
                //JLabel passwordLabel = new JLabel("Password");
                emailField = new JTextField(20);
                enterButton = new JButton("Enter"); // labelling as enter as there's no "password" required so not really a log in.
                skipButton = new JButton("Skip"); // allows the user to skip without logging in.

                // initialise panel that contains all componenets
                JPanel panel = new JPanel();
                panel.add(emailLabel);
                panel.add(emailField);
                panel.add(new JLabel()); // spaces out elements
                panel.add(skipButton);
                panel.add(enterButton);

                // add panel to the frame.
                add(panel);

                enterButton.addActionListener(this);
                skipButton.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e){
                String emailInput = emailField.getText();
                // FileGenerator user = new FileGenerator("emailInput");
                        // user.generateFile();

                if (emailInput.contains("@myune.edu.au")) {
                        System.out.println("Welcome, " + emailInput + ", enjoy your session!");                        
                        this.dispose();
                } else {
                        System.out.println("Please enter a valid email address.");
                }
        }     
}

