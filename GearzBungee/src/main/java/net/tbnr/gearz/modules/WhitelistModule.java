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

import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.List;


/**
 * Created by Jake on 1/15/14.
 *
 * Purpose Of File: A Bungee Whitelist
 *
 * Latest Change: Fix whitelisting
 */
public class WhitelistModule implements TCommandHandler, Listener {
    @TCommand(name = "gwhitelist", permission = "gearz.gwhitelist", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/gwhitelist <argument>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus gwhitelist(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return TCommandStatus.INVALID_ARGS;
        }
        FileConfiguration config = GearzBungee.getInstance().getConfig();
        List<String> whitelisted = config.getStringList("whitelisted");
        switch (args[0]) {
            case "remove":
                whitelisted.remove(args[1]);
                config.set("whitelisted", whitelisted);
                GearzBungee.getInstance().saveConfig();
                break;
            case "add":
                whitelisted.add(args[1]);
                config.set("whitelisted", whitelisted);
                GearzBungee.getInstance().saveConfig();
                break;
            case "list":
                StringBuilder sb = new StringBuilder();
                sb.append("Whitelisted on bungee:");
                for (String string : whitelisted) {
                    sb.append(" ").append(string).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sender.sendMessage(sb.toString());
                break;
            case "enable":
            case "on":
                GearzBungee.getInstance().setWhitelisted(true);
                sender.sendMessage(GearzBungee.getInstance().getFormat("enabled"));
                break;
            case "disable":
            case "off":
                GearzBungee.getInstance().setWhitelisted(false);
                sender.sendMessage(GearzBungee.getInstance().getFormat("disabled"));
                break;
            default:
                return TCommandStatus.SUCCESSFUL;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    private boolean isWhitelisted(ProxiedPlayer proxiedPlayer) {
        FileConfiguration config = GearzBungee.getInstance().getConfig();
        List<String> whitelisted = config.getStringList("whitelisted");
        return whitelisted.contains(proxiedPlayer.getName());
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        if (GearzBungee.getInstance().isWhitelisted() && !isWhitelisted(event.getPlayer()) && !event.getPlayer().hasPermission("gearz.whitelist.bypass")) {
            event.getPlayer().disconnect(GearzBungee.getInstance().getFormat("whitelisted"));
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}
