package net.tbnr.util;

import net.cogz.permissions.bukkit.PermissionsManager;
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

    public static String getPlayerPrefix(Player player) {
        PermissionsManager permsManager = Gearz.getInstance().getPermissions().getPermsManager();
        String prefix = permsManager.getPrefix(permsManager.getPlayer(player.getName()));
        return prefix != null ? prefix : "";
    }

    public static void updateNick(Player player) {
        String name = ChatColor.translateAlternateColorCodes('&', getPlayerPrefix(player) + player.getDisplayName());
        player.setPlayerListName(name.substring(0, Math.min(name.length(), 16)));
    }
}
