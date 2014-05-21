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

package net.cogz.chat;

import com.mongodb.BasicDBList;
import net.cogz.chat.data.Chat;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages chat including chat spying, and
 * default listeners in the case that channels
 * are disabled.
 *
 * <p>
 * Latest Change: Implementation for channels
 * <p>
 *
 * @author Joey
 * @since 9/29/2013
 */
public final class ChatManager implements Listener, TCommandHandler {
    @TCommand(
            name = "censor",
            description = "Command for managing censored words.",
            permission = "gearz.censor",
            usage = "/censor [list|remove|add] [message (required if applicable)]",
            senders = {TCommandSender.Player, TCommandSender.Console}
    )
    @SuppressWarnings("unused")
    public TCommandStatus censors(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) return TCommandStatus.HELP;
        String cmd = args[0];
        Object[] censoredWords1 = GearzChat.getInstance().getCensoredWords();
        if (cmd.equalsIgnoreCase("list")) {
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.header-censorlist", false));
            int index = 0;
            for (Object o : censoredWords1) {
                index++;
                if (!(o instanceof String)) continue;
                String s = (String) o;
                sender.sendMessage(GearzChat.getInstance().getFormat("formats.list-motdlist", false, new String[]{"<index>", String.valueOf(index)}, new String[]{"<motd>", s}));
            }
            return TCommandStatus.SUCCESSFUL;
        }

        if (args.length < 2) return TCommandStatus.FEW_ARGS;
        List<String> strings = new ArrayList<>();
        for (Object o : censoredWords1) {
            if (o instanceof String) strings.add((String) o);
        }

        if (cmd.equalsIgnoreCase("remove")) {
            Integer toRemove = Integer.parseInt(args[1]);
            if (toRemove < 1 || toRemove > censoredWords1.length) {
                sender.sendMessage(GearzChat.getInstance().getFormat("formats.index-out-of-range", false));
                return TCommandStatus.SUCCESSFUL;
            }
            String s = strings.get(toRemove - 1);
            strings.remove(toRemove - 1);
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.removed-motd", false, new String[]{"<motd>", s}));
        } else if (cmd.equalsIgnoreCase("add")) {
            StringBuilder build = new StringBuilder();
            int index = 1;
            while (index < args.length) {
                build.append(args[index]).append(" ");
                index++;
            }
            String s = build.substring(0, build.length() - 1);
            strings.add(s);
            sender.sendMessage(GearzChat.getInstance().getFormat("formats.added-motd", false, new String[]{"<motd>", s}));
        } else {
            return TCommandStatus.INVALID_ARGS;
        }
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.addAll(strings);
        GearzChat.getInstance().setCensoredWords(basicDBList);
        GearzChat.getInstance().getChat().updateCensor();
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "chat",
            description = "Chat management commands such as mute, unmute, etc.",
            usage = "/chat <args>",
            permission = "gearz.chat",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus chat(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) return TCommandStatus.FEW_ARGS;
        Chat chat = GearzChat.getInstance().getChat();
        switch (args[0]) {
            case "mute":
                if (chat.isMuted()) {
                    sender.sendMessage(GearzChat.getInstance().getFormat("formats.chat-is-muted"));
                } else {
                    sender.sendMessage(GearzChat.getInstance().getFormat("formats.chat-mute-on"));
                    chat.setMuted(true);
                }
                return TCommandStatus.SUCCESSFUL;
            case "unmute":
                if (chat.isMuted()) {
                    sender.sendMessage(GearzChat.getInstance().getFormat("formats.chat-mute-off"));
                    chat.setMuted(false);
                } else {
                    sender.sendMessage(GearzChat.getInstance().getFormat("formats.chat-not-muted"));
                    chat.setMuted(true);
                }
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }
}
