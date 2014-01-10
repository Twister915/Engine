package net.tbnr.gearz.event.player;

import lombok.*;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when someone is killed in a game
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerGameDeathEvent extends Event {
    @Setter(AccessLevel.NONE) private GearzGame game;
    @Setter(AccessLevel.NONE) private GearzPlayer dead;
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
