package net.tbnr.gearz.network;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.player.GearzPlayer;
import net.tbnr.util.player.TPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class GearzPlayerProvider<PlayerType extends GearzPlayer> {
    private Map<TPlayer, PlayerType> players = new HashMap<>();

    public PlayerType getPlayerFromPlayer(Player player) {
        return getPlayerFromTPlayer(Gearz.getInstance().getPlayerManager().getPlayer(player));
    }

    public PlayerType getPlayerFromTPlayer(TPlayer player) {
        return this.players.containsKey(player) ? this.players.get(player) : newInstanceFor(player);
    }

    public void removePlayer(Player player) {
        removePlayer(getPlayerFromPlayer(player));
    }

    public void removePlayer(TPlayer player) {
        this.players.remove(player);
    }

    public void removePlayer(PlayerType player) {
        removePlayer(player.getTPlayer());
    }

    protected abstract PlayerType newInstanceFor(TPlayer player);
}