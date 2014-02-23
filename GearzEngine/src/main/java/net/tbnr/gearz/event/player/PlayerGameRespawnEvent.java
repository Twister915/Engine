package net.tbnr.gearz.event.player;

import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.HandlerList;

public final class PlayerGameRespawnEvent extends GearzPlayerGameEvent {
	/*
    Event code
    */
	private static final HandlerList handlers = new HandlerList();

	public PlayerGameRespawnEvent(GearzPlayer player, GearzGame game) {
		super(player, game);
	}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
