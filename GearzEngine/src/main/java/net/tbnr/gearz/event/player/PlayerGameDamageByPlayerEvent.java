package net.tbnr.gearz.event.player;

import lombok.Getter;
import lombok.NonNull;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;

/**
 * Created by Joey on 1/12/14.
 */
public final class PlayerGameDamageByPlayerEvent extends PlayerGameDamageEvent {
    @NonNull @Getter private GearzPlayer damager;

    public PlayerGameDamageByPlayerEvent(PlayerGameDamageEvent event, GearzPlayer damager) {
        super(event.getGame(), event.getPlayer(), event.getDamage(), event.isCancelled());
        this.damager = damager;
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
