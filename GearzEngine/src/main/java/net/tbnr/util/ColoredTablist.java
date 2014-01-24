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

    public static String getPlayerSuffix(Player player) {
        String suffix = Gearz.getInstance().getChat().getPlayerSuffix(player);
        if (suffix == null || suffix.equals("")) {
            String group = Gearz.getInstance().getPermission().getPrimaryGroup(player);
            suffix = Gearz.getInstance().getChat().getGroupSuffix(player.getWorld().getName(), group);
            if (suffix == null) {
                suffix = "";
            }
        }
        return suffix;
    }

    public static void updateNick(Player player) {
        String name = ChatColor.translateAlternateColorCodes('&', getPlayerSuffix(player) + player.getDisplayName());
        player.setPlayerListName(name.substring(0, Math.min(name.length(), 16)));
    }
}
