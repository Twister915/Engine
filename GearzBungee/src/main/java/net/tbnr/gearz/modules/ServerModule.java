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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 12/18/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
@SuppressWarnings("deprecation")
public class ServerModule implements TCommandHandler, Listener {
    /*
    /server - shows current server and lists games
    /server [game] - shows a list of minigame servers for specified game
    /server [game] [number] - connects you to said server
     */
    @SuppressWarnings("UnusedParameters")
    @TCommand(name = "server", aliases = {"servers", "join", "serv"}, permission = "gearz.server", senders = {TCommandSender.Player}, usage = "/server")
    public TCommandStatus server(CommandSender sender, TCommandSender type, TCommand command, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Server thisServer = null;
        for (Server s : ServerManager.getAllServers()) {
            if (s.getBungee_name().equals(player.getServer().getInfo().getName())) {
                thisServer = s;
                break;
            }
        }
        if (thisServer == null) return TCommandStatus.SUCCESSFUL;
        if (args.length != 2)
            player.sendMessage(GearzBungee.getInstance().getFormat("server-connected", false, false, new String[]{"<server>", thisServer.getGame()}, new String[]{"<number>", String.valueOf(thisServer.getNumber())}));
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("lobby") || args[0].equalsIgnoreCase("hub")) {
                player.sendMessage(GearzBungee.getInstance().getFormat("use-lobby", false));
                return TCommandStatus.SUCCESSFUL;
            }
            List<Server> serversWithGame = ServerManager.getServersWithGame(args[0].toLowerCase());
            if (serversWithGame.size() == 0) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-minigame-not-found", false));
                return TCommandStatus.SUCCESSFUL;
            }
            player.sendMessage(GearzBungee.getInstance().getFormat("server-list-header-game", false, false, new String[]{"<minigame>", args[0]}));
            for (Server server : serversWithGame) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-list-item", false, true, new String[]{"<status-color>", "" + ((server.isCanJoin()) ? ChatColor.GREEN : ChatColor.RED)}, new String[]{"<online>", String.valueOf(server.getPlayerCount())}, new String[]{"<server>", server.getGame()}, new String[]{"<number>", String.valueOf(server.getNumber())}));
            }
            return TCommandStatus.SUCCESSFUL;
        }
        if (args.length == 2) {
            List<Server> serversWithGame = ServerManager.getServersWithGame(args[0].toLowerCase());
            if (serversWithGame.size() == 0) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-minigame-not-found", false));
                return TCommandStatus.SUCCESSFUL;
            }
            Server server = null;
            Integer number;
            try {
                number = Integer.valueOf(args[1]);
            } catch (NumberFormatException ex) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-invalid-number", false));
                return TCommandStatus.SUCCESSFUL;
            }
            for (Server server1 : serversWithGame) {
                if (server1.getGame().equals("lobby")) continue;
                if (server1.getNumber().equals(number)) {
                    server = server1;
                    break;
                }
            }
            if (server == null) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-invalid-number", false));
                return TCommandStatus.SUCCESSFUL;
            }
            if (!server.isCanJoin()) {
                player.sendMessage(GearzBungee.getInstance().getFormat("server-not-joinable", false));
                return TCommandStatus.SUCCESSFUL;
            }
            GearzBungee.connectPlayer(player, server.getBungee_name());
            return TCommandStatus.SUCCESSFUL;
        }
        List<Server> allServers = ServerManager.getAllServers();
        Map<String, Integer> minigames = new HashMap<>();
        for (Server allServer : allServers) {
            if (allServer.getGame() == null) continue;
            if (allServer.getGame().equals("lobby")) continue;
            Integer i = 0;
            if (minigames.containsKey(allServer.getGame())) {
                i = minigames.get(allServer.getGame());
            }
            i++;
            minigames.put(allServer.getGame(), i);
        }
        if (minigames.size() == 0) {
            player.sendMessage(GearzBungee.getInstance().getFormat("server-no-minigames", false));
            return TCommandStatus.SUCCESSFUL;
        }
        player.sendMessage(GearzBungee.getInstance().getFormat("server-minigame-list-header", false));
        for (String minigame : minigames.keySet()) {
            player.sendMessage(GearzBungee.getInstance().getFormat("server-minigame-list-item", false, true, new String[]{"<minigame>", minigame}, new String[]{"<count>", String.valueOf(minigames.get(minigame))}));
        }

        return TCommandStatus.SUCCESSFUL;
    }

    public static Server getServerForBungee(ServerInfo info) {
        return getServerForBungee(info.getName());
    }
    public static Server getServerForBungee(String name) {
        for (Server s: ServerManager.getAllServers()) {
            if (s.getBungee_name().equals(name)) return s;
        }
        return null;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static class BungeeServerReloadTask implements Runnable {
        @Override
        public void run() {
            synchronized (ProxyServer.getInstance().getServers()) {
                for (Server server : ServerManager.getAllServers()) {
                    if (ProxyServer.getInstance().getServerInfo(server.getBungee_name()) != null) continue;
                    ProxyServer.getInstance().getLogger().info(server.toString());
                    if (server.getAddress() == null || server.getPort() == null || server.getNumber() == null) {
                        server.remove();
                        continue;
                    }
                    ProxyServer.getInstance().getServers().put(server.getBungee_name(), ProxyServer.getInstance().constructServerInfo(server.getBungee_name(), new InetSocketAddress(server.getAddress(), server.getPort()), GearzBungee.getInstance().getFormat("default-motd", false), false));
                }
            }
            Hub hub = GearzBungee.getInstance().getHub();
            Hub.HubServerReloadTask task = new Hub.HubServerReloadTask(hub);
            task.run();
        }
    }
}
