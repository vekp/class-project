package minigames.client.achievementui;

import io.vertx.ext.auth.impl.asn.ASN1;
import minigames.achievements.Achievement;
import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.MinigameNetworkClientWindow;
import minigames.client.Tickable;
import minigames.client.notifications.NotificationManager;

import javax.swing.*;
import java.util.List;

/**
 * This is a manager class that will monitor the server to detect when a new achievement has been earned. When this
 * occurs, it will use the NotificationManager to do a 'pop up' panel animation to display the unlocked achievement
 */
public class AchievementNotificationHandler implements Tickable {

    private final NotificationManager popupManager;
    private final MinigameNetworkClient client;
    int tickInterval = 10; //how many frames we wait between achievement checks
    int tickTimer = 0; //how many ticks have passed since last check

    Animator anim;
    /**
     * Constructor
     *
     * @param mnClient the network client, for sending requests to the server and obtaining the animator
     */
    public AchievementNotificationHandler(NotificationManager popupManager, MinigameNetworkClient mnClient) {
        //we want the notification manager attached to our client window
        this.popupManager = popupManager;
        this.client = mnClient;
        this.anim = mnClient.getAnimator();
        anim.requestTick(this);
    }

    @Override
    public void tick(Animator al, long now, long delta) {
        //
        tickTimer++;
        if (tickTimer > tickInterval) {
            tickTimer = 0; //reset timer

            //ask the server for the list of achievements that were unlocked since last time we asked
            //these will be added in-order to the NotificationManager's queue to pop up achievement alerts
            client.getRecentAchievements().onSuccess(this::processAchievements);
        } else {
            al.requestTick(this);
        }
    }

    void processAchievements(List<Achievement> unlocks) {
            for (Achievement unlock : unlocks) {
                AchievementPresenter presenter = new AchievementPresenter(unlock, true);
                JPanel popup = presenter.tinyAchievementPanel(false);
                popup.setBorder(null);
                popupManager.showNotification(popup);
            }

        anim.requestTick(this);
    }
}
