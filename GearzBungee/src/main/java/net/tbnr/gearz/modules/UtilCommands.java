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
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created with IntelliJ IDEA.
 * User: George
 * Date: 05/01/13
 * Time: 11:20 AM
 */
public class UtilCommands implements TCommandHandler, Listener {

    @TCommand(name = "about", permission = "gearz.about", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "", aliases = {"plugins", "version", "pl", "?"})
    @SuppressWarnings("unused")
    public TCommandStatus about(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        sender.sendMessage(GearzBungee.getInstance().getFormat("about-prefix", false) + GearzBungee.getInstance().getFormat("about-info", false));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "help", permission = "gearz.help", senders = {TCommandSender.Player}, usage = "")
    @SuppressWarnings("unused")
    public TCommandStatus help(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        sender.sendMessage(GearzBungee.getInstance().getFormat("about-prefix", false) + GearzBungee.getInstance().getFormat("about-info", false));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "kickall", permission = "gearz.kickall", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "")
    @SuppressWarnings("unused")
    public TCommandStatus kickall(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length == 0) {
            return TCommandStatus.INVALID_ARGS;
        }

        String msg = GearzBungee.getInstance().compile(args, 1, args.length);
        kickPlayers(msg);

        return TCommandStatus.SUCCESSFUL;
    }

    public void kickPlayers(String message) {
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            proxiedPlayer.disconnect(message);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
