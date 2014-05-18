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

package net.cogz.engine.hub.modules;

import net.cogz.engine.hub.GearzHub;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/12/13
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class HideStream implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent event) {
        if (GearzHub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setJoinMessage(null);
            return;
        }
        event.setJoinMessage(GearzHub.getInstance().getFormat("formats.join-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
        if (GearzHub.getInstance().getConfig().getBoolean("welcome-messages")) {
            Bukkit.broadcastMessage(GearzHub.getInstance().getFormat("formats.welcome-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        if (GearzHub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setQuitMessage(null);
            return;
        }
        event.setQuitMessage(GearzHub.getInstance().getFormat("formats.quit-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onPlayerKick(PlayerKickEvent event) {
        if (GearzHub.getInstance().getConfig().getBoolean("hide-stream")) {
            event.setLeaveMessage(null);
            return;
        }
        event.setLeaveMessage(GearzHub.getInstance().getFormat("formats.quit-message", false, new String[]{"<player>", event.getPlayer().getPlayer().getName()}));
    }
}
