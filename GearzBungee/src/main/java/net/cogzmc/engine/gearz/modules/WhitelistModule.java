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

package net.cogzmc.engine.gearz.modules;

import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import net.cogzmc.engine.gearz.GearzBungee;
import net.cogzmc.engine.util.UUIDUtil;
import net.cogzmc.engine.util.bungee.command.*;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

/**
 * Module that allows for global whitelisting
 * on the BungeeCord level. Includes the exact
 * same commands as Bukkit's default whitelist.
 * <p/>
 * <p/>
 * Latest Change: Fix whitelisting players
 * <p/>
 *
 * @author Jake
 * @since 1/15/2014
 */
@Log
public class WhitelistModule implements TCommandHandler, Listener, TTabCompleter {

    Map<UUID, String> whitelisted = new HashMap<>();

    {
        FileConfiguration config = GearzBungee.getInstance().getConfig();
        for (String pair : config.getStringList("whitelisted")) {
            String[] parts = pair.split(":");
            // Hasn't been formatted yet
            if (parts.length < 2) {
                new UUIDUtil(pair, new UUIDUtil.UUIDCallback() {
                    @Override
                    public void complete(String username, String uuid) {
                        if (uuid == null) {
                            GearzBungee.getInstance().getLogger().info("Could not whitelist player \"" + username + "\" because the UUID cannot be found");
                            return;
                        }
                        whitelisted.put(UUID.fromString(uuid), username);
                    }
                });
                // Has been formatted as uuid:username
            } else {
                String uuid = parts[0];
                String username = parts[1];
                try {
                    whitelisted.put(UUID.fromString(uuid), username);
                    // Malformed UUID
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        save();
    }

    @TCommand(name = "gwhitelist", permission = "gearz.gwhitelist", senders = {TCommandSender.Player, TCommandSender.Console}, usage = "/gwhitelist <argument>")
    @SuppressWarnings({"unused", "deprecation"})
    public TCommandStatus gwhitelist(final CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return TCommandStatus.INVALID_ARGS;
        }
        switch (args[0]) {
            case "remove":
                UUID uuid = getLocalUUID(args[1]);
                whitelisted.remove(uuid);
                save();
                sender.sendMessage(ChatColor.GREEN + "Removed " + args[1] + " from the whitelist.");
                break;
            case "add":
				new UUIDUtil(args[1], new UUIDUtil.UUIDCallback() {
					@Override
					public void complete(String username, String uuid) {
						if(uuid == null) return;
						whitelisted.put(UUID.fromString(uuid), username);
						save();
                        sender.sendMessage(ChatColor.GREEN + "Whitelisted " + username + " with the UUID " + uuid);
					}
				});
                break;
            case "list":
                StringBuilder sb = new StringBuilder();
                sb.append("Whitelisted on bungee:");
                for (String string : this.whitelisted.values()) {
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

    private UUID getLocalUUID(String proxiedPlayer) {
        for (Map.Entry<UUID, String> entry : whitelisted.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(proxiedPlayer)) return entry.getKey();
        }
        return null;
    }

    private void save() {
        List<String> configWhitelist = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : this.whitelisted.entrySet()) {
            configWhitelist.add(entry.getKey().toString() + ":" + entry.getValue());
        }
        GearzBungee.getInstance().getConfig().set("whitelisted", configWhitelist);
        GearzBungee.getInstance().saveConfig();
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPostLogin(LoginEvent event) {
        if (GearzBungee.getInstance().isWhitelisted() && !whitelisted.containsKey(event.getConnection().getUniqueId())) {
            event.getConnection().disconnect(GearzBungee.getInstance().getFormat("whitelisted"));
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, TCommandSender senderType, Command command, TCommand meta, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("add")) {
            return GearzBungee.getInstance().getDefaultTabComplete();
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("remove")) {
            List<String> list = Lists.newArrayList();
            list.addAll(this.whitelisted.values());
            return list;
        }
        return null;
    }
}
