/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
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
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Utility commands such as /help
 * or /kickall that do not fit into
 * other classes.
 *
 * <p>
 * Latest Change: Add kickall command
 * <p>
 *
 * @author George
 * @since 1/5/2013
 */
public class UtilCommands implements TCommandHandler, Listener {

    @TCommand(name = "help",
            permission = "gearz.help",
            senders = {TCommandSender.Player},
            usage = "/help",
            aliases = {"about"})
    @SuppressWarnings("unused")
    public TCommandStatus help(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        sender.sendMessage(GearzBungee.getInstance().getFormat("about-prefix", false) + GearzBungee.getInstance().getFormat("about-info", false));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "kickall",
            permission = "gearz.kickall",
            senders = {TCommandSender.Player, TCommandSender.Console},
            usage = "/kickall <reason>")
    @SuppressWarnings("unused")
    public TCommandStatus kickall(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        String message = "All players kicked.";
        if (args.length > 1) {
            message = GearzBungee.getInstance().compile(args, 1, args.length);
        }
        kickPlayers(message);

        return TCommandStatus.SUCCESSFUL;
    }

    private void kickPlayers(String message) {
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("gearz.staff")) {
                continue;
            }
            proxiedPlayer.disconnect(message);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
