package net.tbnr.gearz.event.player;

import lombok.AccessLevel;
import lombok.Getter;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerXPChangeEvent extends GearzPlayerEvent {
	@Getter(value = AccessLevel.PUBLIC)
    private final Integer oldXp;
	@Getter(value = AccessLevel.PUBLIC)
    private final Integer newXp;
    private static final HandlerList handlers = new HandlerList();

    public PlayerXPChangeEvent(Integer oldXp, Integer newXp, GearzPlayer player) {
	    super(player);
        this.oldXp = oldXp;
        this.newXp = newXp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
