/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util.inventory;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.ServerSelector;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jake on 12/28/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
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
