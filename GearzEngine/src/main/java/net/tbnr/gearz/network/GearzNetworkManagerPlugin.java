package net.tbnr.gearz.network;

import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.TPlugin;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class GearzNetworkManagerPlugin<PlayerType extends GearzPlayer> extends TPlugin implements Listener {
    @Getter protected GearzPlayerProvider<PlayerType> playerProvider;
    protected abstract GearzPlayerProvider<PlayerType> getNewPlayerProvider();

    protected void onPlayerJoin(PlayerType player) {}
    protected void onPlayerDisconnect(PlayerType player) {}


    @Override
    public void enable() {
        GearzPlayerProvider<PlayerType> newPlayerProvider = getNewPlayerProvider();
        this.playerProvider = newPlayerProvider;
        Gearz.getInstance().setPlayerProvider(newPlayerProvider);
        registerEvents(this);
        getLogger().info("Setup Network Plugin!");
    }

    @Override
    public void disable() {
        this.playerProvider = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(TPlayerJoinEvent event) {
        PlayerType playerFromTPlayer = this.playerProvider.getPlayerFromTPlayer(event.getPlayer());
        onPlayerJoin(playerFromTPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(TPlayerDisconnectEvent event) {
        onPlayerDisconnect(this.playerProvider.getPlayerFromTPlayer(event.getPlayer()));
        this.playerProvider.removePlayer(event.getPlayer());
    }
}
