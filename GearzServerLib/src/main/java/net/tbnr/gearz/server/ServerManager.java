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
 * Manages servers to allow for
 * setting online players, searching
 * for servers with specific critera,
 * and checking if a server is open
 * for joining. Also manages new
 * instances of servers upon server
 * start with the use of the {@link ServerManagerHelper}
 *
 * <p>
 * Latest Change: Search for servers by player
 * <p>
 *
 * @author Joey
 * @since 12/17/2013
 */
public class ServerManager {
    private static Server server; //Server that this manager manages
    @Setter @Getter
    private static ServerManagerHelper helper;     //Interface for the ServerManager

    /**
     * Creates a new instance of the current
     * {@link Server} otherwise returns a new
     * instance based on the values in the
     * {@link ServerManagerHelper}
     *
     * @return returns the current server
     */
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

    /**
     * Returns the servers with the specified gamemode
     *
     * @param game gamemode to search for
     * @return a {@link List} of {@link List}s with the gamemode specified by game
     */
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

    /**
     * Returns a {@link List} of all servers
     * in the database
     *
     * @return {@link List} of all known {@link Server}
     */
    public static List<Server> getAllServers() {
        List<GModel> all = new Server().findAll();
        List<Server> servers = new ArrayList<>();
        for (GModel m : all) {
            if (m instanceof Server) servers.add((Server) m);
        }
        return servers;
    }

    /**
     * Returns the {@link Server} that the
     * {@link ServerManager} is managing.
     *
     * @return the current {@link Server}
     */
    public static Server findThisServer() {
        Server query = new Server();
        query.setBungee_name(helper.getBungeeName());
        GModel one = query.findOne();
        if (one == null) return null;
        if (!(one instanceof Server)) return null;
        return (Server) one;
    }

    /**
     * Adds a player to the current {@link Server}
     *
     * @param name name of the player to add
     */
    public static void addPlayer(String name) {
        Server thisServer = getThisServer();
        thisServer.getOnlinePlayers().add(name);
        thisServer.save();
    }

    /**
     * Removes a player from the current {@link Server}
     *
     * @param name name of the player to remove
     */
    public static void removePlayer(String name) {
        Server thisServer = getThisServer();
        thisServer.getOnlinePlayers().remove(name);
        thisServer.save();
    }

    /**
     * Finds the current {@link Server} that a player is on
     *
     * @param name name of the player to search for
     * @return the {@link Server} that the player is found on
     */
    public static Server getServerByPlayer(String name) {
        for (Server server : getAllServers()) {
            if (server.getOnlinePlayers().contains(name)) return server;
        }
        return null;
    }

    /**
     * Sets the status string of the current {@link Server}
     *
     * @param status {@link String} to set the status string to
     */
    public static void setStatusString(String status) {
        Server thisServer = getThisServer();
        thisServer.setStatusString(status);
        thisServer.save();
    }

    /**
     * Sets whether or not a {@link Server} can be joined
     *
     * @param openForJoining whether or not the {@link Server} is joinable
     */
    public static void setOpenForJoining(boolean openForJoining) {
        Server thisServer = getThisServer();
        thisServer.setCanJoin(openForJoining);
        thisServer.save();
    }

    /**
     * Returns whether or not the {@link Server}
     * can be joined by players.
     *
     * @return whether or not the server is joinable
     */
    public static boolean canJoin() {
        return getThisServer().isCanJoin();
    }

    /**
     * Sets the number of online players on the current {@link Server}
     *
     * @param players players online
     */
    public static void setPlayersOnline(Integer players) {
        Server thisServer = getThisServer();
        thisServer.setPlayerCount(players);
        thisServer.save();
    }

    /**
     * Sets the game {@link String} of the current {@link Server}
     *
     * @param game game to set the {@link Server} to
     */
    public static void setGame(String game) {
        Server thisServer = getThisServer();
        thisServer.setGame(game);
        thisServer.save();
    }

    /**
     * Returns a list of all games that are currently playing
     *
     * @return a list of currently playing games
     */
    public static List<String> getUniqueGames() {
        List<String> games = new ArrayList<>();
        for (Server server1 : getAllServers()) {
            if (!games.contains(server1.getGame())) games.add(server1.getGame());
        }
        return games;
    }

    /**
     * Sets the maximum number of players that can join a {@link Server}
     *
     * @param maximumPlayers maximum number of players that can join the {@link Server}
     */
    public static void setMaximumPlayers(Integer maximumPlayers) {
        Server server = getThisServer();
        server.setMaximumPlayers(maximumPlayers);
        server.save();
    }
}
