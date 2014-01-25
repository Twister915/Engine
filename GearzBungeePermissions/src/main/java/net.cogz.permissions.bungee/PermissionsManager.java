package net.cogz.permissions.bungee;

import com.mongodb.DB;
import net.cogz.permissions.GearzPermissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsManager extends GearzPermissions {

    @Override
    public List<String> onlinePlayers() {
        List<String> players = new ArrayList<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            players.add(p.getName());
        }
        return players;
    }

    @Override
    public void givePermsToPlayer(String player, String perm, boolean value) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (player == null) return;
        proxiedPlayer.setPermission(perm, value);
    }

    @Override
    public DB getDatabase() {
        return GearzBungeePermissions.getInstance().getMongoDB();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPostLogin(PostLoginEvent event) {
        onJoin(event.getPlayer().getName());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onLeave(PlayerDisconnectEvent event) {
        onQuit(event.getPlayer().getName());
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
