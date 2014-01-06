package net.tbnr.gearz.server;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.gearz.activerecord.GModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 12/17/13.
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
        for (Server s : serversWithGame) {
            if (s.getNumber().equals(current)) current = s.getNumber() + 1;
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
}
