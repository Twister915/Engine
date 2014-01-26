package net.tbnr.gearz.player.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/30/13
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GearzPlayerManager implements Listener {
    private final HashMap<String, GearzPlayer> players = new HashMap<>();
    private static GearzPlayerManager instance;
    private final List<ProxiedPlayer> playersAlreadyConnected;

    public static GearzPlayerManager getInstance() {
        return GearzPlayerManager.instance;
    }

    public GearzPlayerManager() {
        GearzPlayerManager.instance = this;
        this.playersAlreadyConnected = new ArrayList<>();
    }

    public static GearzPlayer getGearzPlayer(ProxiedPlayer player) {
        GearzPlayerManager.getInstance().storePlayer(player);
        return GearzPlayerManager.getInstance().players.get(player.getName());
    }

    public void storePlayer(ProxiedPlayer player) {
        if (player == null) return;
        if (this.players.containsKey(player.getName())) return;
        try {
            this.players.put(player.getName(), new GearzPlayer(player));
        } catch (GearzPlayer.PlayerNotFoundException e) {
            //Ignored
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void removePlayer(PlayerDisconnectEvent event) {
        this.players.remove(event.getPlayer().getName());
        this.playersAlreadyConnected.remove(event.getPlayer());
        if (GearzBungee.getInstance().getConfig().getBoolean("channels.enabled")) {
            GearzPlayer gearzPlayer = GearzPlayerManager.getGearzPlayer(event.getPlayer());
            gearzPlayer.setChannel(null);
        }
    }

    @EventHandler
    public void playerLoginEvent(ServerConnectEvent event) {
        if (!this.playersAlreadyConnected.contains(event.getPlayer())) {
            this.playersAlreadyConnected.add(event.getPlayer());
            ServerInfo aHubServer = GearzBungee.getInstance().getHub().getAHubServer();
            if (aHubServer == null) {
                return;
            }
            event.setTarget(aHubServer);
            if (GearzBungee.getInstance().getConfig().getBoolean("channels.enabled")) {
                GearzPlayer gearzPlayer = GearzPlayerManager.getGearzPlayer(event.getPlayer());
                gearzPlayer.setChannel(GearzBungee.getInstance().getChannelManager().getDefaultChannel());
            }
        }
    }

    public List<ProxiedPlayer> getMatchedPlayers(String start) {
        List<ProxiedPlayer> palyerList = new ArrayList<>();
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.getName().toLowerCase().equals(start.toLowerCase())) {
                List<ProxiedPlayer> needleMatch = new ArrayList<>();
                needleMatch.add(proxiedPlayer);
                return needleMatch;
            }
            if (proxiedPlayer.getName().toLowerCase().startsWith(start.toLowerCase())) {
                palyerList.add(proxiedPlayer);
            }
        }
        return palyerList;
    }
}
