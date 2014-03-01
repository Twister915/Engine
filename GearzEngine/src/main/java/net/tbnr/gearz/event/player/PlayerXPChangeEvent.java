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

import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerXPChangeEvent extends Event {
    private final Integer oldXp;
    private final Integer newXp;
    private final GearzPlayer player;
    private static final HandlerList handlers = new HandlerList();

    public PlayerXPChangeEvent(Integer oldXp, Integer newXp, GearzPlayer player) {
        this.oldXp = oldXp;
        this.newXp = newXp;
        this.player = player;
    }

    /**
     * Get the player this event relates to.
     *
     * @return A GearzPlayer object
     */
    public GearzPlayer getPlayer() {
        return player;
    }

    @SuppressWarnings("unused")
    public Integer getNewXp() {
        return newXp;
    }

    @SuppressWarnings("unused")
    public Integer getOldXp() {
        return oldXp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
