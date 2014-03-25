package net.cogz.punishments.bungee;

import net.cogz.punishments.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Commands to remove punishments for players
 */
public class UnPunishCommands implements TCommandHandler {
    private PunishmentManager manager;
    public final SimpleDateFormat readable = new SimpleDateFormat("MM/dd/yyyy");

    public UnPunishCommands(PunishmentManager manager) {
        this.manager = manager;
    }

    @TCommand(
            aliases = {"l", "search", "check"},
            name = "lookup",
            usage = "/lookup <player>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.lookup")
    @SuppressWarnings("unused")
    public TCommandStatus lookup(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length != 1) return TCommandStatus.INVALID_ARGS;
        if (args[0].matches(".*([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}).*")) {
            if (!manager.isIpBanned(args[0])) {
                sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("no-punishment", false, false));
                return TCommandStatus.SUCCESSFUL;
            }
            Punishment punishment = manager.getValidIpBan(args[0]);
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("lookup-format", false, false, new String[]{"<date>", readable.format(punishment.end)}, new String[]{"<reason>", punishment.reason}, new String[]{"<action>", punishment.getPunishmentType().getAction()}, new String[]{"<issuer>", punishment.issuer}));
        }

        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(args[0]);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        List<Punishment> punishments = manager.getPunishmentsByPlayer(gearzTarget.getName(), true);
        if (punishments == null) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("no-punishment", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("lookup-header", false, false, new String[]{"<player>", gearzTarget.getName()}));
        int x = 0;
        for (Punishment punishment : punishments) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("lookup-format", false, false, new String[]{"<date>", readable.format(punishment.end)}, new String[]{"<reason>", punishment.reason}, new String[]{"<action>", punishment.getPunishmentType().getAction()}, new String[]{"<issuer>", punishment.issuer}, new String[]{"<id>", x + ""}));
            x++;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gunban"},
            name = "unban",
            usage = "/unban <player>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.unban")
    @SuppressWarnings("unused")
    public TCommandStatus unban(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length != 1) return TCommandStatus.INVALID_ARGS;

        String target = args[0];
        GearzPlayer gearzPlayer;
        try {
            gearzPlayer = new GearzPlayer(target);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (!manager.isPlayerBanned(gearzPlayer.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("not-banned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        manager.unBan(gearzPlayer.getName());
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("unbanned-player", false, false, new String[]{"<player>", target}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"gunmute"},
            name = "unmute",
            usage = "/unmute <player>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.unmute")
    @SuppressWarnings("unused")
    public TCommandStatus unmute(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length != 1) {
            return TCommandStatus.INVALID_ARGS;
        }

        String target = args[0];
        GearzPlayer gearzPlayer;
        try {
            gearzPlayer = new GearzPlayer(target);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-player", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (!manager.isPlayerMuted(gearzPlayer.getName())) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("not-muted", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        manager.unMute(gearzPlayer.getName());
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("unmuted-player", false, false, new String[]{"<player>", target}));

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"ipunban", "unipban"},
            name = "unbanip",
            usage = "/unbanip <ip>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.unbanip")
    @SuppressWarnings("unused")
    public TCommandStatus unbanip(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length != 1) {
            return TCommandStatus.INVALID_ARGS;
        }

        String ip = args[0];
        if (!manager.isIpBanned(ip)) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("not-ipbanned", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        manager.unIpBan(ip);
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("unbanned-ip", false, false, new String[]{"<target>", ip}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            aliases = {"a"},
            name = "appeal",
            usage = "/appeal <player> <id>",
            senders = {TCommandSender.Player, TCommandSender.Console},
            permission = "gearz.punish.appeal")
    @SuppressWarnings("unused")
    public TCommandStatus appeal(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        if (args.length != 2) return TCommandStatus.INVALID_ARGS;

        String target = args[0];
        Integer id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return TCommandStatus.INVALID_ARGS;
        }

        GearzPlayer gearzTarget;
        try {
            gearzTarget = new GearzPlayer(target);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            return TCommandStatus.INVALID_ARGS;
        }

        List<Punishment> punishments = manager.getPunishmentsByPlayer(gearzTarget.getName(), true);
        if (punishments == null || punishments.size() == 0) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-punishment", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        Punishment toAppeal;
        try {
            toAppeal = punishments.get(id);
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("null-punishment", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        manager.appealPunishment(toAppeal);
        ProxyServer.getInstance().getLogger().info(sender.getName() + " appealed " + target + "'s " + id + " punishment.");
        sender.sendMessage(GearzBungeePunishments.getInstance().getFormat("appeal-punishment", false, false, new String[]{"<target>", target}));
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
