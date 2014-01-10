package net.tbnr.gearz.event.player;

import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerBeginSpectateEvent extends Event {
    private GearzPlayer player;
    private GearzGame game;
    /*
    Event code
     */
    private static final HandlerList handlers = new HandlerList();

    public PlayerBeginSpectateEvent(GearzPlayer player, GearzGame game) {
        this.player = player;
        this.game = game;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GearzPlayer getPlayer() {
        return player;
    }

    public GearzGame getGame() {
        return game;
    }
}