package net.tbnr.gearz.event.player;

import lombok.*;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerPriorityDetermineEvent extends GearzPlayerEvent implements Cancellable {
	/*
    Event code
    */
	private static final HandlerList handlers = new HandlerList();

    @Getter @Setter
    private boolean cancelled;

    @Getter @Setter
    private boolean absolutePriority = false;

    @Getter @Setter
    private String joinMessage = null;

	public PlayerPriorityDetermineEvent(@NonNull GearzPlayer player) {
		super(player);
	}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
