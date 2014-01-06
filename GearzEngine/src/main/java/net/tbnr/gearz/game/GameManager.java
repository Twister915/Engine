package net.tbnr.gearz.game;

import net.tbnr.gearz.GearzPlugin;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.event.Listener;

/**
 * Move GameManager into an interface to support multiple types of GameManagers.
 */
public interface GameManager extends Listener {
    public GameMeta getGameMeta();

    public GearzPlugin getPlugin();

    public void beginGame(Integer id) throws GameStartException;

    void gameEnded(GearzGame game);

    public void spawn(GearzPlayer player);

    @SuppressWarnings("unused")
    public void disable();
}
