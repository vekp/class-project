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
    private JLayeredPane layeredPane;
    private final Animator animator;
    private final List<Component> queuedNotifications = new LinkedList<>();
    JPanel notificationPanel;
    // Margins to leave between frame edge and notification
    private Insets margins;
    // Alignment of notification panel
    private float alignmentX, alignmentY;
    private int notificationPanelHeight;
    // Notification panel's position
    private int currentX;
    private double currentY;
    // Notification panel's target position
    private int targetY;
    // Speed in pixels to move per 16ms frame
    private float animationSpeed;
    // Duration to wait in fully displayed state, in milliseconds
    private int displayTime;
    // Timer starts when notification is fully displayed
    private long startTime;
    private boolean applyColourAndFontStyling;
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
        this.animator = mnClient.getAnimator();
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
        if (applyColourAndFontStyling) applyStyling(notificationPanel);
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
        // Notification panel dimensions
        int notificationPanelWidth = (int) notificationPanel.getPreferredSize().getWidth();
        notificationPanelHeight = (int) notificationPanel.getPreferredSize().getHeight();
        // calculate start position
        int maxX = layeredPane.getWidth() - notificationPanelWidth - margins.right;
        int minX = margins.left;
        currentX = minX + (int) (alignmentX * (maxX - minX));
        currentY = -notificationPanelHeight;
        // calculate target Y position
        int minY = margins.top;
        int maxY = layeredPane.getHeight() - notificationPanelHeight - margins.bottom;
        targetY = minY + (int) (alignmentY * (maxY - minY));
        // set bounds, add to layer
        notificationPanel.setBounds(currentX, (int) currentY, notificationPanelWidth, notificationPanelHeight);
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
                currentY += (targetY - currentY) * animationSpeed;
                int y = (int) Math.round(currentY);
                notificationPanel.setLocation(currentX, y);
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
                currentY -= (targetY - currentY) * animationSpeed;
                int y = (int) Math.round(currentY);
                notificationPanel.setLocation(currentX, y);
                if (y <= -notificationPanelHeight) {
                    status = Status.IDLE;
                    layeredPane.remove(notificationPanel);
                }
            }
        }
        al.requestTick(this);
    }

    /**
     * Reset settings to their default values. Default values are: <br>
     * alignmentX       :   Component.RIGHT_ALIGNMENT (1.0f) <br>
     * alignmentY       :   Component.TOP_ALIGNMENT (0.0f) <br>
     * animationSpeed   :   0.15f <br>
     * displayTime      :   5000 <br>
     * margins          :   Insets(5, 5, 5, 5) <br>
     * colours          :   system default colours <br>
     * font             :   system default font <br>
     * border           :   EtchedBorder()
     */
    public void resetToDefaultSettings() {
        setAlignmentX(Component.RIGHT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);
        setAnimationSpeed(0.2f);
        setDisplayTime(5000);
        setMargins(new Insets(5, 5, 5, 5));
        // Dummy panel for getting system default colours and font
        JPanel defaultPanel = new JPanel();
        setColours(defaultPanel.getForeground(), defaultPanel.getBackground());
        setFont(defaultPanel.getFont().getFontName());
        setBorder(BorderFactory.createEtchedBorder());
        setApplyColourAndFontStyling(false);
        this.layeredPane = frame.getLayeredPane();
    }

    /**
     * Set the area to contain notifications based on the given Component.
     * NOTE: This may not work as intended depending on the Layout Manager used by the Component's parent.
     */
    public void setNotificationArea(Component notificationArea) {
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
    }

    /**
     * Setter for horizontal alignment of the notification.
     * Possible alignment values are in the range 0.0f - 1.0f, such as Component.LEFT_ALIGNMENT(0.0f),
     * Component.CENTER_ALIGNMENT (0.5f), and the default Component.RIGHT_ALIGNMENT (1.0f).
     */
    public void setAlignmentX(float alignmentX) {
        this.alignmentX = alignmentX < 0 ? 0 : alignmentX > 1 ? 1 : alignmentX;
    }

    /**
     * Setter for vertical alignment of the notification.
     * Possible alignment values are in the range 0.0f - 1.0f, such as the default Component.TOP_ALIGNMENT(0.0f),
     * Component.CENTER_ALIGNMENT (0.5f), and Component.BOTTOM (1.0f).
     */
    public void setAlignmentY(float alignmentY) {
        this.alignmentY = alignmentY < 0 ? 0.0f : alignmentY > 1 ? 1.0f : alignmentY;
    }

    /**
     * Setter for changing the speed at which the notification slides up and down.
     * @param animationSpeed float representing proportion of distance from current to target location to travel per tick
     */
    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed < 0 ? 0.0f : animationSpeed > 1 ? 1.0f : animationSpeed;
    }

    /**
     * Set animation using bool to enable or disable animations completely.
     */
    public void animationEnabled(boolean enabled) {
        this.animationSpeed = enabled? 0.15f : 1;
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
     * state. Can be used together with alignment setters for precise positioning.
     */
    public void setMargins(Insets insets) {
        this.margins = insets;
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
     * Specifies whether to call applyColourAndFontStyling
     */
    private void setApplyColourAndFontStyling(boolean applyColourAndFontStyling) {
        this.applyColourAndFontStyling = applyColourAndFontStyling;
    }

    /**
     * Set the colours to be applied to notifications
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
     * Set the border to be applied to notifications
     */
    public void setBorder(Border border) {
        this.border = border;
    }

    /**
     * Set styling for foreground/background colours, font and border in one method.
     */
    public void setStyling(Color foregroundColour, Color backgroundColour, String fontName, Border border) {
        setColours(foregroundColour, backgroundColour);
        setFont(fontName);
        setBorder(border);
    }

    /**
     * Set styling for foreground/background colours, font and border based on a component.
     */
    public void setStyling(Component component) {
        setColours(component.getForeground(), component.getBackground());
        setFont(component.getFont().getFontName());
        if (component instanceof JComponent jc) setBorder(jc.getBorder());
    }

    /**
     * Apply font, foreground and background colours to a Component.
     * If Container, recurse through its children.
     */
    private void applyStyling(Component component) {
        // Set colours
        component.setForeground(foregroundColor);
        component.setBackground(backgroundColor);
        if (component instanceof JComponent jc) {
            jc.setOpaque(true);
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

}
