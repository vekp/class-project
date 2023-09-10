package minigames.client;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import minigames.client.achievementui.AchievementUI;
import minigames.client.backgrounds.Starfield;
import minigames.rendering.GameMetadata;
import minigames.rendering.GameServerDetails;

import minigames.client.krumgame.KrumMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * The main window that appears.
 * 
 * For simplicity, we give it a BorderLayout with panels for north, south, east, west, and center.
 * 
 * This makes it simpler for games to load up the UI however they wish, though the default expectation
 * is that the centre just has an 800x600 canvas.
 */
public class MinigameNetworkClientWindow {

    MinigameNetworkClient networkClient;

    JFrame frame;

    JPanel parent;
    JPanel north;
    JPanel center;
    JPanel south;
    JPanel west;
    JPanel east;    

    JLabel messageLabel;

    // We hang on to this one for registering in servers
    JTextField nameField;

    JTextField krumNameField;

    public MinigameNetworkClientWindow(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        parent = new JPanel(new BorderLayout());        

        north = new JPanel();
        parent.add(north, BorderLayout.NORTH);
        center = new JPanel();
        center.setPreferredSize(new Dimension(800, 600));
        parent.add(center, BorderLayout.CENTER);
        south = new JPanel();
        parent.add(south, BorderLayout.SOUTH);
        east = new JPanel();
        parent.add(east, BorderLayout.EAST);
        west = new JPanel();
        parent.add(west, BorderLayout.WEST);

        frame.add(parent);

        nameField = new JTextField(20);
        nameField.setText("Algernon");
        krumNameField = new JTextField(20);
        krumNameField.setText("Algernon");
    }

    /** Removes all components from the south panel */
    public void clearSouth() {
        south.removeAll();
    }

    /** Clears all sections of the UI  */
    public void clearAll() {
        for (JPanel p : new JPanel[] { north, south, east, west, center }) {
            p.removeAll();
        }
    }

    /** Adds a component to the north part of the main window */
    public void addNorth(java.awt.Component c) {
        north.add(c);
    }

    /** Adds a component to the south part of the main window */
    public void addSouth(java.awt.Component c) {
        south.add(c);
    }

    /** Adds a component to the east part of the main window */
    public void addEast(java.awt.Component c) {
        east.add(c);
    }

    /** Adds a component to the west part of the main window */
    public void addWest(java.awt.Component c) {
        west.add(c);
    }

    /** Adds a component to the center of the main window */
    public void addCenter(java.awt.Component c) {
        center.add(c);
    }

    /** "Packs" the frame, setting its size to match the preferred layout sizes of its component */
    public void pack() {
        frame.pack();
        parent.repaint();
    }

    /** Makes the main window visible */
    public void show() {
        pack();
        frame.setVisible(true);
    }

    /**
     * Shows a simple message layered over a retro-looking starfield.
     * Terrible placeholder art.
     */
    public void showStarfieldMessage(String s) {
        clearAll();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(new Starfield(networkClient.animator), JLayeredPane.DEFAULT_LAYER);
        layeredPane.setBackground(new Color(0,0,0,0));
        layeredPane.setPreferredSize(new Dimension(800, 600));

        JLabel label = new JLabel(s);
        label.setOpaque(true);
        label.setForeground(Color.CYAN);
        label.setBackground(Color.BLACK);
        label.setFont(new Font("Monospaced", Font.PLAIN, 36));
        Dimension labelSize = label.getPreferredSize();
        label.setSize(labelSize);
        label.setLocation((int)(400 - labelSize.getWidth() / 2), (int)(300 - labelSize.getHeight() / 2));
        layeredPane.add(label, JLayeredPane.MODAL_LAYER);

        center.add(layeredPane);
        pack();
    }

