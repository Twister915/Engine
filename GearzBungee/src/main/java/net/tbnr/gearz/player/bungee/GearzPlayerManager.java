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

package net.tbnr.gearz.player.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages logins and logouts for GearzPlayers.
 * Also provides utility methods to retrieving
 * a Gearz player based on certain parameters.
 *
 * <p>
 * Latest Change: Add matched players method
 * <p>
 *
 * @author Joey
 * @since 9/13/2013
 */
public final class GearzPlayerManager implements Listener {
    private final HashMap<String, GearzPlayer> players = new HashMap<>();
    private static GearzPlayerManager instance;
    private final List<ProxiedPlayer> playersAlreadyConnected;

    public static GearzPlayerManager getInstance() {
        return GearzPlayerManager.instance;
    }

    public GearzPlayerManager() {
        GearzPlayerManager.instance = this;
        this.playersAlreadyConnected = new ArrayList<>();
    }

    public static GearzPlayer getGearzPlayer(ProxiedPlayer player) {
        GearzPlayerManager.getInstance().storePlayer(player);
        return GearzPlayerManager.getInstance().players.get(player.getName());
    }

    public void storePlayer(ProxiedPlayer player) {
        if (player == null) return;
        if (this.players.containsKey(player.getName())) return;
        try {
            this.players.put(player.getName(), new GearzPlayer(player));
        } catch (GearzPlayer.PlayerNotFoundException e) {
            //Ignored
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void removePlayer(PlayerDisconnectEvent event) {
        this.players.remove(event.getPlayer().getName());
        this.playersAlreadyConnected.remove(event.getPlayer());
        if (GearzBungee.getInstance().getChannelManager().isEnabled()) {
            GearzBungee.getInstance().getChannelManager().removeChannel(event.getPlayer());
        }
    }

    @EventHandler
    public void playerLoginEvent(ServerConnectEvent event) {
        if (!this.playersAlreadyConnected.contains(event.getPlayer())) {
            this.playersAlreadyConnected.add(event.getPlayer());
            ServerInfo aHubServer = GearzBungee.getInstance().getHub().getAHubServer();
            if (aHubServer == null) {
                return;
            }
            event.setTarget(aHubServer);
            if (GearzBungee.getInstance().getChannelManager().isEnabled()) {
                GearzBungee.getInstance().getChannelManager().setChannel(event.getPlayer(), GearzBungee.getInstance().getChannelManager().getDefaultChannel());
            }
        }
    }

    public List<ProxiedPlayer> getMatchedPlayers(String start) {
        List<ProxiedPlayer> playerList = new ArrayList<>();
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.getName().toLowerCase().equals(start.toLowerCase())) {
                List<ProxiedPlayer> needleMatch = new ArrayList<>();
                needleMatch.add(proxiedPlayer);
                return needleMatch;
            }
            if (proxiedPlayer.getName().toLowerCase().startsWith(start.toLowerCase())) {
                playerList.add(proxiedPlayer);
            }
        }
        return playerList;
    }
}
