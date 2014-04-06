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

package net.tbnr.gearz.chat.channels;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;

/**
 * Listener for Channels that receives chat
 * messages and sends a message with the
 * sender and the message.
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author Jake
 * @since 1/16/2014
 */
public class ChannelsListener implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (!GearzBungee.getInstance().getChannelManager().isEnabled()) return;
        if (event.isCancelled()) return;
        if (event.isCommand()) return;
        if (event.getMessage().contains("\\")) {
            event.getSender().disconnect("Bad.");
            event.setCancelled(true);
            return;
        }
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();

        if (GearzBungee.getInstance().getChat().isPlayerInConversation(sender)) return;
        if (GearzBungee.getInstance().getChat().isMuted() && !sender.hasPermission("gearz.mute.bypass")) {
            event.setCancelled(true);
            sender.sendMessage(GearzBungee.getInstance().getFormat("chat-muted"));
            return;
        }

        String message = event.getMessage();
        GearzBungee.getInstance().getChannelManager().sendMessage(sender, message);
        event.setCancelled(true);
    }
}
