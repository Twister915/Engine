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

package net.tbnr.gearz.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Jake on 1/29/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ClearChat implements TCommandHandler {
    @TCommand(
            name = "clearchat",
            usage = "/clearchat",
            permission = "gearz.clearchat.all",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus clearchat(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        for (int i = 0; i <= 200; i++) {
            silentBroadcast("", player.getServer().getInfo());
        }
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", player.getServer().getInfo());
        silentBroadcast(ChatColor.DARK_AQUA + "\u25BA" + ChatColor.RESET + "" + ChatColor.BOLD + " The chat has been cleared by a staff member", player.getServer().getInfo());
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", player.getServer().getInfo());
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "clearmychat",
            usage = "/clearmychat",
            permission = "gearz.clearchat.own",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused")
    public TCommandStatus clearmychat(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        for (int i = 0; i <= 200; i++) {
            sender.sendMessage("");
        }
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    private void silentBroadcast(String message, ServerInfo server) {
        for (ProxiedPlayer p : server.getPlayers()) {
            if (p.hasPermission("gearz.clearchat.bypass")) continue;
            p.sendMessage(message);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
