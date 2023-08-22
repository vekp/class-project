package minigames.client.notifications;

import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for managing popup notifications to display to players without disrupting gameplay.
 */
public class NotificationManager implements Tickable {
    private final JFrame frame;
    private final JLayeredPane layeredPane;
    private final Animator animator;
    private final List<Component> queuedNotifications;
    JPanel notificationPanel;
    // Margins to leave between frame edge and notification
    private int topMargin, leftMargin, rightMargin;
    // Size of notification panel
    private float alignment;
    private int height;
    // Panel's position
    private int currentX, currentY;
    // Speed in pixels to move per 16ms frame
    private int animationSpeed;
    // Duration to wait in fully displayed state, in milliseconds
    private int displayTime;
    // Timer starts when notification is fully displayed
    private long startTime;
    boolean applyColourAndFontStyling;
    private Color backgroundColor;
    private Color foregroundColor;
    private String fontName;
    private Border border;

    /**
     * Status enum defines the current state of the notification panel
     */
    enum Status {
        IDLE, MOVING_DOWN, FULLY_DISPLAYED, MOVING_UP
    }
    private Status status = Status.IDLE;

    /**
     * Constructor for NotificationManager
     * @param mnClient the currently running MinigameNetworkClient
     */

    public NotificationManager(MinigameNetworkClient mnClient) {
        this.frame = mnClient.getMainWindow().getFrame();
        this.layeredPane = frame.getLayeredPane();
        this.animator = mnClient.getAnimator();
        this.queuedNotifications = new LinkedList<>();
        resetToDefaultSettings();
    }

    /**
     * Display a dismissible popup notification on top of the frame's other contents that slides down from the top
     * of the frame, pauses, and slides back up off the frame.
     * @param component the Component to display
     */
    public void showNotification (Component component) {
        showNotification(component, true);
    }

