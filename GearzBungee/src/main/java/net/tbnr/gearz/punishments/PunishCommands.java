package net.tbnr.gearz.punishments;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.modules.PlayerInfoModule;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by jake on 1/4/14.
 */
public class PunishCommands implements TCommandHandler {
    @TCommand(
            aliases = {"gban"},
            name = "ggban",
            usage = "/ban <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.ban")
    @SuppressWarnings("unused")
    public TCommandStatus ban(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (gearzTarget.getActiveBan() != null) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("already-banned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 1, args.length).trim();
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.PERMANENT_BAN, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.PERMANENT_BAN, true);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getName() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(PlayerInfoModule.getServerForBungee(gearzTarget.getProxiedPlayer().getServer().getInfo()).getGame(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.PERMANENT_BAN);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gtempban"},
            name = "ggtempban",
            usage = "/tempban <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.tempban")
    @SuppressWarnings("unused")
    public TCommandStatus tempBan(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 3) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (gearzTarget.getActiveBan() != null) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("already-banned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 2, args.length).trim();
        String length = args[1];
        int duration = parseTime(length);
        Date end = new Date();
        end.setTime(end.getTime() + duration);
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.TEMP_BAN, end, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.TEMP_BAN, end, true);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getName() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(PlayerInfoModule.getServerForBungee(gearzTarget.getProxiedPlayer().getServer().getInfo()).getGame(), sender.getName(), gearzTarget.getName(), PunishmentType.TEMP_BAN);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gkick"},
            name = "ggkick",
            usage = "/kick <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.kick")
    @SuppressWarnings("unused")
    public TCommandStatus kick(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        List<ProxiedPlayer> matchedPlayers = GearzBungee.getInstance().getPlayerManager().getMatchedPlayers(args[0]);
        if (matchedPlayers.size() < 1) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        ProxiedPlayer target = matchedPlayers.get(0);

        String reason = compile(args, 1, args.length).trim();
        if (type.equals(TCommandSender.Console)) {
            GearzPlayerManager.getGearzPlayer(target).punishPlayer(reason, null, PunishmentType.KICK, true);
        } else {
            GearzPlayerManager.getGearzPlayer(target).punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.KICK, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("kicked-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", target.getName()}));
        broadcastPunishment(PlayerInfoModule.getServerForBungee(target.getServer().getInfo()).getGame(), sender.getName(), target.getName(), PunishmentType.KICK);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gwarn"},
            name = "ggwarn",
            usage = "/warn <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.warn")
    @SuppressWarnings("unused")
    public TCommandStatus warn(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 1, args.length).trim();
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.WARN, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.WARN, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("warned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getName() == null) return TCommandStatus.SUCCESSFUL;
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("warned-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gmute"},
            name = "ggmute",
            usage = "/mute <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.mute")
    @SuppressWarnings("unused")
    public TCommandStatus mute(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (gearzTarget.getActiveMuteData() != null) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("already-muted", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 1, args.length).trim();
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.MUTE, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.MUTE, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("muted-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getName() == null) return TCommandStatus.SUCCESSFUL;
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gtempmute", "tmute"},
            name = "ggtempmute",
            usage = "/tempmute <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.mute")
    @SuppressWarnings("unused")
    public TCommandStatus tempMute(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 3) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (gearzTarget.getActiveMuteData() != null) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("already-muted", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 2, args.length).trim();
        String length = args[1];
        int duration = parseTime(length);
        Date end = new Date();
        end.setTime(end.getTime() + duration);
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.TEMP_MUTE, end, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.TEMP_MUTE, end, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("muted-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getName() == null) return TCommandStatus.SUCCESSFUL;
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "ggipban",
            usage = "/ipban <ip> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.ipban")
    @SuppressWarnings("unused")
    public TCommandStatus ipban(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }

        String ip = args[0];
        String reason = compile(args, 1, args.length).trim();
        if (GearzBungee.getInstance().getIpBanHandler().isBanned(ip)) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("already-ipbanned", false, true));
        }
        if (type.equals(TCommandSender.Console)) {
            GearzBungee.getInstance().getIpBanHandler().add(ip, reason, "CONSOLE");
        } else {
            GearzBungee.getInstance().getIpBanHandler().add(ip, reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender).getPlayerDocument().get("_id").toString());
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("banned-ip", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", ip}));
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static String compile(String[] args, int min, int max) {
        return GearzBungee.getInstance().compile(args, min, max);
    }

    private int parseTime(String time) {
        String[] arr = time.split("");
        String sdur = "0";
        for (String c : arr) {
            try {
                if (Integer.valueOf(c) != null) sdur += c;
            } catch (NumberFormatException e) {
                // ignored
            }
        }
        int duration = Integer.valueOf(sdur);
        if (duration == 0) {
            return 0;
        }

        if (time.contains("s") || time.contains("second")) {
            return duration;
        }
        if (time.contains("m") || time.contains("minute")) {
            duration *= 60;
            return duration;
        }
        if (time.contains("h") || time.contains("hour")) {
            duration *= 60 * 60;
            return duration;
        }
        if (time.contains("d") || time.contains("day")) {
            duration *= 60 * 60 * 24;
            return duration;
        }
        if (time.contains("w") || time.contains("week")) {
            duration *= 60 * 60 * 24 * 7;
            return duration;
        }
        if (time.contains("month")) {
            duration *= 60 * 60 * 24 * 31;
            return duration;
        }
        if (time.contains("y") || time.contains("year")) {
            duration *= 60 * 60 * 24 * 365;
            return duration;
        }
        return 0;
    }

    public void broadcastPunishment(String server, String issuer, String target, PunishmentType punishmentType) {
        synchronized (GearzBungee.getInstance().getListModule().getStaff()) {
            for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                proxiedPlayer.sendMessage(GearzBungee.getInstance().getFormat("punish-broadcast", false, false, new String[]{"<server>", server}, new String[]{"<issuer>", issuer}, new String[]{"<target>", target}, new String[]{"<action>", punishmentType.getAction()}));
            }
        }
    }
}
