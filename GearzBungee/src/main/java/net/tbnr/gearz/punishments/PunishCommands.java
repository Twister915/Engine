package net.tbnr.gearz.punishments;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.modules.PlayerInfoModule;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.gearz.player.bungee.GearzPlayerManager;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jake on 1/4/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@SuppressWarnings("deprecation")
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

        String reason = compile(args, 1, args.length).trim();
        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.PERMANENT_BAN, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.PERMANENT_BAN, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.PERMANENT_BAN, reason);
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
        Date checkAgainst = new Date();
        Long duration = parseTime(length);
        if (duration - checkAgainst.getTime() == 1000) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("bad-timestamp", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        Date end = new Date();
        end.setTime(duration);
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.TEMP_BAN, end, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.TEMP_BAN, end, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getName(), PunishmentType.TEMP_BAN, reason);
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
        broadcastPunishment(target.getServer().getInfo(), sender.getName(), target.getName(), PunishmentType.KICK, reason);
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
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.WARN, reason);
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
        if (args.length < 2) return TCommandStatus.INVALID_ARGS;

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
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.MUTE, reason);
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @TCommand(
            aliases = {"gtempmute", "tmute"},
            name = "ggtempmute",
            usage = "/tempmute <player> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.mute")
    @SuppressWarnings("unused")
    public TCommandStatus tempMute(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 3) return TCommandStatus.INVALID_ARGS;

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
        Date checkAgainst = new Date();
        Long duration = parseTime(length);
        if (duration - checkAgainst.getTime() == 1000) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("bad-timestamp", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        Date end = new Date();
        end.setTime(duration);
        if (type.equals(TCommandSender.Console)) {
            gearzTarget.punishPlayer(reason, null, PunishmentType.TEMP_MUTE, end, true);
        } else {
            gearzTarget.punishPlayer(reason, GearzPlayerManager.getGearzPlayer((ProxiedPlayer) sender), PunishmentType.TEMP_MUTE, end, false);
        }

        sender.sendMessage(GearzBungee.getInstance().getFormat("muted-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("temp-muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}, new String[]{"<end>", longReadable.format(end)}));
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.TEMP_MUTE, reason);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "gipban",
            aliases = {"gbanip"},
            usage = "/ipban <ip> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.ipban")
    @SuppressWarnings("unused")
    public TCommandStatus ipban(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) return TCommandStatus.INVALID_ARGS;

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

    public long parseTime(String time) {
        long timeReturn;
        try {
            timeReturn = parseDateDiff(time, true);
        } catch (Exception e) {
            timeReturn = 0;
        }
        return timeReturn;
    }

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0,
            months = 0,
            weeks = 0,
            days = 0,
            hours = 0,
            minutes = 0,
            seconds = 0;

        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty())
                    years = Integer.parseInt(m.group(1));
                if (m.group(2) != null && !m.group(2).isEmpty())
                    months = Integer.parseInt(m.group(2));
                if (m.group(3) != null && !m.group(3).isEmpty())
                    weeks = Integer.parseInt(m.group(3));
                if (m.group(4) != null && !m.group(4).isEmpty())
                    days = Integer.parseInt(m.group(4));
                if (m.group(5) != null && !m.group(5).isEmpty())
                    hours = Integer.parseInt(m.group(5));
                if (m.group(6) != null && !m.group(6).isEmpty())
                    minutes = Integer.parseInt(m.group(6));
                if (m.group(7) != null && !m.group(7).isEmpty())
                    seconds = Integer.parseInt(m.group(7));
                break;
            }
        }
        if (!found)
            throw new Exception("Illegal Date");

        if (years > 20)
            throw new Exception("Illegal Date");

        Calendar c = new GregorianCalendar();
        if (years > 0)
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        if (months > 0)
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        if (weeks > 0)
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        if (days > 0)
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        if (hours > 0)
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        if (minutes > 0)
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        if (seconds > 0)
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        return c.getTimeInMillis();
    }

    public void broadcastPunishment(ServerInfo server, String issuer, String target, PunishmentType punishmentType, String reason) {
        for (ProxiedPlayer proxiedPlayer : server.getPlayers()) {
            proxiedPlayer.sendMessage(GearzBungee.getInstance().getFormat("punish-broadcast", false, false, new String[]{"<server>", PlayerInfoModule.getServerForBungee(server).getGame()}, new String[]{"<issuer>", issuer}, new String[]{"<target>", target}, new String[]{"<action>", punishmentType.getAction()}, new String[]{"<reason>", reason}));
        }
    }
}
