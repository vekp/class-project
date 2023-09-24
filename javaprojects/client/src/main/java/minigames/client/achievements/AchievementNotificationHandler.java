package minigames.client.achievements;

import minigames.achievements.Achievement;
import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;

import javax.swing.*;
import java.util.List;

/**
 * This is a manager class that will monitor the server to detect when a new achievement has been earned. When this
 * occurs, it will use the NotificationManager to do a 'pop up' panel animation to display the unlocked achievement
 */
public class AchievementNotificationHandler implements Tickable {

    private final MinigameNetworkClient client;
    int tickInterval = 10; //how many frames we wait between achievement checks
    int tickTimer = 0; //how many ticks have passed since last check

    String playerName;
    boolean canTick;
    Animator anim;

    /**
     * Constructor
     *
     * @param mnClient the network client, for sending requests to the server and obtaining the animator
     */
    public AchievementNotificationHandler(MinigameNetworkClient mnClient) {
        this.client = mnClient;
        this.anim = mnClient.getAnimator();
        canTick = false;
    }

    /**
     * Turns on the handler, asking it to start checking the server for recent unlocks.
     * Can only be done when a player is active on the client (e.g. a game has started)
     * @param playerName the player that we want to check achievements for
     */
    public void enable(String playerName) {
        this.playerName = playerName;
        canTick = true;
        anim.requestTick(this);
    }

    /**
     * Stops checking the server for recent achievement unlocks. Called any time a game is exited (as we no longer
     * have a player to check for)
     */
    public void disable() {
        canTick = false;
        playerName = "";
    }

    /**
     * Periodically ask the server if any achievements have been unlocked for the current player.
     * If any are sent back, schedule a notification popup to display to the player
     * @param al animator - used to re-request a tick
     * @param now current time?
     * @param delta time difference
     */
    @Override
    public void tick(Animator al, long now, long delta) {
        //when disabled we just cancel any scheduled ticks and do nothing
        if (!canTick) return;

        tickTimer++;
        if (tickTimer > tickInterval) {
            tickTimer = 0; //reset timer

            //ask the server for the list of achievements that were unlocked for the current client's player
            //these will be added in-order to the NotificationManager's queue to pop up achievement alerts
            client.getRecentAchievements(playerName).onSuccess(this::processAchievements);
        } else {
            al.requestTick(this);
        }
    }

    /**
     * If we have recent unlocks, this will go through them all and send a popup request to the notification system
     * @param unlocks list of achievements this player has recently unlocked
     */
    void processAchievements(List<Achievement> unlocks) {
        for (Achievement unlock : unlocks) {
            AchievementPresenter presenter = new AchievementPresenter(unlock, true);
            JPanel popup = presenter.smallAchievementPanel();
            client.getNotificationManager().showNotification(popup);
        }

        anim.requestTick(this);
    }
}
