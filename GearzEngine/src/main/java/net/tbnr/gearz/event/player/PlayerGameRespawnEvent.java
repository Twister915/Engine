package net.tbnr.gearz.event.player;

import lombok.*;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerGameRespawnEvent extends Event {
    @Setter(AccessLevel.NONE) private GearzPlayer player;
    @Setter(AccessLevel.NONE) private GearzGame game;
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
