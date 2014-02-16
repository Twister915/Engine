package net.tbnr.gearz.event.player;


import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerLevelChangeEvent extends GearzPlayerEvent {
    private final Integer oldLevel;
    private final Integer newLevel;
    private static final HandlerList handlers = new HandlerList();

    public PlayerLevelChangeEvent(Integer oldLevel, Integer newLevel, GearzPlayer player) {
	    super(player);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public Integer getNewLevel() {
        return newLevel;
    }

    public Integer getOldLevel() {
        return oldLevel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
