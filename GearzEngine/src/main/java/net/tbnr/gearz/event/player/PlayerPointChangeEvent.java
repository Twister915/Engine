package net.tbnr.gearz.event.player;

import lombok.Getter;
import lombok.Setter;
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
public final class PlayerPointChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final GearzPlayer player;
    @Getter
    private final Integer oldPoints;
    @Getter
    private final Integer newPoints;
    @Getter @Setter
    private Integer points;
    private boolean canceled;

    public PlayerPointChangeEvent(GearzPlayer player, Integer oldPoints, Integer newPoints, Integer points) {
        this.player = player;
        this.oldPoints = oldPoints;
        this.newPoints = newPoints;
        this.points = points;
        this.canceled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
