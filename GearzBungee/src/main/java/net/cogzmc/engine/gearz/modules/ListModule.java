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

package net.cogzmc.engine.gearz.modules;

import lombok.Getter;
import net.cogzmc.engine.gearz.GearzBungee;
import net.cogzmc.engine.server.Server;
import net.cogzmc.engine.server.ServerManager;
import net.cogzmc.engine.util.bungee.command.TCommand;
import net.cogzmc.engine.util.bungee.command.TCommandHandler;
import net.cogzmc.engine.util.bungee.command.TCommandSender;
import net.cogzmc.engine.util.bungee.command.TCommandStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

/**
 * List module, deals with the /list and /where command.
 */
public class ListModule implements TCommandHandler, Listener {
    @Getter
    public final Collection<ProxiedPlayer> staff = new HashSet<>(); //Cache this for efficiency, don't for loop it every tie someone types /list staff lol

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(ServerSwitchEvent event) {
        if (event.getPlayer().hasPermission("gearz.staff")) {
            if (!staffContains(event.getPlayer().getUniqueId())) staff.add(event.getPlayer());
        }
    }

    private Boolean staffContains(UUID uniqueId) {
        for (ProxiedPlayer proxiedPlayer : staff) {
            if (proxiedPlayer.getUniqueId().equals(uniqueId)) return true;
        }
        return false;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(PlayerDisconnectEvent event) {
        staffRemove(event.getPlayer().getUniqueId());
    }

    private void staffRemove(UUID uniqueId) {
        Iterator<ProxiedPlayer> iterator = staff.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getUniqueId().equals(uniqueId)) iterator.remove();
        }
    }

    @TCommand(aliases = {"who", "w", "ls", "players", "online"}, usage = "/list", senders = {TCommandSender.Player, TCommandSender.Console}, permission = "gearz.list", name = "list")
    @SuppressWarnings("unused")
    public TCommandStatus list(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        List<String> multiMessage = new ArrayList<>();
        multiMessage.add(GearzBungee.getInstance().getFormat("list-online", false, false, new String[]{"<online>", String.valueOf(ProxyServer.getInstance().getOnlineCount())}, new String[]{"<max>", String.valueOf(GearzBungee.getInstance().getMaxPlayers())}));
        boolean shouldShowMoreMessage = false;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("more") || args[0].equalsIgnoreCase("all")) {
                multiMessage.add(formatPlayerList(ProxyServer.getInstance().getPlayers()));
            } else if (args[0].equalsIgnoreCase("staff")) {
                multiMessage.add(GearzBungee.getInstance().getFormat("list-server-title", false, false, new String[]{"<server>", "Staff"}, new String[]{"<online>", String.valueOf(staff.size())}) + formatPlayerList(staff));
            } else if (args[0].equalsIgnoreCase("server") && type == TCommandSender.Player) {
                Collection<ProxiedPlayer> thisServer = ((ProxiedPlayer) sender).getServer().getInfo().getPlayers();
                multiMessage.add(GearzBungee.getInstance().getFormat("list-server-title", false, false, new String[]{"<server>", "This Server"}, new String[]{"<online>", String.valueOf(thisServer.size())}) + formatPlayerList(thisServer));
            } else if (args[0].equalsIgnoreCase("refresh") && sender.hasPermission("gearz.list.refresh")) {
                List<ProxiedPlayer> toRemove = new ArrayList<>();
                for (ProxiedPlayer staff : this.staff) {
                    if (staff.getServer() == null) {
                        toRemove.add(staff);
                    }
                }
                this.staff.removeAll(toRemove);
            }
        } else {
            shouldShowMoreMessage = true;
        }
        if (shouldShowMoreMessage) {
            multiMessage.add(GearzBungee.getInstance().getFormat("list-more-server-message", false));
        }
        List<String> stringList = GearzBungee.boxMessage(ChatColor.BLUE, multiMessage);
        for (String s : stringList) {
            sender.sendMessage(s);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(name = "where", permission = "gearz.where", senders = {TCommandSender.Console, TCommandSender.Player}, usage = "/where <name>", aliases = {"find", "search", "seen"})
    @SuppressWarnings("unused")
    public TCommandStatus where(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        if (args.length < 1) return TCommandStatus.FEW_ARGS;
        List<String> messages = new ArrayList<>();
        for (String arg : args) {
            String name = arg;
            boolean online = false;
            String serverBungeeName = null;
            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(arg);
            if (player1 == null) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().toUpperCase().startsWith(arg.toUpperCase())) {
                        player1 = player;
                        break;
                    }
                }
            }
            if (player1 != null) {
                name = player1.getName();
                online = true;
                serverBungeeName = player1.getServer().getInfo().getName();
            }
            messages.add(GearzBungee.getInstance().getFormat("player-status-where", false, true, new String[]{"<status>", online ? "&aonline" : "&coffline"}, new String[]{"<name>", name}));
            if (online) {
                Server server = null;
                for (Server server1 : ServerManager.getAllServers()) {
                    if (server1.getBungee_name().equalsIgnoreCase(serverBungeeName)) {
                        server = server1;
                        break;
                    }
                }
                if (server != null)
                    messages.add(GearzBungee.getInstance().getFormat("player-server-where", false, true, new String[]{"<server>", server.getGame() + server.getNumber()}));
            }
        }
        for (String s : GearzBungee.boxMessage(ChatColor.BLUE, messages)) {
            sender.sendMessage(s);
        }
        return TCommandStatus.SUCCESSFUL;
    }

    /**
     * Formats a list of players for a specific server.
     *
     * @param server The server to list the players of.
     * @return The formatted String.
     */
    private String getServer(ServerInfo server) {
        return GearzBungee.getInstance().getFormat("list-server-title", false, false, new String[]{"<server>", server.getName()}, new String[]{"<online>", String.valueOf(server.getPlayers().size())}) + formatPlayerList(server.getPlayers());
    }

    /**
     * Format a list player
     *
     * @param players The players to format mate.
     * @return The formatted list of players.
     */
    public String formatPlayerList(Collection<ProxiedPlayer> players) {
        int index = 0;
        StringBuilder builder = new StringBuilder();
        for (ProxiedPlayer player : players) {
            builder.append(GearzBungee.getInstance().getFormat("list-player", false, false, new String[]{"<name>", player.getName()}));
            if (index + 1 < players.size()) {
                builder.append(GearzBungee.getInstance().getFormat("list-commaspace", false)).append(" ");
            }
            if (index > 49) {
                builder.append(GearzBungee.getInstance().getFormat("list-more", false, false, new String[]{"<num>", String.valueOf((players.size() + 1) - index)}));
                break;
            }
            index++;
        }
        return builder.toString();
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }
}