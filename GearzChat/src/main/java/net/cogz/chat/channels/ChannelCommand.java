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

package net.cogz.chat.channels;

import net.cogz.chat.GearzChat;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands to manage a player's channel
 * including listing and switching between
 * channels.
 *
 * <p>
 * Latest Change: Rewrite for Bukkit
 * <p>
 *
 * @author Jake0oo0
 * @since 1/18/2014
 */
public final class ChannelCommand implements TCommandHandler {

    @TCommand(name = "channel", usage = "/channel <channel>", permission = "gearz.channels.command.switch", senders = {TCommandSender.Player}, description = "Allows the user to switch channels.")
    @SuppressWarnings("unused")
    public TCommandStatus channel(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length != 1)  return TCommandStatus.INVALID_ARGS;

        Channel channel = GearzChat.getInstance().getChannelManager().getChannelByName(args[0].toLowerCase());
        if (channel == null) {
            return TCommandStatus.INVALID_ARGS;
        }

        if (channel.hasPermission() && !sender.hasPermission(channel.getPermission())) {
            return TCommandStatus.PERMISSIONS;
        }
        GearzChat.getInstance().getChannelManager().setChannel((Player) sender, channel);
        sender.sendMessage(GearzChat.getInstance().getFormat("formats.switched", false, new String[]{"<channel>", channel.getName()}));
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "channels", usage = "/channels", permission = "gearz.channels.command.list", senders = {TCommandSender.Player}, description = "Lists all currently regsistered channels.")
    @SuppressWarnings("unused")
    public TCommandStatus channels(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length != 0) {
            return TCommandStatus.INVALID_ARGS;
        }

        sender.sendMessage(GearzChat.getInstance().getFormat("formats.channels", false));
        for (Channel channel : GearzChat.getInstance().getChannelManager().getChannels()) {
            if (channel.hasPermission() && !sender.hasPermission(channel.getPermission())) continue;
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.channel", false, new String[]{"<channel>", channel.getName()}));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "modbroadcast", usage = "/mb <message>", permission = "gearz.modbroadcast", senders = {TCommandSender.Player}, description = "Switches the player to the staff channel.", aliases = {"mb"})
    @SuppressWarnings("unused")
    public TCommandStatus mb(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 0) {
            Channel channel = GearzChat.getInstance().getChannelManager().getChannelByName("staff");
            if (channel == null) {
                return TCommandStatus.INVALID_ARGS;
            }
            try {
                GearzChat.getInstance().getChannelManager().setChannel((Player) sender, channel);
            } catch (IllegalStateException e) {
                sender.sendMessage(GearzChat.getInstance().getFormat("formats.already-on-channel"));
            }
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.switched", false, new String[]{"<channel>", channel.getName()}));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "default", usage = "/default <message>", permission = "", senders = {TCommandSender.Player}, description = "Switches the player to the default channel.", aliases = {"d"})
    @SuppressWarnings("unused")
    public TCommandStatus def(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 0) {
            Channel channel = GearzChat.getInstance().getChannelManager().getChannelByName("default");
            if (channel == null) {
                return TCommandStatus.INVALID_ARGS;
            }
            try {
                GearzChat.getInstance().getChannelManager().setChannel((Player) sender, channel);
            } catch (IllegalStateException e) {
                sender.sendMessage(GearzChat.getInstance().getFormat("formats.already-on-channel"));
            }
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.switched", false, new String[]{"<channel>", channel.getName()}));
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }
}
