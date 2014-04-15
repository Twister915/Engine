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

package net.cogz.chat.channels;

import net.cogz.chat.GearzChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles messaging on channels
 * and join and quit events
 * which allow the {@link ChannelManager} to
 * register a player to a channel.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public class ChannelsListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(AsyncPlayerChatEvent event) {
        if (!GearzChat.getInstance().getChannelManager().isEnabled()) return;
        if (event.isCancelled()) return;
        Player sender = event.getPlayer();

        if (GearzChat.getInstance().getChat().isMuted() && !sender.hasPermission("gearz.mute.bypass")) {
            event.setCancelled(true);
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.chat-muted"));
            return;
        }

        String message = event.getMessage();
        GearzChat.getInstance().getChannelManager().sendMessage(sender, message);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent event) {
        ChannelManager channelManager = GearzChat.getInstance().getChannelManager();
        channelManager.setChannel(event.getPlayer(), channelManager.getDefaultChannel());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        ChannelManager channelManager = GearzChat.getInstance().getChannelManager();
        channelManager.removeChannel(event.getPlayer());
    }
}
