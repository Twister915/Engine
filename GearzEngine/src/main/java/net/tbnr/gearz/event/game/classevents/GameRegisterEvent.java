package net.tbnr.gearz.event.game.classevents;

import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.arena.Arena;
import net.tbnr.gearz.game.GameMeta;
import net.tbnr.gearz.game.GearzGame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GameRegisterEvent extends GearzGameClassEvent implements Cancellable {
    private final Class<? extends Arena> arena;
    private final GearzPlugin plugin;
    private boolean cancelled;
    private final GameMeta meta;

    /*
    Event code
     */
    private static final HandlerList handlers = new HandlerList();

    public GameRegisterEvent(Class<? extends Arena> arena, Class<? extends GearzGame> game, GameMeta meta, GearzPlugin plugin) {
	    super(game);
        this.plugin = plugin;
        this.arena = arena;
        this.meta = meta;
        this.cancelled = false;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Class<? extends Arena> getArena() {
        return arena;
    }

    public GameMeta getMeta() {
        return meta;
    }

    public GearzPlugin getPlugin() {
        return plugin;
    }
}
