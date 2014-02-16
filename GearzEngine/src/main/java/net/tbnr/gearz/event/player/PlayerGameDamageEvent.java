package net.tbnr.gearz.event.player;

import lombok.*;
import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is attacked in a game
 */
public class PlayerGameDamageEvent extends GearzPlayerGameEvent implements Cancellable {

	/*
	Event code
	*/
	private static final HandlerList handlers = new HandlerList();

	@Getter
    private final double damage;
	@Getter @Setter
    private boolean cancelled;

	public PlayerGameDamageEvent(GearzGame game, GearzPlayer player, Double damage, boolean cancelled) {
		super(player, game);
		this.damage = damage;
		this.cancelled = cancelled;
	}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