    /**
     * Shows a list of GameServers to pick from
     * 
     * TODO: Prettify!
     * @param servers
     */
    public void showGameServers(List<GameServerDetails> servers) {
        frame.setTitle("COSC220 2023 Minigame Collection");
        clearAll();

        JPanel panel = new JPanel();
        List<JPanel> serverPanels = servers.stream().map((gsd) -> {
            JPanel p = new JPanel();
            JLabel l = new JLabel(String.format("<html><h1>%s</h1><p>%s</p></html>", gsd.name(), gsd.description()));
            JButton newG = new JButton("Open games");

            newG.addActionListener((evt) -> {
                networkClient.getGameMetadata(gsd.name())
                  .onSuccess((list) -> showGames(gsd.name(), list));
            });

            p.add(l);
            p.add(newG);
            return p;
        }).toList();

        for (JPanel serverPanel : serverPanels) {
            panel.add(serverPanel);
        }

        center.add(panel);

        // Create a button for the Achievement UI.
        JButton achievementsButton = new JButton("Achievements");
        // Create action listener to use as back button action.
        ActionListener returnAction = (a) -> {
            showGameServers(servers);
        };        achievementsButton.addActionListener(e -> {
            clearAll();
            JPanel achievements = new AchievementUI(returnAction);
            frame.setTitle(AchievementUI.TITLE);
            center.add(achievements);
            pack();
        });
        south.add(achievementsButton);
        pack();
    }

    /**
     * Shows a list of games to pick from
     * 
     * TODO: Prettify!
     * @param servers
     */
    public void showGames(String gameServer, List<GameMetadata> inProgress) {
        if (gameServer.equals("KrumGame")) {
            showKrumTitle(inProgress);
            return;
        } 
        
        clearAll();

        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Your name");
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        north.add(namePanel);


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<JPanel> gamePanels = inProgress.stream().map((g) -> {
            JPanel p = new JPanel();
            JLabel l = new JLabel(String.format("<html><h1>%s</h1><p>%s</p></html>", g.name(), String.join(",", g.players())));
            JButton join = new JButton("Join game");
            join.addActionListener((evt) -> {
                networkClient.joinGame(gameServer, g.name(), nameField.getText());
            });
            join.setEnabled(g.joinable());
            p.add(l);
            p.add(join);
            return p;
        }).toList();

        for (JPanel gamePanel : gamePanels) {
            panel.add(gamePanel);
        }

        JButton newG = new JButton("New game");
        newG.addActionListener((evt) -> {
            // FIXME: We've got a hardcoded player name here
            networkClient.newGame(gameServer, nameField.getText());
        });
        panel.add(newG);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        center.add(scrollPane);
        pack();
        parent.repaint();
    }
    public JFrame getFrame() {
        return this.frame;
    }

