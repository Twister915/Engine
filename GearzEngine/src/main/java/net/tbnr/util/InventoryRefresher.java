package net.tbnr.util;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jake on 12/28/13.
 */
public class InventoryRefresher implements Runnable {
    final List<ServerSelector> serverSelectors = new ArrayList<>();

    public InventoryRefresher() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Gearz.getInstance(), this, 0L, 20L);
    }

    public void add(ServerSelector serverSelector) {
        serverSelectors.add(serverSelector);
    }

    public void remove(ServerSelector serverSelector) {
        serverSelectors.remove(serverSelector);
    }

    @Override
    public void run() {
        synchronized (serverSelectors) {
            if (serverSelectors.size() == 0) {
                return;
            }
            HashMap<String, List<Server>> allServerTypes = new HashMap<>();
            for (ServerSelector serverSelector : serverSelectors) {
                if (allServerTypes.containsKey(serverSelector.getGameType())) {
                    continue;
                }
                allServerTypes.put(serverSelector.getGameType(), getServersForSelector(serverSelector));
            }
            for (ServerSelector serverSelector : serverSelectors) {
                serverSelector.setServers(allServerTypes.get(serverSelector.getGameType()));
                serverSelector.update();
            }
        }
    }

    public static List<Server> getServersForSelector(ServerSelector selector) {
        return ServerManager.getServersWithGame(selector.getGameType());
    }
}
