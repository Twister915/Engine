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

package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.player.bungee.GearzPlayer;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Module to send the sender a list of a player's previous usernames.
 * <p>
 * Latest Change: Created module
 * <p>
 *
 * @author Jake
 * @since 3/29/2014
 */
public class PlayerHistoryModule implements TCommandHandler {
    @TCommand(usage = "/history <target>", senders = {TCommandSender.Player, TCommandSender.Console}, permission = "gearz.playerhistory", aliases = {"usernames"}, name = "playerhistory")
    @SuppressWarnings("unused")
    public TCommandStatus playerHistory(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length != 1) {
            return TCommandStatus.INVALID_ARGS;
        }
        GearzPlayer target;
        try {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(args[0]);
            target = new GearzPlayer(proxiedPlayer);
        } catch (GearzPlayer.PlayerNotFoundException e) {
            sender.sendMessage(GearzBungee.getInstance().getFormat("no-matches", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
        sender.sendMessage(GearzBungee.getInstance().getFormat("history-header", false, false, new String[]{"<player>", target.getUsername()}));
        for (String string : target.getUsernameHistory()) {
            if (string.equals(target.getUsername())) {
                sender.sendMessage(GearzBungee.getInstance().getFormat("history-current", false, false, new String[]{"<player>", string}));
            } else {
                sender.sendMessage(GearzBungee.getInstance().getFormat("history-past", false, false, new String[]{"<player>", string}));
            }
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
