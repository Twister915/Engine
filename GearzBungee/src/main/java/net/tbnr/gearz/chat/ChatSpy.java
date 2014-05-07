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

package net.tbnr.gearz.chat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/14/2014
 */
public class ChatSpy implements Listener, TCommandHandler {

    public static enum SpyType {
        Chat, Command, All
    }

    private final Map<String, SpyType> spies = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onSpy(ChatEvent event) {
        if (event.getMessage().contains("\\")) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        String m = GearzBungee.getInstance().getFormat("spy-message", false, false, new String[]{"<message>", event.getMessage()}, new String[]{"<sender>", ((ProxiedPlayer) event.getSender()).getName()}, new String[]{"<server>", ((ProxiedPlayer) event.getSender()).getServer().getInfo().getName()});
        for (Map.Entry<String, SpyType> p : this.spies.entrySet()) {
            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(p.getKey());
            if (player1 == null) continue;
            Connection sender = event.getSender();
            if ((p.getValue() == SpyType.All && sender instanceof ProxiedPlayer && ((ProxiedPlayer) sender).getServer() != null && ((ProxiedPlayer) sender).getServer() != player1.getServer()) || (p.getValue() == SpyType.Command && event.isCommand()) || (p.getValue() == SpyType.Chat && !event.isCommand() && sender instanceof ProxiedPlayer && ((ProxiedPlayer) sender).getServer() != null && ((ProxiedPlayer) sender).getServer() != player1.getServer())) {
                player1.sendMessage(m);
            }
        }
    }

    @TCommand(name = "spy", permission = "gearz.spy", senders = {TCommandSender.Player}, usage = "/spy [off|chat|command|all]", aliases = {"cs", "commandspy", "cw", "commandwatcher", "chatspy"})
    @SuppressWarnings("unused")
    public TCommandStatus spy(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.HELP;
        SpyType sType = null;
        String formatKey = null;
        if (args[0].equalsIgnoreCase("off")) {
            this.spies.remove(sender.getName());
            sender.sendMessage(GearzBungee.getInstance().getFormat("spy-off"));
            return TCommandStatus.SUCCESSFUL;
        }
        if (args[0].equalsIgnoreCase("command")) {
            sType = SpyType.Command;
            formatKey = "spy-on-command";
        }
        if (args[0].equalsIgnoreCase("chat")) {
            sType = SpyType.Chat;
            formatKey = "spy-on-chat";
        }
        if (args[0].equalsIgnoreCase("all")) {
            sType = SpyType.All;
            formatKey = "spy-on-all";
        }
        if (sType == null) {
            return TCommandStatus.INVALID_ARGS;
        }
        this.spies.put(sender.getName(), sType);
        sender.sendMessage(GearzBungee.getInstance().getFormat(formatKey));
        return TCommandStatus.SUCCESSFUL;
    }

    @EventHandler(priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        if (this.spies.containsKey(event.getPlayer().getName())) {
            this.spies.remove(event.getPlayer().getName());
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
