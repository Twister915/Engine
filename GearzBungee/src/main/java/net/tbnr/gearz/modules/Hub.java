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

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/14/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Hub implements TCommandHandler, Listener {

    private List<Server> hubServers;

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        GearzBungee.handleCommandStatus(status, sender);
    }

    public static List<Server> getHubServers() {
        return ServerManager.getServersWithGame("lobby");
    }

    public ServerInfo getAHubServer() {
        if (hubServers.size() < 1) return null;
        int x = 0;
        List<Integer> attempts = new ArrayList<>();
        ServerInfo info = null;
        while (info == null && attempts.size() < hubServers.size()) {
            while (attempts.contains(x)) {
                x = GearzBungee.getRandom().nextInt(hubServers.size());
            }
            info = ProxyServer.getInstance().getServerInfo(hubServers.get(x).getBungee_name());
            attempts.add(x);
        }
        return info;
    }

    public static boolean isHubServer(ServerInfo info) {
        for (Server s : GearzBungee.getInstance().getHub().hubServers) {
            if (s.getBungee_name().equals(info.getName())) return true;
        }
        return false;
    }

    @TCommand(aliases = {"leave", "done", "back", "lobby"}, usage = "/hub", senders = {TCommandSender.Player}, permission = "gearz.hub", name = "hub")
    @SuppressWarnings("unused")
    public TCommandStatus hubCommand(CommandSender sender, TCommandSender type, TCommand meta, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (isHubServer(player.getServer().getInfo())) {
            player.sendMessage(GearzBungee.getInstance().getFormat("already-in-hub", true));
            return TCommandStatus.SUCCESSFUL;
        }
        if (!ServerModule.getServerForBungee(player.getServer().getInfo()).isCanJoin()) {
            player.sendMessage(GearzBungee.getInstance().getFormat("server-cannot-disconnect", true));
            return TCommandStatus.SUCCESSFUL;
        }
        player.getServer().getInfo();
        player.connect(getAHubServer());
        sender.sendMessage(GearzBungee.getInstance().getFormat("send-to-hub", true));
        return TCommandStatus.SUCCESSFUL;
    }

    @EventHandler
    public void onKickEvent(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.getServer() == null) return;
        ServerInfo info = player.getServer().getInfo();
        if (isHubServer(info)) {
            ServerInfo aHubServer = null;
            int x = 0;
            while ((aHubServer == null || !aHubServer.equals(info)) && x < this.hubServers.size()) {
                aHubServer = getAHubServer();
            }
            if (aHubServer != null && !aHubServer.equals(info)) {
                GearzBungee.connectPlayer(player, aHubServer.getName());
                return;
            }
            Server server;
            try {
                server = serverForDispersion();
            } catch (Exception e) {
                return;
            }
            player.sendMessage(GearzBungee.getInstance().getFormat("hub-disconnected-disperse"));
            GearzBungee.connectPlayer(player, server.getBungee_name());
            return;
        }
        event.setCancelServer(getAHubServer());
        event.setCancelled(true);
        player.sendMessage(GearzBungee.getInstance().getFormat("server-kick", true, true, new String[]{"<reason>", event.getKickReason()}));
    }

    private Server serverForDispersion() throws Exception {
        Server s = null;
        for (Server server : ServerManager.getAllServers()) {
            if (server.getGame().equals("lobby")) continue;
            if (!server.getStatusString().equals("lobby")) continue;
            if (s == null) {
                s = server;
                continue;
            }
            if (s.getPlayerCount() > server.getPlayerCount()) s = server;
        }
        if (s == null) throw new Exception("Could not find a server to send the player to!");
        return s;
    }

    @Data
    @RequiredArgsConstructor
    static class HubServerReloadTask implements Runnable {

        @NonNull private final Hub hubManager;

        @Override
        public void run() {
            this.hubManager.hubServers = getHubServers();
        }
    }
}
