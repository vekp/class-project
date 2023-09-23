package minigames.client.snake;

import javax.swing.*;

/**
 * MainMenuPanel represents the main menu of the Snake game.
 * It provides buttons for starting the game, accessing help, about, achievements, and exiting
 * the game.
 */
public class MainMenuPanel extends JPanel {
    private final Runnable closeGameAction;
    private final PanelSwitcher panelSwitcher;
    private final JLabel backgroundLabel;

    /**
     * Interface for switching between panels in the main menu.
     */
    public interface PanelSwitcher {
        void switchToPanel(String panelName);
    }

    /**
     * Constructs a MainMenuPanel.
     *
     * @param closeGameAction The action to be performed when the game is closed.
     * @param panelSwitcher   The panel switcher for transitioning between menu panels.
     */
    public MainMenuPanel(Runnable closeGameAction, PanelSwitcher panelSwitcher) {
        this.closeGameAction = closeGameAction;
        this.panelSwitcher = panelSwitcher;

        setLayout(null);
        backgroundLabel = createBackgroundLabel();
        setupButtons();
    }

    /**
     * Creates and configures the background label for the main menu.
     *
     * @return The background label.
     */
    private JLabel createBackgroundLabel() {
        ImageIcon backgroundIcon = MultimediaManager.getPhoneBackground().getImageResource();
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(
                0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        backgroundLabel.setLayout(null);

        ImageIcon logoIcon = MultimediaManager.getSnakeLogoResource().getImageResource();
        int logoWidth = logoIcon.getIconWidth();
        int logoHeight = logoIcon.getIconHeight();

        int logoX = (backgroundLabel.getWidth() - logoWidth) / 2;
        int logoY = GameConstants.LOGO_Y;

        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(logoX, logoY, logoWidth, logoHeight);
        backgroundLabel.add(logoLabel);

        add(backgroundLabel);

        return backgroundLabel;
    }

    /**
     * Sets up and adds buttons to the main menu panel.
     */
    private void setupButtons() {
        int buttonX = (MultimediaManager.getPhoneBackground()
                                        .getImageResourceWidth() - GameConstants.BUTTON_WIDTH) / 2;
        backgroundLabel.add(ButtonFactory.createButton(GameConstants.PLAY_PANEL, buttonX,
                                                       GameConstants.START_BUTTON_Y,
                                                       GameConstants.BUTTON_WIDTH,
                                                       GameConstants.BUTTON_HEIGHT,
                                                       e -> panelSwitcher.switchToPanel(
                                                               GameConstants.PLAY_PANEL)
                                                      ));
        backgroundLabel.add(ButtonFactory.createButton(GameConstants.HELP_MENU_PANEL, buttonX,
                                                       GameConstants.START_BUTTON_Y + GameConstants.BUTTON_HEIGHT + GameConstants.BUTTON_GAP,
                                                       GameConstants.BUTTON_WIDTH,
                                                       GameConstants.BUTTON_HEIGHT,
                                                       e -> panelSwitcher.switchToPanel(
                                                               GameConstants.HELP_MENU_PANEL)
                                                      ));
        backgroundLabel.add(ButtonFactory.createButton(GameConstants.ABOUT_ME_PANEL, buttonX,
                                                       GameConstants.START_BUTTON_Y + 2 * GameConstants.BUTTON_HEIGHT + GameConstants.BUTTON_GAP * 2,
                                                       GameConstants.BUTTON_WIDTH,
                                                       GameConstants.BUTTON_HEIGHT,
                                                       e -> panelSwitcher.switchToPanel(
                                                               GameConstants.ABOUT_ME_PANEL)
                                                      ));
        backgroundLabel.add(ButtonFactory.createButton(GameConstants.ACHIEVEMENTS_PANEL, buttonX,
                                                       GameConstants.START_BUTTON_Y + 3 * GameConstants.BUTTON_HEIGHT + GameConstants.BUTTON_GAP * 3,
                                                       GameConstants.BUTTON_WIDTH,
                                                       GameConstants.BUTTON_HEIGHT,
                                                       e -> panelSwitcher.switchToPanel(
                                                               GameConstants.ACHIEVEMENTS_PANEL)
                                                      ));
        backgroundLabel.add(ButtonFactory.createButton(GameConstants.EXIT_GAME, buttonX,
                                                       GameConstants.START_BUTTON_Y + 4 * GameConstants.BUTTON_HEIGHT + GameConstants.BUTTON_GAP * 4,
                                                       GameConstants.BUTTON_WIDTH,
                                                       GameConstants.BUTTON_HEIGHT,
                                                       e -> closeGameAction.run()
                                                      ));
    }

}
