package net.cogz.permissions.bungee;

import com.mongodb.DB;
import net.cogz.permissions.GearzPermissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.player.bungee.PermissionsDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsManager extends GearzPermissions implements Listener, PermissionsDelegate {
    private final List<ProxiedPlayer> playersAlreadyConnected = new ArrayList<>();

    @Override
    public List<String> onlinePlayers() {
        return GearzBungee.getInstance().getUserNames();
    }

    @Override
    public void givePermsToPlayer(String player, String perm, boolean value) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer == null) return;
        proxiedPlayer.setPermission(perm, value);
    }

    @Override
    public DB getDatabase() {
        GModel.setDefaultDatabase(GearzBungee.getInstance().getMongoDB());
        return GearzBungee.getInstance().getMongoDB();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPostLogin(ServerConnectEvent event) {
        if (!this.playersAlreadyConnected.contains(event.getPlayer())) {
            this.playersAlreadyConnected.add(event.getPlayer());
            onJoin(event.getPlayer().getName());
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onLeave(PlayerDisconnectEvent event) {
        this.playersAlreadyConnected.remove(event.getPlayer());
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
