package net.cogz.punishments.bungee;

import net.cogz.punishments.PunishmentType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.modules.PlayerInfoModule;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jake on 3/6/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class PunishmentCommands implements TCommandHandler {
    private PunishmentManager manager;

    public PunishmentCommands(PunishmentManager manager) {
        this.manager = manager;
    }

    @TCommand(
            aliases = "gban",
            name = "ban",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (manager.isPlayerBanned(gearzTarget.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("already-banned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        manager.punishPlayer(gearzTarget.getName(), sender.getName(), reason, PunishmentType.PERMANENT_BAN, null);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.PERMANENT_BAN, reason);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = "gtempban",
            name = "tempban",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (manager.isPlayerBanned(gearzTarget.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("already-banned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 2, args.length).trim();
        String length = args[1];
        Date checkAgainst = new Date();
        Long duration = parseTime(length);
        if (duration - checkAgainst.getTime() == 1000) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("bad-timestamp", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        Date end = new Date();
        end.setTime(duration);
        manager.punishPlayer(gearzTarget.getName(), sender.getName(), reason, PunishmentType.TEMP_BAN, end);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("banned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getName(), PunishmentType.TEMP_BAN, reason);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gkick"},
            name = "kick",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        ProxiedPlayer target = matchedPlayers.get(0);

        String reason = compile(args, 1, args.length).trim();
        manager.punishPlayer(target.getName(), sender.getName(), reason, PunishmentType.KICK, null);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("kicked-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", target.getName()}));
        broadcastPunishment(target.getServer().getInfo(), sender.getName(), target.getName(), PunishmentType.KICK, reason);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gwarn"},
            name = "warn",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 1, args.length).trim();
        manager.punishPlayer(gearzTarget.getName(), sender.getName(), reason, PunishmentType.WARN, null);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("warned-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.WARN, reason);
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungeePunishments.getInstance().getFormat("warned-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gmute"},
            name = "mute",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (manager.isPlayerMuted(gearzTarget.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("already-muted", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 1, args.length).trim();
        manager.punishPlayer(gearzTarget.getName(), sender.getName(), reason, PunishmentType.MUTE, null);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("muted-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.MUTE, reason);
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungeePunishments.getInstance().getFormat("muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    public final SimpleDateFormat longReadable = new SimpleDateFormat("MM/dd/yyyy hh:mm zzzz");

    @TCommand(
            aliases = {"gtempmute", "tempmute"},
            name = "tempmute",
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
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (manager.isPlayerMuted(gearzTarget.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("already-muted", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String reason = compile(args, 2, args.length).trim();
        String length = args[1];
        Date checkAgainst = new Date();
        Long duration = parseTime(length);
        if (duration - checkAgainst.getTime() == 1000) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("bad-timestamp", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        Date end = new Date();
        end.setTime(duration);
        manager.punishPlayer(gearzTarget.getName(), sender.getName(), reason, PunishmentType.TEMP_MUTE, end);

        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("muted-player", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", gearzTarget.getName()}));
        if (gearzTarget.getProxiedPlayer() == null) return TCommandStatus.SUCCESSFUL;
        gearzTarget.getProxiedPlayer().sendMessage(GearzBungee.getInstance().getFormat("temp-muted-for", false, false, new String[]{"<reason>", reason}, new String[]{"<issuer>", sender.getName()}, new String[]{"<end>", longReadable.format(end)}));
        broadcastPunishment(gearzTarget.getProxiedPlayer().getServer().getInfo(), sender.getName(), gearzTarget.getProxiedPlayer().getName(), PunishmentType.TEMP_MUTE, reason);
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "ipban",
            aliases = {"banip"},
            usage = "/ipban <ip> <reason...>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.ipban")
    @SuppressWarnings("unused")
    public TCommandStatus ipban(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length < 2) return TCommandStatus.INVALID_ARGS;

        String ip = args[0];
        String reason = compile(args, 1, args.length).trim();
        if (manager.isIpBanned(ip)) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("already-ipbanned", false, true));
            return TCommandStatus.SUCCESSFUL;
        }
        manager.punishPlayer(ip, sender.getName(), reason, PunishmentType.IP_BAN, null);
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("banned-ip", false, true, new String[]{"<reason>", reason}, new String[]{"<target>", ip}));
        return TCommandStatus.SUCCESSFUL;
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
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
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
            if (proxiedPlayer.getServer() == null) continue;
            proxiedPlayer.sendMessage(GearzBungeePunishments.getInstance().getFormat("punish-broadcast", false, false, new String[]{"<server>", PlayerInfoModule.getServerForBungee(server).getGame().toUpperCase()}, new String[]{"<issuer>", issuer}, new String[]{"<target>", target}, new String[]{"<action>", punishmentType.getAction()}, new String[]{"<reason>", reason}));
            proxiedPlayer.sendMessage(GearzBungee.getInstance().getFormat("punish-broadcast", false, false, new String[]{"<server>", PlayerInfoModule.getServerForBungee(server).getGame()}, new String[]{"<issuer>", issuer}, new String[]{"<target>", target}, new String[]{"<action>", punishmentType.getAction()}, new String[]{"<reason>", reason}));
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
