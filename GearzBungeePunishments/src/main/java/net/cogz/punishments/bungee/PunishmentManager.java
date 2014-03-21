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
            boolean ipBanned = isIpBanned(event.getConnection().getAddress().getHostName());
            if (ipBanned) {
                Punishment punishment = getValidIpBan(event.getConnection().getAddress().getHostName());
                event.getConnection().disconnect(GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<issuer>", punishment.issuer}));
                cleanUpPunishmentMap(event.getConnection().getAddress().getHostName());
                return;
            }
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
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer == null) return;
        if (punishment.getPunishmentType() == PunishmentType.KICK) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("kick-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.TEMP_BAN) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("temp-reason", false, true, new String[]{"<reason>", punishment.reason}, new String[]{"<date>", longReadable.format(punishment.end)}), punishment.issuer);
        } else if (punishment.getPunishmentType() == PunishmentType.PERMANENT_BAN) {
            formatKickPlayer(punishment.punished, GearzBungeePunishments.getInstance().getFormat("ban-reason", false, true, new String[]{"<reason>", punishment.reason}), punishment.issuer);
        }
    }

    public void formatKickPlayer(String player, String reason, String issuer) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer == null) return;
        proxiedPlayer.disconnect(GearzBungeePunishments.getInstance().getFormat("kick", false, true, new String[]{"<reason>", reason}, new String[]{"<issuer>", issuer}));
    }
}
