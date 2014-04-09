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
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;

import java.text.SimpleDateFormat;
import java.util.UUID;

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
        boolean muted = onChat(player.getUniqueId().toString());
        if (muted) {
            Punishment mute = getLocalMute(player.getUniqueId().toString());
            if (mute == null) {
                return;
            }
            if (mute.getPunishmentType() == PunishmentType.MUTE) {
                player.sendMessage(GearzBungeePunishments.getInstance().getFormat("muted", false, false, new String[]{"<reason>", mute.reason}, new String[]{"<issuer>", punisherFromUUID(mute.issuer)}));
            } else if (mute.getPunishmentType() == PunishmentType.TEMP_MUTE) {
                player.sendMessage(GearzBungeePunishments.getInstance().getFormat("temp-muted", false, false, new String[]{"<reason>", mute.reason}, new String[]{"<issuer>", punisherFromUUID(mute.issuer)}, new String[]{"<end>", longReadable.format(mute.end)}));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PostLoginEvent event) {
        String ipAddress = event.getPlayer().getAddress().getHostName();
        boolean ipBanned = isLocalIpBanned(ipAddress);
        if (ipBanned) {
            Punishment punishment = getValidLocalIpBan(ipAddress);
            event.getPlayer().disconnect(GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<issuer>", punisherFromUUID(punishment.issuer)}));
            return;
        }
        String uuid = event.getPlayer().getUniqueId().toString();
        boolean banned = onJoin(uuid);
        if (banned) {
            Punishment punishment = getValidBan(uuid);
            PunishmentType punishmentType = punishment.getPunishmentType();
            if (punishmentType == PunishmentType.PERMANENT_BAN) {
                event.getPlayer().disconnect(GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}));
            } else if (punishmentType == PunishmentType.TEMP_BAN) {
                event.getPlayer().disconnect(GearzBungeePunishments.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<date>", longReadable.format(punishment.end)}));
            }
            cleanUpPunishmentMap(uuid);
        } else {
            loadMute(uuid);
            cleanUpPunishmentMap(uuid);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        onQuit(event.getPlayer().getUniqueId().toString());
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
            formatKickPlayer(proxiedPlayer, GearzBungeePunishments.getInstance().getFormat("kick-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.TEMP_BAN) {
            formatKickPlayer(proxiedPlayer, GearzBungeePunishments.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<date>", longReadable.format(punishment.end)}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.PERMANENT_BAN) {
            formatKickPlayer(proxiedPlayer, GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        }
    }

    private ProxiedPlayer getPlayerByUUID(String uuid) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getUniqueId().toString().equals(uuid)) return player;
        }
        return null;
    }

    private void formatKickPlayer(ProxiedPlayer player, String reason, String issuer) {
        if (player == null) return;
        player.disconnect(GearzBungeePunishments.getInstance().getFormat("kick", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", punisherFromUUID(issuer)}));
    }

    public String punisherFromUUID(String uuid) {
        if (uuid.equals("CONSOLE")) {
            return uuid;
        }
        try {
            GearzPlayer player = new GearzPlayer(uuid, true);
            return player.getUsername();
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return uuid;
        }
    }
}
