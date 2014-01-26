package net.cogz.parties.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.chat.Filter;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Commands for players to manage their parties with
 */
public class PartyCommands implements TCommandHandler {
    @TCommand(
            name = "party",
            aliases = {"prty"},
            usage = "/party <args...>",
            permission = "gearz.party",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus party(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.FEW_ARGS;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        String cmd = args[0];
        PartyHandler partyHandler = GearzBungeeParties.getInstance().getPartyHandler();
        switch (cmd) {
            case "create":
            case "new":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                if (partyHandler.create(player) == null) {
                    break;
                } else {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-create", false));
                }
                break;
            case "disband":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                Party party = partyHandler.getPartyFromPlayer(player);
                if (party == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                if (!party.getCreator().getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-not-leader", false));
                    break;
                }
                party.disband();
                party.sendMessage(GearzBungeeParties.getInstance().getFormat("party-disband", false));
                break;
            case "leave":
                if (args.length != 1) return TCommandStatus.INVALID_ARGS;
                Party prty = partyHandler.getPartyFromPlayer(player);
                if (prty == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                if (prty.getCreator().getName().equals(sender.getName())) {
                    prty.disband();
                    prty.sendMessage(GearzBungeeParties.getInstance().getFormat("party-disband", false));
                    break;
                }
                prty.leave(player);
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-left", false));
                break;
            case "invite":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer trgt = ProxyServer.getInstance().getPlayer(args[1]);
                if (trgt == null) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-bad-player", false));
                    break;
                }
                if (trgt.getName().equals(sender.getName())) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                Party pty = partyHandler.getPartyFromPlayer(player);
                if (pty == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                if (pty.getMembers().contains(trgt)) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-already", false));
                    break;
                }
                if (!pty.getCreator().getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-not-leader", false));
                    break;
                }
                pty.invite(trgt);
                break;
            case "kick":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-nohave-player", false));
                    break;
                }
                if (target.getName().equals(sender.getName())) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                Party pry = partyHandler.getPartyFromPlayer(player);
                if (pry == null || !pry.getMembers().contains(target)) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                if (!pry.getCreator().getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-not-leader", false));
                    break;
                }
                pry.kick(target, true);
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-kick", false));
                break;
            case "join":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if (targetPlayer == null) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-bad-player", false));
                    break;
                }
                if (targetPlayer.getName().equals(sender.getName())) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                Party partyToJoin = partyHandler.getPartyFromPlayer(targetPlayer);
                if (partyToJoin == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-player-nohave", false));
                    break;
                }
                if (!partyHandler.hasInviteFor(player, partyToJoin)) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-need-invite", false));
                    break;
                }
                partyToJoin.join((ProxiedPlayer) sender);
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-join", false));
                break;
            case "chat":
                if (args.length < 1) return TCommandStatus.INVALID_ARGS;
                String message = GearzBungee.getInstance().compile(args, 1, args.length);
                Party pr = partyHandler.getPartyFromPlayer(player);
                if (pr == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                Filter.FilterData filterData = Filter.filter(message, (ProxiedPlayer) sender);
                if (filterData.isCancelled()) return TCommandStatus.SUCCESSFUL;
                pr.sendMessage(GearzBungeeParties.getInstance().getFormat("party-chat", false, false, new String[]{"<message>", filterData.getMessage()}, new String[]{"<player>", ((ProxiedPlayer) sender).getDisplayName()}));
                break;
            case "help":
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-help", false));
                break;
            case "list":
                Party partyToList = partyHandler.getPartyFromPlayer((ProxiedPlayer) sender);
                if (partyToList == null) {
                    sender.sendMessage(GearzBungeeParties.getInstance().getFormat("party-no-have", false));
                    break;
                }
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-header", false));
                player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-entry", false, false, new String[]{"<player>", partyToList.getCreator().getName()}));
                for (ProxiedPlayer member : partyToList.getMembers()) {
                    player.sendMessage(GearzBungeeParties.getInstance().getFormat("party-entry", false, false, new String[]{"<player>", member.getName()}));
                }
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
