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

package net.cogz.chat.channels;

import net.cogz.chat.GearzChat;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.event.game.GameStartEvent;
import net.tbnr.gearz.event.player.PlayerBeginSpectateEvent;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.Bukkit;
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
 * <p/>
 * <p/>
 * Latest Change: Rewrite for Bukkit
 * <p/>
 *
 * @author Jake
 * @since 1/16/2014
 */
public final class ChannelsListener implements Listener {
    private ChannelManager channelManager;

    public ChannelsListener(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(AsyncPlayerChatEvent event) {
        if (!GearzChat.getInstance().getChannelManager().isEnabled()) return;
        if (event.isCancelled()) return;
        Player sender = event.getPlayer();

        GearzChat.getInstance().getChannelManager().sendMessage(sender, event.getMessage());
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSpectate(PlayerBeginSpectateEvent event) {
        if (!Gearz.getInstance().isGameServer()) return;
        Player player = event.getPlayer().getPlayer();
        channelManager.setChannel(player, channelManager.getChannelByName("spectator"));
        player.addAttachment(GearzChat.getInstance(), channelManager.getChannelByName("spectator").getPermission(), true);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            channelManager.setChannel(player, channelManager.getDefaultChannel());
        }
    }

    @EventHandler
    public void onChannelSwitchEvent(ChannelSwitchEvent event) {
        if (Gearz.getInstance().isGameServer() && !event.isNecessary() && event.getNewChannel().getName().equals("default")) {
            event.getPlayer().sendMessage(GearzChat.getInstance().getFormat("formats.spectating", true));
            event.setCancelled(true);
        } else if (Gearz.getInstance().isGameServer() && event.isNecessary() && event.getNewChannel().getName().equals("spectator")) {
            event.getPlayer().sendMessage(GearzChat.getInstance().getFormat("formats.ingame", true));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent event) {
        GearzPlayer player = Gearz.getInstance().getPlayerProvider().getPlayerFromPlayer(event.getPlayer());
        if (player.getGame() != null && player.getGame().isRunning()) {
            channelManager.setChannel(event.getPlayer(), channelManager.getChannelByName("spectator"));
            event.getPlayer().addAttachment(GearzChat.getInstance(), channelManager.getChannelByName("spectator").getPermission(), true);
            return;
        }
        channelManager.setChannel(event.getPlayer(), channelManager.getDefaultChannel(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        channelManager.removeChannel(event.getPlayer());
    }
}
