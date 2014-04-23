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

package net.tbnr.gearz.chat.messaging;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module to manage the /msg command
 * and also personal, one way, consversations.
 *
 * <p>
 * Latest Change: Create
 * <p>
 *
 * @author Jake
 * @since Unknown
 */
@SuppressWarnings("unused")
public class Messaging implements TCommandHandler {
    final Map<String, String> lastReplies = new HashMap<>();

    @TCommand(senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/msg <player>", permission = "gearz.message", name = "msg", aliases = {"m", "w", "whisper", "tell", "t"})
    @SuppressWarnings("unused")
    public TCommandStatus messageCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 0 && GearzBungee.getInstance().getConversationManager().isPlayerInConversation(player)) {
            GearzBungee.getInstance().getConversationManager().getConversationForPlayer(player).end();
            return TCommandStatus.SUCCESSFUL;
        }
        if (args.length == 0) {
            return TCommandStatus.FEW_ARGS;
        }
        String msg = compile(args, 1, args.length);
        if (msg.trim().equals("")) return TCommandStatus.INVALID_ARGS;

        List<ProxiedPlayer> matchedPlayers = GearzBungee.getInstance().getPlayerManager().getMatchedPlayers(args[0]);
        if (matchedPlayers.size() < 1) {
            player.sendMessage(GearzBungee.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.INVALID_ARGS;
        }

        ProxiedPlayer target = matchedPlayers.get(0);

        if (target.getName().equals(player.getName())) {
            player.sendMessage(GearzBungee.getInstance().getFormat("message-self", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        if (target == null) {
            player.sendMessage(GearzBungee.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.INVALID_ARGS;
        }
        if (args.length == 1 && !GearzBungee.getInstance().getConversationManager().isPlayerInConversation(player)) {
            new PrivateConversation(player, target);
            return TCommandStatus.SUCCESSFUL;
        }

        String sendToSender = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", target.getName()}, new String[]{"<message>", msg}, new String[]{"<direction>", "to"});

        String sendToTarget = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", sender.getName()}, new String[]{"<message>", msg}, new String[]{"<direction>", "from"});

        target.sendMessage(sendToTarget);
        player.sendMessage(sendToSender);

        lastReplies.put(player.getName(), target.getName());
        lastReplies.put(target.getName(), player.getName());

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(senders = {TCommandSender.Player}, usage = "/reply", permission = "gearz.message", name = "reply", aliases = {"r"})
    @SuppressWarnings("unused")
    public TCommandStatus replyCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        String msg = compile(args, 0, args.length);
        ProxiedPlayer player = (ProxiedPlayer) sender;
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(lastReplies.get(player.getName()));


        if (target == null) {
            player.sendMessage(GearzBungee.getInstance().getFormat("message-notonline", false, false));
            return TCommandStatus.SUCCESSFUL;
        }

        String sendToPlayer = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", target.getName()}, new String[]{"<message>", msg}, new String[]{"<direction>", "to"});

        String sendToTarget = GearzBungee.getInstance().getFormat("messaging-message", false, false, new String[]{"<sender>", player.getName()}, new String[]{"<message>", msg}, new String[]{"<direction>", "from"});

        target.sendMessage(sendToTarget);
        player.sendMessage(sendToPlayer);

        lastReplies.put(player.getName(), target.getName());
        lastReplies.put(target.getName(), player.getName());

        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public String compile(String[] args, int min, int max) {
        return GearzBungee.getInstance().compile(args, min, max);
    }
}
