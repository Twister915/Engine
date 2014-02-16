package net.tbnr.gearz.event.game;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.game.GearzGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GamePreStartEvent extends GearzGameEvent implements Cancellable {
	@Getter @Setter
    private boolean cancelled;
	@Getter @Setter
	private String reasonCancelled;
    /*
    Event code
     */
    private static final HandlerList handlers = new HandlerList();

    public GamePreStartEvent(GearzGame game) {
        super(game);
        this.cancelled = false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
