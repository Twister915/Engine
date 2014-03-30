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
import net.cogz.permissions.GearzPermissions;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.util.PermissionsDelegate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Bukkit Specific Permissions API
 */
public class PermissionsManager extends GearzPermissions implements Listener, PermissionsDelegate {
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
        Player p = Bukkit.getPlayerExact(player);
        if (p == null) return;
        p.addAttachment(GearzBukkitPermissions.getInstance(), perm, value);
    }

    @Override
    public DB getDatabase() {
        return Gearz.getInstance().getMongoDB();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerJoinEvent event) {
        onJoin(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerQuit(PlayerQuitEvent event) {
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
