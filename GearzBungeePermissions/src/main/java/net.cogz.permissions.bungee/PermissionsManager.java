package net.cogz.permissions.bungee;

import com.google.common.base.Preconditions;
import net.cogz.permissions.GearzPermissions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 * <p/>
 * Many of these methods are not used bungee side
 */
public class PermissionsManager extends GearzPermissions implements Listener {
    private final List<ProxiedPlayer> playersAlreadyConnected = new ArrayList<>();

    @Override
    public List<String> onlinePlayers() {
        List<String> players = new ArrayList<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            System.out.println(p.getName() + " online!");
            players.add(p.getName());
        }
        return players;
    }

    @Override
    public void givePermsToPlayer(String player, String perm, boolean value) {
        System.out.println("We're adding a permission now!");
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        Preconditions.checkNotNull(player, "player can not be null");
        Preconditions.checkNotNull(perm, "permission can not be null");
        Preconditions.checkNotNull(value, "value can not be null");
        Preconditions.checkNotNull(proxiedPlayer, "proxied player can not be null");
        if (proxiedPlayer == null) return;
        proxiedPlayer.setPermission(perm, value);
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
    public void updatePlayerDisplays(String player, String prefix, String nameColor, String tabColor) {

    }

    @Override
    public void updatePlayerNameColor(String player, String nameColor) {

    }

    @Override
    public void updatePlayerSuffix(String player, String suffix) {

    }

    @Override
    public void updatePlayerPrefix(String player, String prefix) {

    }

    @Override
    public void updatePlayerTabColor(String player, String tabColor) {

    }
}
