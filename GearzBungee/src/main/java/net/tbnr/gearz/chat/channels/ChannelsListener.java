package net.tbnr.gearz.chat.channels;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.chat.Filter;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;

/**
 * Created by Jake on 1/16/14.
 */
public class ChannelsListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand()) return;
        if (!GearzBungee.getInstance().getChannelManager().isEnabled()) return;
        String message = event.getMessage();
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        GearzBungee.getInstance().getChannelManager().sendMessage(sender, message);
        event.setCancelled(true);
    }
}
