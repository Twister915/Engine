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
 * Date: 10/19/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PlayerChangeDonorPointsEvent extends Event {
    private final Integer oldPoint;
    private final Integer newPoint;
    private final GearzPlayer player;

    public PlayerChangeDonorPointsEvent(Integer current_points, Integer newPoint, GearzPlayer player) {
        this.oldPoint = current_points;
        this.newPoint = newPoint;
        this.player = player;
    }

    /*
   Event code
    */
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public Integer getOldPoint() {
        return oldPoint;
    }

    @SuppressWarnings("unused")
    public Integer getNewPoint() {
        return newPoint;
    }

    public GearzPlayer getPlayer() {
        return player;
    }
}
