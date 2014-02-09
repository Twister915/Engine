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
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;

/**
 * Created by George on 01/05/14.
 * <p/>
 * Purpose Of File: To allow for a private channel for mods
 * <p/>
 * Latest Change: Added support for console
 */
@Deprecated //Deprecated on 1/30/2014 in favor of new, better, channels system.
public class ModBroadcast implements TCommandHandler, Listener {

    final ArrayList<String> modBroadcast = new ArrayList<>();

    @TCommand(name = "mb", permission = "gearz.modbroadcast", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/mb <args>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus modBroadcast(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length > 0) {
            String compile = GearzBungee.getInstance().compile(args, 0, args.length);
            sendModBroadcast(sender instanceof ProxiedPlayer ? sender.getName() : "CONSOLE", compile);
            return TCommandStatus.SUCCESSFUL;
        }
        if (type == TCommandSender.Console) return TCommandStatus.FEW_ARGS;

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (modBroadcast.contains(player.getName())) {
            modBroadcast.remove(player.getName());
            player.sendMessage(GearzBungee.getInstance().
                    getFormat("mod-broadcast-off", false, false));
            return TCommandStatus.SUCCESSFUL;
        } else {
            modBroadcast.add(player.getName());
            player.sendMessage(GearzBungee.getInstance().
                    getFormat("mod-broadcast-on", false, false));
            return TCommandStatus.SUCCESSFUL;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(ChatEvent event) {
        if (event.isCommand() || event.isCancelled()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (modBroadcast.contains(player.getName())) {
            sendModBroadcast(player.getName(), event.getMessage());
            event.setCancelled(true);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public void sendModBroadcast(String sender, String s) {
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (!proxiedPlayer.hasPermission("gearz.modbroadcast")) continue;
            proxiedPlayer.sendMessage(GearzBungee.getInstance().getFormat("mod-broadcast", false, false, new String[]{"<sender>", sender}, new String[]{"<message>", s}));
        }
    }
}
