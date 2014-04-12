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

package net.cogz.permissions.bungee;

import com.mongodb.DB;
import com.mongodb.DBObject;
import net.cogz.permissions.GearzPermissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.gearz.player.bungee.PermissionsDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Bungee Specific Permissions API
 */
public class PermissionsManager extends GearzPermissions implements Listener, PermissionsDelegate {
    private final List<ProxiedPlayer> playersAlreadyConnected = new ArrayList<>();

    @Override
    public List<String> onlinePlayers() {
        return GearzBungee.getInstance().getUserNames();
    }

    @Override
    public void givePermsToPlayer(String player, String perm, boolean value) {
        ProxiedPlayer proxiedPlayer = getOnlinePlayer(player);
        if (proxiedPlayer == null) return;
        proxiedPlayer.setPermission(perm, value);
    }

    private ProxiedPlayer getOnlinePlayer(String name) {
        for (ProxiedPlayer player : this.playersAlreadyConnected) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }

    @Override
    public DB getDatabase() {
        return GearzBungee.getInstance().getMongoDB();
    }

    @Override
    public String getUUID(String player) {
        return (String) getPlayerDocument(player).get("uuid");
    }

    public DBObject getPlayerDocument(String player) {
        if (isPlayerOnline(player)) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            return GearzPlayerManager.getGearzPlayer(proxiedPlayer).getPlayerDocument();
        } else {
            try {
                return new GearzPlayer(player).getPlayerDocument();
            } catch (GearzPlayer.PlayerNotFoundException e) {
                return null;
            }
        }
    }

    public boolean isPlayerOnline(String player) {
        return ProxyServer.getInstance().getPlayer(player) != null;
    }


    @EventHandler(priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    public void onPostLogin(ServerConnectedEvent event) {
        if (!this.playersAlreadyConnected.contains(event.getPlayer())) {
            this.playersAlreadyConnected.add(event.getPlayer());
            onJoin(event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onLeave(PlayerDisconnectEvent event) {
        this.playersAlreadyConnected.remove(event.getPlayer());
        onQuit(event.getPlayer().getName());
    }

    @Override
    public String getPrefix(String player) {
        return getPrefix(getPlayer(player));
    }

    @Override
    public String getSuffix(String player) {
        return getSuffix(getPlayer(player));
    }

    @Override
    public String getTabColor(String player) {
        return getTabColor(getPlayer(player));
    }

    @Override
    public String getNameColor(String player) {
        return getNameColor(getPlayer(player));
    }

    @Override
    public List<String> getValidPermissions(String player) {
        return getPlayer(player).getPermissions();
    }

    @Override
    public List<String> getAllPermissions(String player) {
        return getPlayer(player).getPermissions();
    }
}
