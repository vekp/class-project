package minigames.client.battleship;

import minigames.client.Animator;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;

public class BattleshipUpdateHandler implements Tickable {
    private final MinigameNetworkClient client;
    int tickInterval = 10;
    int tickTimer = 0;
    Animator animator;

    /**
     * @param mnClient
     */
    public BattleshipUpdateHandler(MinigameNetworkClient mnClient) {
        this.client = mnClient;
        this.animator = mnClient.getAnimator();
        animator.requestTick(this);
    }

    @Override
    public void tick(Animator al, long now, long delta) {
        tickTimer++;
        if (tickTimer > tickInterval) {
            tickTimer = 0; //reset timer

            System.out.println("\n\nWorks I guess\n\n");
        } else {
            al.requestTick(this);
        }
    }
}
