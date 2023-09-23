package minigames.client.snake;

import javax.swing.*;
import javax.swing.text.*;

/**
 * The InformationPanel class represents a panel that displays information with a title,
 * message, and a return button. It is typically used to show information to the user.
 */
public class InformationPanel extends JPanel {
    private final MainMenuPanel.PanelSwitcher panelSwitcher;
    private final JLabel backgroundLabel;
    private final String title;
    private final String[] message;

    /**
     * Constructs an InformationPanel with the provided parameters.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param title The title to be displayed at the top of the panel.
     * @param message An array of strings representing the message content.
     */
    public InformationPanel(MainMenuPanel.PanelSwitcher panelSwitcher, String title, String[] message) {
        this.panelSwitcher = panelSwitcher;
        this.title = title;
        this.message = message;

        setLayout(null);
        backgroundLabel = new BackgroundContainer();
        add(backgroundLabel);

        setupTitleLabel();
        setupMessage();
        setupReturnButton();
    }

    /**
     * Sets up the title label with the specified font and color.
     */
    private void setupTitleLabel() {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(GameConstants.INFO_TITLE_FONT);
        titleLabel.setForeground(GameConstants.INFO_TITLE_COLOR);
        int titleWidth = titleLabel.getPreferredSize().width;
        int titleX = (backgroundLabel.getWidth() - titleWidth) / 2;
        int titleY = GameConstants.INFO_TITLE_Y;
        titleLabel.setBounds(titleX, titleY, titleWidth, titleLabel.getPreferredSize().height);
        backgroundLabel.add(titleLabel);
    }

    /**
     * Sets up the message text pane with the specified font, color, and alignment.
     */
    private void setupMessage() {
        JTextPane messagePane = new JTextPane();
        messagePane.setText(String.join("\n", message));
        messagePane.setOpaque(false);
        messagePane.setEditable(false);
        messagePane.setFocusable(false);
        messagePane.setFont(GameConstants.INFO_MESSAGE_FONT);
        messagePane.setForeground(GameConstants.INFO_MESSAGE_COLOR);

        // Center-align the text
        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        int textWidth = GameConstants.INFO_TEXT_WIDTH;
        int textHeight = GameConstants.INFO_TEXT_HEIGHT;
        int availableWidth = MultimediaManager.getPhoneBackground().getImageResourceWidth();
        int horizontalPosition = (availableWidth - textWidth) / 2;
        int verticalPosition = GameConstants.INFO_TEXT_Y;

        messagePane.setBounds(horizontalPosition, verticalPosition, textWidth, textHeight);
        backgroundLabel.add(messagePane);
    }

    /**
     * Sets up the return button using the UIHelper class for consistency.
     */
    private void setupReturnButton() {
        backgroundLabel.add(ButtonFactory.setupReturnButton(panelSwitcher, backgroundLabel));
    }
}
