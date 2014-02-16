package net.tbnr.gearz.event.player;

import lombok.AccessLevel;
import lombok.Getter;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/19/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerChangeDonorPointsEvent extends GearzPlayerEvent {
	@Getter
    private final Integer oldPoint;
	@Getter
    private final Integer newPoint;

    public PlayerChangeDonorPointsEvent(Integer current_points, Integer newPoint, GearzPlayer player) {
	    super(player);
        this.oldPoint = current_points;
        this.newPoint = newPoint;
    }

    /*
   Event code
    */
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
