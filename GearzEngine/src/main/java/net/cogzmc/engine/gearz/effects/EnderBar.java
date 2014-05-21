/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.gearz.effects;

import net.cogzmc.engine.gearz.Gearz;
import net.cogzmc.engine.gearz.packets.FakeEntity;
import net.cogzmc.engine.gearz.player.GearzPlayer;
import net.cogzmc.engine.util.player.TPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joey on 12/10/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public final class EnderBar {
    static {
        resetPlayers();
    }

    private static Map<GearzPlayer, EnderBar> playerEnderBarMap;
    private final FakeEntity enderDragon;

    private EnderBar(GearzPlayer player) {
        this.enderDragon = new FakeEntity(player.getPlayer(), EntityType.ENDER_DRAGON, 200, player.getPlayer().getLocation().clone().subtract(0, 100, 0), FakeEntity.EntityFlags.INVISIBLE);
    }

    private static void showDragonFor(GearzPlayer player) {
        if (!player.isValid()) {
            return;
        }
        EnderBar enderBarFor = getEnderBarFor(player);
        enderBarFor.enderDragon.create();
    }

    public static void setTextFor(GearzPlayer player, String string) {
        if (!player.isValid()) {
            return;
        }
        showDragonFor(player);
        EnderBar enderBarFor = getEnderBarFor(player);
        enderBarFor.enderDragon.setCustomName(string.substring(0, Math.min(string.length(), 63)));
    }

    private static EnderBar getEnderBarFor(GearzPlayer player) {
        if (!EnderBar.playerEnderBarMap.containsKey(player)) {
            EnderBar enderBar = new EnderBar(player);
            EnderBar.playerEnderBarMap.put(player, enderBar);
            return enderBar;
        }
        return EnderBar.playerEnderBarMap.get(player);
    }

    private static boolean hasEnderBarFor(GearzPlayer player) {
        return EnderBar.playerEnderBarMap.containsKey(player);
    }

    public static void setHealthPercent(GearzPlayer player, float health) {
        EnderBar enderBarFor = getEnderBarFor(player);
        Integer finHealth = Float.valueOf(health * 200).intValue();
        enderBarFor.enderDragon.setHealth(finHealth);
    }

    public static void resetPlayers() {
        playerEnderBarMap = null;
        playerEnderBarMap = new HashMap<>();
    }

    public static void refreshForPlayer(GearzPlayer player) {
        EnderBar enderBarFor = getEnderBarFor(player);
        enderBarFor.enderDragon.create();
    }

    public static void remove(GearzPlayer player) {
        EnderBar enderBarFor = getEnderBarFor(player);
        enderBarFor.enderDragon.destroy();
        playerEnderBarMap.remove(player);
    }

    public static class EnderBarListeners implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            TPlayer tpLayer = Gearz.getInstance().getPlayerManager().getPlayer(player);
            GearzPlayer gearzPlayer = Gearz.getInstance().getPlayerProvider().getPlayerFromTPlayer(tpLayer);
            if (!hasEnderBarFor(gearzPlayer)) {
                return;
            }
            EnderBar enderBarFor = getEnderBarFor(gearzPlayer);
            Location enderLocation = enderBarFor.enderDragon.getLocation().clone();
            Location playerLocation = player.getLocation().clone();
            enderLocation.setY(0);
            playerLocation.setY(0);
            if (!enderLocation.getWorld().equals(playerLocation.getWorld()) || enderLocation.distance(playerLocation) >= 25d) {
                enderBarFor.enderDragon.setLocation(playerLocation.subtract(0, 100, 0));
            }
        }

        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            final GearzPlayer player = Gearz.getInstance().getPlayerProvider().getPlayerFromPlayer(event.getPlayer());
            if (!hasEnderBarFor(player)) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
                @Override
                public void run() {
                    EnderBar.refreshForPlayer(player);
                }
            }, 2L);
        }
    }
}
