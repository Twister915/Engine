package net.tbnr.util;

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
        /*PermissionsManager permsManager = Gearz.getInstance().getPermissions().getPermsManager();
        String tabCoolor = permsManager.getTabColor(permsManager.getPlayer(player.getName()));
        return tabCoolor != null ? tabCoolor : "";*/
    }

    public static void updateNick(Player player) {
        String name = ChatColor.translateAlternateColorCodes('&', getPlayerTabColor(player) + player.getDisplayName());
        player.setPlayerListName(name.substring(0, Math.min(name.length(), 16)));
    }
}