    /**
     * Display a popup notification on top of the frame's other contents that slides down from the top of the frame,
     * pauses, and slides back up off the frame.
     * @param component     the Component to display in the notification
     * @param isDismissible if the user should be able to click on it to dismiss it.
     */
    public void showNotification(Component component, boolean isDismissible) {
        // If something already in progress, add it to the queue and stop
        if (!status.equals(Status.IDLE)) {
            queuedNotifications.add(component);
            return;
        }
        status = Status.MOVING_DOWN;
        // create a panel to contain component
        notificationPanel = new JPanel();
        notificationPanel.add(component);
        // apply the set border if component does not already have one
        if (component instanceof JComponent jc && jc.getBorder() == null) {
            notificationPanel.setBorder(border);
        }
        // apply custom styling of colours and font
        applyStyling(notificationPanel);
        // make dismissible
        if (isDismissible) {
            notificationPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    status = Status.MOVING_UP;
                }
            });
        }
        // get panel dimensions
        int width = (int) notificationPanel.getPreferredSize().getWidth();
        height = (int) notificationPanel.getPreferredSize().getHeight();
        // calculate start position
        int maxX = frame.getWidth() - width - rightMargin;
        int minX = leftMargin;
        currentX = minX + (int) (alignment * (maxX - minX));
        currentY = - height;
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
            case MOVING_DOWN -> {
                currentY = Math.min(topMargin, currentY + animationSpeed);
                notificationPanel.setLocation(currentX, currentY);
                if (currentY == topMargin) {
                    startTime = now;
                    status = Status.FULLY_DISPLAYED;
                }
            }
            // Wait for display time to elapse
            case FULLY_DISPLAYED -> {
                if (displayTime > 0 && (now - startTime) / 1000000 >= displayTime) {
                    status = Status.MOVING_UP;
                }
            }
            // Move back up until out of view
            case MOVING_UP -> {
                currentY = (currentY - animationSpeed);
                notificationPanel.setLocation(currentX, currentY);
                if (currentY <= -height) {
                    status = Status.IDLE;
                    layeredPane.remove(notificationPanel);
                }
            }
        }
        al.requestTick(this);
    }

    /**
     * Setter for changing the horizontal alignment of the notification.
     * This can be used if default value of Component.CENTER_ALIGNMENT (0.5f) could cause notifications to obstruct
     * important gameplay UI elements. Use together with setMargins for precise control of positioning.
     * @param alignment a float representing desired horizontal alignment. Use Component alignment constants.
     */
    public void setAlignment(float alignment) {
        if (alignment < 0) this.alignment = 0f;
        else if (alignment > 1) this.alignment = 1.0f;
        else this.alignment = alignment;
    }

    /**
     * Setter for changing the speed at which the notification slides up and down.
     * @param animationSpeed speed in pixels per 16ms tick.
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * Setter for changing duration of time the notification will remain in its fully displayed state, before
     * it starts moving back up. If displayTime <= 0, the notification will stay on screen indefinitely until
     * manually dismissed by clicking on it.
     * @param displayTime time in ms
     */
    public void setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
    }

    /**
     * Setter for altering the margins between edge of the frame and the notification panel at its fully displayed
     * state. Can be used together with alignment setter for precise positioning.
     * @param topMargin   distance from top of frame
     * @param leftMargin  distance from left of frame
     * @param rightMargin distance from right of frame
     */
    public void setMargins(int topMargin, int leftMargin, int rightMargin) {
        this.topMargin = topMargin;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    /**
     * Dismiss the currently displayed notification if there is one.
     */
    public void dismissCurrentNotification() {
        if (!status.equals(Status.IDLE)) status = Status.MOVING_UP;
    }

    /**
     * Clear all notifications in the queue. Does not affect current notification.
     */
    public void clearNotificationQueue() {
        queuedNotifications.clear();
    }

    /**
     * Reset settings to their default values. Default values are: <br>
     * alignment        :   Component.RIGHT_ALIGNMENT (1.0f) <br>
     * animationSpeed   :   4 <br>
     * displayTime      :   5000 <br>
     * margins          :   5 <br>
     * colours          :   system default colours <br>
     * font             :   system default font <br>
     * border           :   EtchedBorder
     */
    public void resetToDefaultSettings() {
        setAlignment(Component.RIGHT_ALIGNMENT);
        setAnimationSpeed(4);
        setDisplayTime(5000);
        setMargins(5, 5, 5);
        JPanel defaultPanel = new JPanel();
        setColours(defaultPanel.getForeground(), defaultPanel.getBackground());
        setFont(defaultPanel.getFont().getFontName());
        setBorder(BorderFactory.createEtchedBorder());
        setApplyColourAndFontStyling(false);
    }

    public void setApplyColourAndFontStyling(boolean applyColourAndFontStyling) {
        this.applyColourAndFontStyling = applyColourAndFontStyling;
    }

    /**
     * Set the colours to be applied to notifications
     * @param foregroundColour
     * @param backgroundColour
     */
    public void setColours(Color foregroundColour, Color backgroundColour) {
        this.foregroundColor = foregroundColour;
        this.backgroundColor = backgroundColour;
        setApplyColourAndFontStyling(true);
    }

    /**
     * Set font for notifications
     */
    public void setFont(String fontname) {
        this.fontName = fontname;
        setApplyColourAndFontStyling(true);
    }


    /**
     * Set styling for foreground/background colours, font and border in one method.
     * @param foregroundColour
     * @param backgroundColour
     * @param fontName
     * @param border
     */
    public void setStyling(Color foregroundColour, Color backgroundColour, String fontName, Border border) {
        setColours(foregroundColour, backgroundColour);
        setFont(fontName);
        setBorder(border);
    }

    /**
     * Apply font, foreground and background colours to a Component.
     * If Container, recurse through its children.
     * @param component
     */
    private void applyStyling(Component component) {
        // Set colours
        component.setForeground(foregroundColor);
        component.setBackground(backgroundColor);
        if (component instanceof JComponent c) {
            c.setOpaque(true);
        }
        // Set font
        Font f = component.getFont();
        component.setFont(new Font(fontName, f.getStyle(), f.getSize()));
        // Recurse
        if (component instanceof Container container) {
            for (Component c : container.getComponents()) {
                applyStyling(c);
            }
        }
    }

    /**
     * Set the border to be applied to notifications
     * @param border
     */
    public void setBorder(Border border) {
        this.border = border;
    }
}