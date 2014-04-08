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

package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by rigor789 on 2013.12.29..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ColoredTablist implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateNick(player);
    }

    public static String getPlayerTabColor(Player player) {
        PermissionsDelegate permsManager = Gearz.getInstance().getPermissionsDelegate();
        String tabColor = permsManager.getTabColor(player.getName());
        return tabColor != null ? tabColor : "";
    }

    public static void updateNick(Player player) {
        String name = ChatColor.translateAlternateColorCodes('&', getPlayerTabColor(player) + player.getDisplayName());
        player.setPlayerListName(name.substring(0, Math.min(name.length(), 16)));
    }
}
