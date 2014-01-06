package net.tbnr.gearz.event.game;

import net.tbnr.gearz.game.GearzGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/6/13
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameStartEvent extends Event {
    private GearzGame game;
    /*
    Event code
     */
    private static final HandlerList handlers = new HandlerList();

    public GameStartEvent(GearzGame game) {
        this.game = game;
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
}
