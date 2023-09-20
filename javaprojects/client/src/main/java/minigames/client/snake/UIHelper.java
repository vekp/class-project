package minigames.client.snake;

import javax.swing.*;
import java.awt.event.ActionListener;

public class UIHelper {

    /**
     * Sets up a "Return to Main Menu" button with default position (centered) and name.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container The container (like a JPanel) where the button will be added.
     * @return JButton The created button.
     */
    public static JButton setupReturnButton(MainMenuPanel.PanelSwitcher panelSwitcher, JComponent container) {
        int buttonX = (MultimediaManager.getPhoneBackground().getImageResourceWidth() - GameConstants.BUTTON_WIDTH) / 2;
        return setupReturnButton(panelSwitcher, container, buttonX, GameConstants.RETURN_BUTTON_Y, GameConstants.MAIN_MENU_PANEL);
    }

    /**
     * Sets up a button with custom position and name.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container The container (like a JPanel) where the button will be added.
     * @param x The x-coordinate of the button.
     * @param y The y-coordinate of the button.
     * @param buttonName The custom name for the button.
     * @return JButton The created button.
     */
    public static JButton setupReturnButton(MainMenuPanel.PanelSwitcher panelSwitcher, JComponent container, int x, int y, String buttonName) {
        ActionListener action = e -> panelSwitcher.switchToPanel(GameConstants.MAIN_MENU_PANEL);
        JButton returnButton = ButtonFactory.createButton(buttonName, x, y, GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT, action);
        container.add(returnButton);
        return returnButton;
    }
    /**
     * Sets up a button with custom position, name, and background sound.
     *
     * @param panelSwitcher The interface used to switch between panels.
     * @param container The container (like a JPanel) where the button will be added.
     * @param x The x-coordinate of the button.
     * @param y The y-coordinate of the button.
     * @param buttonName The custom name for the button.
     * @param soundName The name of the background sound to play when the button is clicked.
     * @return JButton The created button.
     */
    public static JButton setupReturnButton(MainMenuPanel.PanelSwitcher panelSwitcher, JComponent container, int x, int y, String buttonName, String soundName) {
        ActionListener action = e -> {
            panelSwitcher.switchToPanel(GameConstants.MAIN_MENU_PANEL);
            MultimediaManager.playBackgroundSound(soundName);
        };

        JButton returnButton = ButtonFactory.createButton(buttonName, x, y, GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT, action);
        container.add(returnButton);
        return returnButton;
    }


}
