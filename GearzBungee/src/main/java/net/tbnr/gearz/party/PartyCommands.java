package net.tbnr.gearz.party;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created by Jake on 1/26/14.
 */
public class PartyCommands implements TCommandHandler {
    @TCommand(
            name = "party",
            aliases = {"prty"},
            usage = "/party <args...>",
            permission = "gearz.party",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus command(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.FEW_ARGS;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String cmd = args[0];
        PartyHandler partyHandler = GearzBungee.getInstance().getPartyHandler();
        switch (cmd) {
            case "create":
            case "new":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                if (partyHandler.create(player) == null) {
                    break;
                } else {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-create"));
                }
                break;
            case "disband":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                Party party = partyHandler.getPartyFromPlayer(player);
                if (party == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-no-have"));
                    break;
                }
                party.disband();
                party.sendMessage(GearzBungee.getInstance().getFormat("party-disband"));
                break;
            case "leave":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                Party prty = partyHandler.getPartyFromPlayer(player);
                if (prty == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-no-have"));
                    break;
                }
                prty.leave(player);
                player.sendMessage(GearzBungee.getInstance().getFormat("party-left"));
                break;
            case "invite":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer trgt = ProxyServer.getInstance().getPlayer(args[1]);
                if (trgt == null) {
                    player.sendMessage(GearzBungee.getInstance().getFormat("party-bad-player"));
                    break;
                }
                Party pty = partyHandler.getPartyFromPlayer(player);
                if (pty == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-no-have"));
                    break;
                }
                pty.invite(trgt);
                break;
            case "kick":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(GearzBungee.getInstance().getFormat("party-nohave-player"));
                    break;
                }
                Party pry = partyHandler.getPartyFromPlayer(player);
                if (pry == null || !pry.getMembers().contains(target)) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-no-have"));
                    break;
                }
                pry.kick(target, true);
                player.sendMessage(GearzBungee.getInstance().getFormat("party-kick"));
                break;
            case "join":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if (targetPlayer == null) {
                    player.sendMessage(GearzBungee.getInstance().getFormat("party-bad-player"));
                    break;
                }
                Party partyToJoin = partyHandler.getPartyFromPlayer(targetPlayer);
                if (partyToJoin == null) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-player-nohave"));
                    break;
                }
                if (!partyHandler.hasInviteFor(player, partyToJoin)) {
                    sender.sendMessage(GearzBungee.getInstance().getFormat("party-need-invite"));
                    break;
                }
                player.sendMessage(GearzBungee.getInstance().getFormat("party-join"));
            case "help":
                player.sendMessage(GearzBungee.getInstance().getFormat("party-help"));
                break;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
