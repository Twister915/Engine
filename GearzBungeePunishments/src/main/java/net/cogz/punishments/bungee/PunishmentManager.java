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

package net.cogz.punishments.bungee;

import com.mongodb.DB;
import net.cogz.punishments.GearzPunishments;
import net.cogz.punishments.Punishment;
import net.cogz.punishments.PunishmentType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;

import java.text.SimpleDateFormat;

/**
 * Bungee Side Permissions Manager
 */

//todo punisher from uuid
public class PunishmentManager extends GearzPunishments implements Listener {
    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ChatEvent event) {
        if (event.isCommand()) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        boolean muted = onChat(player.getName());
        if (muted) {
            Punishment mute = getLocalMute(player.getName());
            if (mute == null) {
                return;
            }
            if (mute.getPunishmentType() == PunishmentType.MUTE) {
                player.sendMessage(GearzBungeePunishments.getInstance().getFormat("muted", false, false, new String[]{"<reason>", mute.reason}, new String[]{"<issuer>", mute.issuer}));
            } else if (mute.getPunishmentType() == PunishmentType.TEMP_MUTE) {
                player.sendMessage(GearzBungeePunishments.getInstance().getFormat("temp-muted", false, false, new String[]{"<reason>", mute.reason}, new String[]{"<issuer>", mute.issuer}, new String[]{"<end>", longReadable.format(mute.end)}));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PreLoginEvent event) {
        String ipAddress = event.getConnection().getAddress().getHostName();
        boolean ipBanned = isLocalIpBanned(ipAddress);
        if (ipBanned) {
            Punishment punishment = getValidLocalIpBan(ipAddress);
            event.getConnection().disconnect(GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<issuer>", punishment.issuer}));
            return;
        }
        String player = event.getConnection().getName();
        boolean banned = onJoin(player);
        if (banned) {
            Punishment punishment = getValidBan(player);
            PunishmentType punishmentType = punishment.getPunishmentType();
            if (punishmentType == PunishmentType.PERMANENT_BAN) {
                event.getConnection().disconnect(GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}));
            } else if (punishmentType == PunishmentType.TEMP_BAN) {
                event.getConnection().disconnect(GearzBungeePunishments.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<date>", longReadable.format(punishment.end)}));
            }
            cleanUpPunishmentMap(player);
        } else {
            loadMute(player);
            cleanUpPunishmentMap(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        onQuit(event.getPlayer().getName());
    }

    @Override
    public DB getDB() {
        return GearzBungee.getInstance().getMongoDB();
    }

    @Override
    public void kickPlayer(String player, Punishment punishment) {
        ProxiedPlayer proxiedPlayer = getPlayerByUUID(player);
        if (proxiedPlayer == null) return;
        if (punishment.getPunishmentType() == PunishmentType.KICK) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("kick-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.TEMP_BAN) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<date>", longReadable.format(punishment.end)}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.PERMANENT_BAN) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        }
    }

    private ProxiedPlayer getPlayerByUUID(String uuid) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            System.out.println("Found UUID: " + player.getUniqueId().toString());
            System.out.println("Was looking for: " + uuid);
            if (player.getUniqueId().toString().equals(uuid)) return player;
        }
        return null;
    }

    private void formatKickPlayer(String player, String reason, String issuer) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer == null) return;
        proxiedPlayer.disconnect(GearzBungeePunishments.getInstance().getFormat("kick", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
    }
}
