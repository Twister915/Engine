package net.cogz.permissions.bukkit;

import com.mongodb.DB;
import net.cogz.permissions.GearzPermissions;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.gearz.player.GearzPlayerNickname;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsManager extends GearzPermissions implements Listener {
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
        return GearzBukkitPermissions.getInstance().getMongoDB();
    }

    @Override
    public void updatePlayerDisplays(String player, String prefix, String nameColor, String tabColor) {

    }

    @Override
    public void updatePlayerNameColor(String player, String name_color) {

    }

    @Override
    public void updatePlayerSuffix(String player, String suffix) {

    }

    @Override
    public void updatePlayerPrefix(String player, String prefix) {

    }

    @Override
    public void updatePlayerTabColor(String player, String tab_color) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 == null) return;
        Object storable = GearzPlayer.playerFromPlayer(player1).getTPlayer().getStorable(Gearz.getInstance(), new GearzPlayerNickname(null));
        if (!(storable instanceof String)) {
            storable = player;
        }
        String name = (String) storable;
        player1.setPlayerListName(ChatColor.translateAlternateColorCodes('&', tab_color + name).substring(14));
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerJoinEvent event) {
        onHandlerJoin(event.getPlayer().getName());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerQuit(PlayerQuitEvent event) {
        onQuit(event.getPlayer().getName());
    }
}
