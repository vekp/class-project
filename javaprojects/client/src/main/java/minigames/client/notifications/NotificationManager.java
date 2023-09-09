package minigames.client.notifications;

import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A class for managing popup notifications to display to players without disrupting gameplay.
 */
public class NotificationManager implements Tickable {
    private final JFrame frame;
    private JLayeredPane layeredPane;
    private final Animator animator;
    private final HashMap<Component, Boolean> queuedNotifications = new LinkedHashMap<>();
    Component notification;
    // Margins to leave between frame edge and notification
    private Insets margins;
    // Alignment of notification panel
    private float alignmentX, alignmentY;
    private int notificationHeight;
    // Notification panel's position
    private int currentX;
    private double currentY;
    // Notification panel's target position
    private int targetY;
    // Speed of notification animation movement
    private float animationSpeed;
    // Duration to wait in fully displayed state, in milliseconds
    private int displayTime;
    // Timer starts when notification is fully displayed
    private long startTime;
    private boolean applyColourAndFontStyling;
    private Color backgroundColour, foregroundColour, hoverColour;
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
        this.animator = mnClient.getAnimator();
        resetToDefaultSettings();
    }

    /**
     * Display a dismissible popup notification on top of the frame's other contents that slides down from the top
     * of the frame, pauses, and slides back up off the frame.
     * @param component the Component to display
     */
    public NotificationManager showNotification(Component component) {
        return showNotification(component, true);
    }

    /**
     * Display a popup notification on top of the frame's other contents that slides down from the top of the frame,
     * pauses, and slides back up off the frame.
     * @param component     the Component to display in the notification
     * @param isDismissible if the user should be able to click on it to dismiss it.
     */
    public NotificationManager showNotification(Component component, boolean isDismissible) {
        // If something already in progress, add it to the queue and stop
        if (!status.equals(Status.IDLE)) {
            queuedNotifications.put(component, isDismissible);
            return this;
        }
        status = Status.MOVING_DOWN;
        // apply the set border if component does not already have one.
        if (component instanceof JComponent jc && jc.getBorder() != null) {
            notification = component;
        } else {
            JPanel panel = new JPanel();
            panel.add(component);
            panel.setBorder(border);
            notification = panel;
        }
        // apply custom styling of colours and font
        applyStyling(notification);
        // make dismissible
        if (isDismissible) {
            notification.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    status = Status.MOVING_UP;
                }
            });
        }
        // Notification dimensions
        int notificationWidth = (int) notification.getPreferredSize().getWidth();
        notificationHeight = (int) notification.getPreferredSize().getHeight();
        // calculate start position
        int maxX = layeredPane.getWidth() - notificationWidth - margins.right;
        int minX = margins.left;
        currentX = minX + (int) (alignmentX * (maxX - minX));
        currentY = -notificationHeight;
        // calculate target Y position
        int minY = margins.top;
        int maxY = layeredPane.getHeight() - notificationHeight - margins.bottom;
        targetY = minY + (int) (alignmentY * (maxY - minY));
        // set bounds, add to layer
        notification.setBounds(currentX, (int) currentY, notificationWidth, notificationHeight);
        layeredPane.add(notification, JLayeredPane.POPUP_LAYER);
        // start animating
        animator.requestTick(this);
        return this;
    }

    // Animate the notification panel depending on its current status
    @Override
    public void tick(Animator al, long now, long delta) {
        switch (status) {
            case IDLE -> {
                // Display next item in the queue if exists
                if (!queuedNotifications.isEmpty()) {
                    Component c = queuedNotifications.keySet().iterator().next();
                    boolean b = queuedNotifications.remove(c);
                    showNotification(c, b);
                }
                // No more animation required, end here
                return;
            }
            // Move down from the top
            case MOVING_DOWN -> {
                currentY += (targetY - currentY) * animationSpeed;
                int y = (int) Math.round(currentY);
                notification.setLocation(currentX, y);
                if (y == targetY) {
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
                currentY -= (targetY - currentY) * animationSpeed * 2; // Doubled to increase dismissal responsiveness
                int y = (int) Math.round(currentY);
                notification.setLocation(currentX, y);
                if (y <= -notificationHeight) {
                    status = Status.IDLE;
                    layeredPane.remove(notification);
                    notification = null;
                }
            }
        }
        al.requestTick(this);
    }

        /**
         * Reset settings to their default values. Default values are: <br>
         * alignmentX       :   Component.RIGHT_ALIGNMENT (1.0f) <br>
         * alignmentY       :   Component.TOP_ALIGNMENT (0.0f) <br>
         * animationSpeed   :   0.2f <br>
         * displayTime      :   5000 <br>
         * margins          :   Insets(5, 5, 5, 5) <br>
         * colours          :   system default colours <br>
         * font             :   system default font <br>
         * border           :   EtchedBorder()
         */
    public NotificationManager resetToDefaultSettings() {
        setAlignmentX(Component.RIGHT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);
        setAnimationSpeed(0.2f);
        setDisplayTime(5000);
        setMargins(new Insets(5, 5, 5, 5));
        // Dummy panel for getting system default colours and font
        JPanel defaultPanel = new JPanel();
        setColours(defaultPanel.getForeground(), defaultPanel.getBackground(), null);
        setFont(defaultPanel.getFont().getFontName());
        setBorder(BorderFactory.createEtchedBorder());
        setApplyColourAndFontStyling(false);
        this.layeredPane = frame.getLayeredPane();
        if (notification != null && notification.getParent() != layeredPane) {
            layeredPane.add(notification);
            applyStyling(notification);
            applyBorder(notification);
        }
        return this;
    }

    /**
     * Set the area to contain notifications based on the given Component.
     * NOTE: This may not work as intended depending on the Layout Manager used by the Component's parent.
     */
    public NotificationManager setNotificationArea(Component notificationArea) {
        // Create new layered pane
        JLayeredPane pane = new JLayeredPane();
        // Get parent container and position of area within its parent
        Container parent = notificationArea.getParent();
        int index = parent.getComponentZOrder(notificationArea);
        // Set sizes of pane and area
        pane.setPreferredSize(notificationArea.getPreferredSize());
        notificationArea.setBounds(0, 0, notificationArea.getPreferredSize().width, notificationArea.getPreferredSize().height);
        // Put area inside pane, and pane inside parent
        pane.add(notificationArea, JLayeredPane.DEFAULT_LAYER);
        parent.add(pane, index);
        parent.revalidate();
        this.layeredPane = pane;
        if (notification != null) {
            pane.add(notification);
            applyStyling(notification);
            applyBorder(notification);
        }
        return this;
    }

    /**
     * Setter for horizontal alignment of the notification.
     * Possible alignment values are in the range 0.0f - 1.0f, such as Component.LEFT_ALIGNMENT(0.0f),
     * Component.CENTER_ALIGNMENT (0.5f), and the default Component.RIGHT_ALIGNMENT (1.0f).
     */
    public NotificationManager setAlignmentX(float alignmentX) {
        this.alignmentX = alignmentX < 0 ? 0 : alignmentX > 1 ? 1 : alignmentX;
        return this;
    }

    /**
     * Setter for vertical alignment of the notification.
     * Possible alignment values are in the range 0.0f - 1.0f, such as the default Component.TOP_ALIGNMENT(0.0f),
     * Component.CENTER_ALIGNMENT (0.5f), and Component.BOTTOM (1.0f).
     */
    public NotificationManager setAlignmentY(float alignmentY) {
        this.alignmentY = alignmentY < 0 ? 0.0f : alignmentY > 1 ? 1.0f : alignmentY;
        return this;
    }

    /**
     * Setter for changing the speed at which the notification slides up and down.
     * @param animationSpeed float representing proportion of distance from current to target location to travel per tick
     */
    public NotificationManager setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed < 0 ? 0.0f : animationSpeed > 1 ? 1.0f : animationSpeed;
        return this;
    }

    /**
     * Set animation using bool to enable or disable animations completely.
     */
    public NotificationManager animationEnabled(boolean enabled) {
        this.animationSpeed = enabled? 0.2f : 1;
        return this;
    }

    /**
     * Setter for changing duration of time the notification will remain in its fully displayed state, before
     * it starts moving back up. If displayTime <= 0, the notification will stay on screen indefinitely until
     * manually dismissed by clicking on it or calling dismissCurrentNotification.
     * @param displayTime time in ms
     */
    public NotificationManager setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
        return this;
    }

    /**
     * Setter for altering the margins between edge of the frame and the notification panel at its fully displayed
     * state. Can be used together with alignment setters for precise positioning.
     */
    public NotificationManager setMargins(Insets insets) {
        this.margins = insets;
        return this;
    }

    /**
     * Dismiss the currently displayed notification if there is one.
     */
    public NotificationManager dismissCurrentNotification() {
        if (!status.equals(Status.IDLE)) status = Status.MOVING_UP;
        return this;
    }

    /**
     * Clear all notifications in the queue. Does not affect current notification.
     */
    public NotificationManager clearNotificationQueue() {
        queuedNotifications.clear();
        return this;
    }

    /**
     * Specifies whether to call applyColourAndFontStyling
     */
    private void setApplyColourAndFontStyling(boolean applyColourAndFontStyling) {
        this.applyColourAndFontStyling = applyColourAndFontStyling;
    }

    /**
     * Set the colours to be applied to notifications
     */
    public NotificationManager setColours(Color foregroundColour, Color backgroundColour, Color hoverColour) {
        this.foregroundColour = foregroundColour;
        this.backgroundColour = backgroundColour;
        this.hoverColour = hoverColour;
        setApplyColourAndFontStyling(true);
        return this;
    }

    /**
     * Set font for notifications
     */
    public NotificationManager setFont(String fontname) {
        this.fontName = fontname;
        setApplyColourAndFontStyling(true);
        return this;
    }

    /**
     * Set the border to be applied to notifications
     */
    public NotificationManager setBorder(Border border) {
        this.border = border;
        return this;
    }

    /**
     * Set styling for foreground/background colours, font and border based on a component.
     */
    public NotificationManager setStyling(Component component) {
        this.foregroundColour = component.getForeground();
        this.backgroundColour = component.getBackground();
        setFont(component.getFont().getFontName());
        if (component instanceof JComponent jc) setBorder(jc.getBorder());
        return this;
    }

    /**
     * Apply font, foreground and background colours to a Component.
     * If Container, recurse through its children.
     */
    public NotificationManager applyStyling(Component component) {
        if (!applyColourAndFontStyling) return this;
        // Set colours
        if (component.getName() != null && component.getName().equals("Locked achievement text")) {
            // Darken text colour if locked achievement
            Color lockedAchievementColour = new Color(foregroundColour.getRed() / 2, foregroundColour.getGreen() / 2, foregroundColour.getBlue() / 2);
            component.setForeground(lockedAchievementColour);
        } else component.setForeground(foregroundColour);
        component.setBackground(backgroundColour);
        // Set border for buttons
        if (component instanceof JButton jb) {
            jb.setOpaque(true);
            jb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    jb.setBackground(backgroundColour);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    jb.setBackground(hoverColour);
                }
            });
            jb.setBorder(border);
        }
        // Set font
        Font f = component.getFont();
        if (f != null) component.setFont(new Font(fontName, f.getStyle(), f.getSize()));
        // Recurse
        if (component instanceof Container container) {
            for (Component c : container.getComponents()) {
                applyStyling(c);
            }
        }
        return this;
    }

    /**
     * Apply border to a Component
     */
    private void applyBorder(Component component) {
        if (component instanceof JComponent jc && !(jc instanceof JInternalFrame)) jc.setBorder(border);
    }

}
