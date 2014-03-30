/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.event.player;

import net.tbnr.gearz.game.GearzGame;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerLoseGameEvent extends Event {
    private final GearzPlayer player;
    private final GearzGame game;

    private static final HandlerList handlers = new HandlerList();

    public PlayerLoseGameEvent(GearzPlayer player, GearzGame game) {
        this.player = player;
        this.game = game;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GearzPlayer getPlayer() {
        return player;
    }

    public GearzGame getGame() {
        return game;
    }
}