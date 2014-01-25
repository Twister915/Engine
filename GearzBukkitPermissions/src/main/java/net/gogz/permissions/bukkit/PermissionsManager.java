package net.gogz.permissions.bukkit;

import com.mongodb.DB;
import net.cogz.permissions.GearzPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsManager extends GearzPermissions {
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
        p.addAttachment(GearzBukkitPerimssions.getInstance(), perm, value);
    }

    @Override
    public DB getDatabase() {
        return GearzBukkitPerimssions.getInstance().getMongoDB();
    }

    @Override
    public void updatePlayerDisplays(String player, String prefix, String name_color, String tab_color) {

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

    }
}
