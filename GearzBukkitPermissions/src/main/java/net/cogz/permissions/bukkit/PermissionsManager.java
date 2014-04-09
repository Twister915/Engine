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

package net.cogz.permissions.bukkit;

import com.mongodb.DB;
import com.mongodb.DBObject;
import net.cogz.permissions.GearzPermissions;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.PermissionsDelegate;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bukkit Specific Permissions API
 */
public class PermissionsManager extends GearzPermissions implements Listener, PermissionsDelegate {
    private Map<String, Player> loggedPlayers = new HashMap<>();

    @Override
    public List<String> onlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    @Override
    public void givePermsToPlayer(String player, String perm, boolean value) {
        Player p = loggedPlayers.get(player);
        if (p == null) return;
        System.out.println("Giving Bukkit permission to: " + player + " with values " + perm + ":" + value);
        p.addAttachment(GearzBukkitPermissions.getInstance(), perm, value);
    }

    @Override
    public DB getDatabase() {
        return Gearz.getInstance().getMongoDB();
    }

    @Override
    public String getUUID(String player) {
        if (loggedPlayers.containsKey(player)) {
            return loggedPlayers.get(player).getUniqueId().toString();
        }
        DBObject playerDocument = getPlayerDocument(player);
        return (String) playerDocument.get("uuid");
    }

    public boolean isPlayerOnline(String player) {
        return Bukkit.getPlayerExact(player) != null;
    }

    public DBObject getPlayerDocument(String player) {
        if (player == null) return null;
        if (isPlayerOnline(player)) {
            Player bukkitPlayer = Bukkit.getPlayerExact(player);
            TPlayer tPlayer = TPlayerManager.getInstance().getPlayer(bukkitPlayer);
            return tPlayer.getPlayerDocument();
        } else {
            return TPlayer.getPlayerObjectByLastKnownName(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerLoginEvent event) {
        loggedPlayers.put(event.getPlayer().getName(), event.getPlayer());
        onJoin(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerQuit(PlayerQuitEvent event) {
        loggedPlayers.remove(event.getPlayer().getName());
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
