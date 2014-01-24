package net.tbnr.gearz.event.game;

import net.tbnr.gearz.game.GearzGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GamePreStartEvent extends Event implements Cancellable {
    private final GearzGame game;
    private boolean cancelled;
    private String reasonCancelled;
    /*
    Event code
     */
    private static final HandlerList handlers = new HandlerList();

    public GamePreStartEvent(GearzGame gearzGame) {
        this.game = gearzGame;
        this.cancelled = false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GearzGame getGame() {
        return game;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public String getReasonCancelled() {
        return reasonCancelled;
    }

    @SuppressWarnings("unused")
    public void setReasonCancelled(String reasonCancelled) {
        this.reasonCancelled = reasonCancelled;
    }
}
