package net.tbnr.gearz.event.player;

import lombok.Getter;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;

/**
 * Created by Joey on 1/12/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class PlayerGameKillEvent extends PlayerGameDeathEvent {
    @Getter
    private GearzPlayer killer;
    public PlayerGameKillEvent(GearzGame game, GearzPlayer dead, GearzPlayer killer) {
        super(game, dead);
        this.killer = killer;
    }
    /*
      Event code
      */
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
