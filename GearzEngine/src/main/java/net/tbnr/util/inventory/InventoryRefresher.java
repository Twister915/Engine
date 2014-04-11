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
import net.tbnr.util.inventory.base.GUIItem;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

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
                allServerTypes.put(serverSelector.getGameType(), getServersForSelector(serverSelector.getGameType()));
            }
            for (ServerSelector serverSelector : serverSelectors) {
                serverSelector.updateContents(getServerItems(InventoryRefresher.getServersForSelector(serverSelector.getGameType())));
            }
        }
    }

    public static List<Server> getServersForSelector(String gameType) {
        return ServerManager.getServersWithGame(gameType);
    }

    public static ArrayList<GUIItem> getServerItems(List<Server> servers) {
        ArrayList<GUIItem> items = new ArrayList<>();
        for (Server aServer : servers) {
            items.add(itemForServer(aServer));
        }
        return items;
    }

    public static GUIItem itemForServer(Server server) {
        DyeColor color = null;

        String status = server.getStatusString();
        switch (status) {
            case "lobby":
                color = DyeColor.LIME;
                break;
            case "spectate":
                color = DyeColor.YELLOW;
                break;
            case "load_lobby":
            case "load-map":
            case "game-over":
                color = DyeColor.RED;
                break;
        }

        Wool wool = new Wool(color);
        ItemStack itemStack = wool.toItemStack(1);

        String serverName = Gearz.getInstance().getFormat("formats.selector-name", true, new String[]{"<game>", server.getGame()}, new String[]{"<number>", server.getNumber().toString()});

        itemStack.setAmount(server.getPlayerCount() == null ? 1 : Math.max(1, server.getPlayerCount()));
        return new GUIItem(itemStack, serverName);
    }
}
