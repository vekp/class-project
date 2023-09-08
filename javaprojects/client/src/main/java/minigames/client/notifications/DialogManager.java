package minigames.client.notifications;

import minigames.client.MinigameNetworkClient;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;

public class DialogManager extends NotificationManager{

    public DialogManager(MinigameNetworkClient mnClient) {
        super(mnClient);
    }

    /**
     * Reset settings to their default values. Default values are: <br>
     * alignmentX       :   Component.CENTER_ALIGNMENT (0.5f) <br>
     * alignmentY       :   Component.CENTER_ALIGNMENT (0.5f) <br>
     * animationSpeed   :   0.2f <br>
     * displayTime      :   0 (indefinite) <br>
     * margins          :   Insets(5, 5, 5, 5) <br>
     * colours          :   system default colours <br>
     * font             :   system default font <br>
     * border           :   EtchedBorder()
     */
    @Override
    public NotificationManager resetToDefaultSettings() {
        return super.resetToDefaultSettings()
                .setAlignmentX(Component.CENTER_ALIGNMENT)
                .setAlignmentY(Component.CENTER_ALIGNMENT)
                .setDisplayTime(0);
    }

    /**
     * Clears any currently displayed or queued notifications before displaying a new notification.
     */
    @Override
    public NotificationManager showNotification(Component component, boolean isDismissible) {
        clearNotificationQueue();
        dismissCurrentNotification();
        return super.showNotification(component, isDismissible);
    }

    /**
     * Display a message dialog with the given title and Component. Includes an OK button for dismissal.
     */
    public DialogManager showMessageDialog(String title, JComponent component, boolean okButtonRequired, ActionListener okButtonActionListener) {
  // Make internal frame with OK button
        JInternalFrame dialog = new JInternalFrame(title, false, true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(component, gbc);
        // OK button to dismiss the panel and restore previous settings
        JButton okButton = null;
        if (okButtonRequired) {
            okButton = new JButton("OK");
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(0, 20, 10, 20);
            dialog.add(okButton, gbc);
            okButton.addActionListener(e -> dismissCurrentNotification());
            if (okButtonActionListener != null) okButton.addActionListener(okButtonActionListener);
        }
        // Set close operation
        dialog.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        dialog.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                dismissCurrentNotification();
            }
        });
        // Disable repositioning
        for (MouseMotionListener mml : dialog.getMouseMotionListeners()) dialog.removeMouseMotionListener(mml);
        // Show the panel
        dialog.setVisible(true);
        showNotification(dialog, false);
        if (okButtonRequired) okButton.requestFocusInWindow();
        return this;
    }

    /**
     * Call showMessageDialog using given title and component, and default value of true for okButtonRequired.
     */
    public DialogManager showMessageDialog(String title, JComponent component) {
        return showMessageDialog(title, component, true);
    }

    public DialogManager showMessageDialog(String title, JComponent component, boolean okButtonRequired) {
        return showMessageDialog(title, component, okButtonRequired, null);
    }

}
