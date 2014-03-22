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

package net.tbnr.gearz.server;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.activerecord.GModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 12/17/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ServerManager {
    private static Server server;
    @Setter @Getter
    private static ServerManagerHelper helper;

    public static Server getThisServer() {
        String currentGame = helper.getGame();
        if (server != null) {
            if (server.getGame() == null) {
                server.setGame(currentGame);
            }
            return server;
        }
        Server thisServer = findThisServer();
        if (thisServer != null) {
            return thisServer;
        }
        Integer current = 1;
        List<Server> serversWithGame = getServersWithGame(currentGame);
        List<Integer> serverNumbers = new ArrayList<>();
        for (Server s : serversWithGame) {
            serverNumbers.add(s.getNumber());
        }
        while (serverNumbers.contains(current)) {
            current++;
        }
        Server serv = new Server();
        serv.setGame(currentGame);
        serv.setBungee_name(helper.getBungeeName());
        serv.setNumber(current);
        ServerManager.server = serv;
        return serv;
    }

    public static List<Server> getServersWithGame(String game) {
        List<Server> all = getAllServers();
        List<Server> servers = new ArrayList<>();
        for (Server m : all) {
            if (m.getGame() == null) continue;
            if (m.getGame().equals(game)) {
                servers.add(m);
            }
        }
        return servers;
    }

    public static List<Server> getAllServers() {
        List<GModel> all = new Server().findAll();
        List<Server> servers = new ArrayList<>();
        for (GModel m : all) {
            if (m instanceof Server) servers.add((Server) m);
        }
        return servers;
    }

    public static Server findThisServer() {
        Server query = new Server();
        query.setBungee_name(helper.getBungeeName());
        GModel one = query.findOne();
        if (one == null) return null;
        if (!(one instanceof Server)) return null;
        return (Server) one;
    }

    public static void addPlayer(String name) {
        Server thisServer = getThisServer();
        thisServer.getOnlinePlayers().add(name);
        thisServer.save();
    }

    public static void removePlayer(String name) {
        Server thisServer = getThisServer();
        thisServer.getOnlinePlayers().remove(name);
        thisServer.save();
    }

    public static Server getServerByPlayer(String name) {
        for (Server server : getAllServers()) {
            if (server.getOnlinePlayers().contains(name)) return server;
        }
        return null;
    }

    public static void setStatusString(String str) {
        Server thisServer = getThisServer();
        thisServer.setStatusString(str);
        thisServer.save();
    }

    public static void setOpenForJoining(boolean openForJoining) {
        Server thisServer = getThisServer();
        thisServer.setCanJoin(openForJoining);
        thisServer.save();
    }

    public static boolean canJoin() {
        return getThisServer().isCanJoin();
    }

    public static void setPlayersOnline(Integer players) {
        Server thisServer = getThisServer();
        thisServer.setPlayerCount(players);
        thisServer.save();
    }

    public static void setGame(String game) {
        Server thisServer = getThisServer();
        thisServer.setGame(game);
        thisServer.save();
    }

    public static List<String> getUniqueGames() {
        List<String> games = new ArrayList<>();
        for (Server server1 : getAllServers()) {
            if (!games.contains(server1.getGame())) games.add(server1.getGame());
        }
        return games;
    }

    public static void setMaximumPlayers(Integer maximumPlayers) {
        Server server = getThisServer();
        server.setMaximumPlayers(maximumPlayers);
        server.save();
    }
}
