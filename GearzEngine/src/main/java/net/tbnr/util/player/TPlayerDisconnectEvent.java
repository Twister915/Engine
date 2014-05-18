/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util.player;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/5/13
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
public final class TPlayerDisconnectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String quitMessage;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final TPlayer player;

    public TPlayerDisconnectEvent(TPlayer player) {
        this.player = player;
        this.quitMessage = ChatColor.YELLOW + player.getPlayer().getName() + " has left the game.";
    }

    public TPlayer getPlayer() {
        return player;
    }

    public String getQuitMessage() {
        return quitMessage;
    }

    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }
}
