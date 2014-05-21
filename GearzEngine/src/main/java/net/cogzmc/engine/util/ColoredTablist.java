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

package net.cogzmc.engine.util;

import net.cogzmc.engine.gearz.Gearz;
import net.cogzmc.engine.util.delegates.PermissionsDelegate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Created by rigor789 on 2013.12.29..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ColoredTablist implements Listener {

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
