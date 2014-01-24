package net.tbnr.gearz.game.voting;

import lombok.*;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Joey on 1/9/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public final class PlayerMapVoteEvent extends Event {
    private Integer numberOfVotes;
    @Setter(AccessLevel.PACKAGE)private GearzPlayer player;
    @Setter(AccessLevel.PACKAGE)private Votable votable;
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