    public void showKrumAchievements(List<GameMetadata> games) {
        clearAll();
        KrumMenu menu = new KrumMenu();
        JLabel screen = new JLabel(new ImageIcon(menu.getScreen("achievements")));
        JButton instructionsButton = new JButton("");
        instructionsButton.setLayout(null);
        instructionsButton.setOpaque(false);
        instructionsButton.setContentAreaFilled(false);
        instructionsButton.setBorderPainted(false);
        instructionsButton.setBounds(50, 60, 142, 42);
        instructionsButton.setFocusable(false);
        instructionsButton.addActionListener((evt) -> {
            showKrumInstructions(games);
            return;
        });
        screen.add(instructionsButton);
        JButton newGameButton = new JButton();
        newGameButton.setLayout(null);
        newGameButton.setOpaque(false);
        newGameButton.setContentAreaFilled(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setBounds(485, 414, 277, 108);
        newGameButton.setFocusable(false);
        newGameButton.addActionListener((evt) -> {
            networkClient.newGame("KrumGame", krumNameField.getText());
            return;
        });
        screen.add(newGameButton);
        center.add(screen);
        pack();
        parent.repaint();
        int numListed = 0;
        int x = 525;
        int y = 175;
        int yInc = 75;
        for (GameMetadata gm : games) {
            if (gm.joinable() && gm.players().length > 0) {
                JButton jb = new JButton(gm.players()[0]);
                jb.setFont(new Font("Courier New", Font.PLAIN, 24));
                jb.setForeground(Color.RED);
                jb.setOpaque(false);
                jb.setContentAreaFilled(false);
                jb.setBorderPainted(false);
                jb.setBounds(x, y, 200, 60);
                jb.addActionListener((evt) -> {
                    networkClient.joinGame("KrumGame", gm.name(), krumNameField.getText());
                    return;
                });
                screen.add(jb);
                y += yInc;
                numListed++;
            }
            if (numListed >= 3) break;
        }
        screen.add(krumNameField);

        center.add(screen);
        pack();
        parent.repaint();
    }

    public void showKrumInstructions(List<GameMetadata> games) {
        clearAll();
        KrumMenu menu = new KrumMenu();
        JLabel screen = new JLabel(new ImageIcon(menu.getScreen("instructions")));
        screen.setLayout(null);
        JButton achievementsButton = new JButton("");
        achievementsButton.setLayout(null);
        achievementsButton.setOpaque(false);
        achievementsButton.setContentAreaFilled(false);
        achievementsButton.setBorderPainted(false);
        achievementsButton.setBounds(200, 59, 200, 43);
        achievementsButton.setFocusable(false);
        achievementsButton.addActionListener((evt) -> {
            showKrumAchievements(games);
            return;
        });
        screen.add(achievementsButton);
        JButton newGameButton = new JButton();
        newGameButton.setLayout(null);
        newGameButton.setOpaque(false);
        newGameButton.setContentAreaFilled(false);
        newGameButton.setBorderPainted(false);
        newGameButton.setBounds(485, 414, 277, 108);
        newGameButton.setFocusable(false);
        newGameButton.addActionListener((evt) -> {
            networkClient.newGame("KrumGame", krumNameField.getText());
            return;
        });
        screen.add(newGameButton);     
        center.add(screen);
        pack();
        parent.repaint();
        int numListed = 0;
        int x = 525;
        int y = 175;
        int yInc = 75;
        for (GameMetadata gm : games) {
            if (gm.joinable() && gm.players().length > 0) {
                JButton jb = new JButton(gm.players()[0]);
                jb.setFont(new Font("Courier New", Font.PLAIN, 24));
                jb.setForeground(Color.RED);
                jb.setOpaque(false);
                jb.setContentAreaFilled(false);
                jb.setBorderPainted(false);
                jb.setBounds(x, y, 200, 60);
                jb.addActionListener((evt) -> {
                    networkClient.joinGame("KrumGame", gm.name(), krumNameField.getText());
                    return;
                });
                screen.add(jb);
                y += yInc;
                numListed++;
            }
            if (numListed >= 3) break;
        }
        screen.add(krumNameField);

        center.add(screen);
        pack();
        parent.repaint();
    }

    public void showKrumTitle(List<GameMetadata> games) {
        clearAll();
        krumNameField.setLayout(null);
        krumNameField.setBounds(373, 10, 54, 30);
        KrumMenu menu = new KrumMenu();
        JLabel screen = new JLabel(new ImageIcon(menu.getScreen("title")));
        JButton optionsButton = new JButton("");
        optionsButton.setLayout(null);
        optionsButton.setOpaque(false);
        optionsButton.setContentAreaFilled(false);
        optionsButton.setBorderPainted(false);
        optionsButton.setBounds(400, 416, 293, 123);
        optionsButton.setFocusable(false);
        optionsButton.addActionListener((evt) -> {
            System.out.println("instructions button pressed");
            showKrumInstructions(games);
            return;
        });
        screen.add(optionsButton);
        JButton quickPlayButton = new JButton();
        quickPlayButton.setLayout(null);
        quickPlayButton.setOpaque(false);
        quickPlayButton.setContentAreaFilled(false);
        quickPlayButton.setBorderPainted(false);
        quickPlayButton.setBounds(100, 416, 293, 123);
        quickPlayButton.setFocusable(false);
        quickPlayButton.addActionListener((evt) -> {
            // join oldest joinable game
            String gameToJoin = null;
            for (GameMetadata gm : games) {
                if (gm.joinable() && gm.players().length > 0) {
                    gameToJoin = gm.name();
                }
            }
            if (gameToJoin != null) {
                networkClient.joinGame("KrumGame", gameToJoin, krumNameField.getText());
            }
            else {
                networkClient.newGame("KrumGame", krumNameField.getText());
            }            
            return;
        });
        screen.add(quickPlayButton);    
        screen.add(krumNameField);
        
        


        center.add(screen);
        pack();
        parent.repaint();
    }
}
