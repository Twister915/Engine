package net.tbnr.gearz.event.player;

import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerPointChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private GearzPlayer player;
    private Integer oldPoints;
    private Integer newPoints;
    private boolean canceled;

    public PlayerPointChangeEvent(GearzPlayer player, Integer oldPoints, Integer newPoints) {
        this.player = player;
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
        this.canceled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public Integer getNewPoints() {
        return newPoints;
    }

    @SuppressWarnings("unused")
    public Integer getOldPoints() {
        return oldPoints;
    }

    public GearzPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.canceled = b;
    }
}
