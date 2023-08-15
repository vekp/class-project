package minigames.client.notifications;

import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class NotificationManager implements Tickable {
    private final JFrame frame;
    private final JLayeredPane layeredPane;
    private final Animator animator;
    private final List<Component> queuedNotifications;
    JPanel notificationPanel;
    final int topMargin = 5, leftMargin = 5, rightMargin = 5;
    int width, height;
    int currentX, currentY;
    int movementSpeed = 4; // pixels to move per 16ms frame
    int displayTime = 3000; // in milliseconds
    long startTime;

    enum Status {
        SHOW, HIDE, DISPLAY, IDLE
    }
    private Status status = Status.IDLE;

    public NotificationManager(MinigameNetworkClient mnClient, JFrame frame) {
        this.frame = frame;
        this.layeredPane = frame.getLayeredPane();
        this.animator = mnClient.getAnimator();
        this.queuedNotifications = new LinkedList<>();
    }

    public void showNotification(Component component) {
        // If something already in progress, add it to the queue and stop
        if (!status.equals(Status.IDLE)) {
            queuedNotifications.add(component);
            return;
        }

        status = Status.SHOW;
        // do stuff to show notification
        notificationPanel = new JPanel();
        notificationPanel.add(component);
        notificationPanel.setBorder(BorderFactory.createEtchedBorder());
        // make dismissible
        notificationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                status = Status.HIDE;
            }
        });
        // get panel dimensions
        width = (int) notificationPanel.getPreferredSize().getWidth();
        height = (int) notificationPanel.getPreferredSize().getHeight();
        // calculate start position
        currentX = (frame.getWidth() - width) / 2;
        currentY = -height;
        // set bounds, add to layer
        notificationPanel.setBounds(currentX, currentY, width, height);
        layeredPane.add(notificationPanel, JLayeredPane.POPUP_LAYER);
        // start animating
        animator.requestTick(this);
    }

    // Animate the notification panel depending on its current status
    @Override
    public void tick(Animator al, long now, long delta) {
        switch (status) {
            case IDLE -> {
                // Display next item in the queue if exists
                if (!queuedNotifications.isEmpty()) {
                    showNotification(queuedNotifications.remove(0));
                }
                // No more animation required, end here
                return;
            }
            // Move down from the top
            case SHOW -> {
                currentY = Math.min(topMargin, currentY + movementSpeed);
                notificationPanel.setLocation(currentX, currentY);
                if (currentY == topMargin) {
                    startTime = now;
                    status = Status.DISPLAY;
                }
            }
            // Wait for display time to elapse
            case DISPLAY -> {
                if ((now - startTime) / 1000000 >= displayTime) status = Status.HIDE;
            }
            // Move back up until out of view
            case HIDE -> {
                currentY = (currentY - movementSpeed);
                notificationPanel.setLocation(currentX, currentY);
                if (currentY <= -height) {
                    status = Status.IDLE;
                    layeredPane.remove(notificationPanel);
                }
            }
        }
        al.requestTick(this);
    }
}
