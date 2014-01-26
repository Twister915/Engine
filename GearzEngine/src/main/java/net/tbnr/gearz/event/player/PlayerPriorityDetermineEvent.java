package net.tbnr.gearz.event.player;

import lombok.*;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public final class PlayerPriorityDetermineEvent extends Event implements Cancellable {
    @Getter
    @Setter
    private boolean cancelled;

    @Getter
    @Setter
    private boolean absolutePriority = false;

    @Getter
    @Setter
    private String joinMessage = null;

    @Getter
    @Setter
    @NonNull
    private GearzPlayer player;

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
