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

package net.tbnr.util.player;

import com.mongodb.*;
import lombok.NonNull;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.player.cooldowns.TCooldownManager;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 */
public final class TPlayerManager implements Listener {
    private final HashMap<String, TPlayer> players = new HashMap<>();
    private DBCollection collection = null;
    private DB database = null;
    private static TPlayerManager instance;
    public static String getUsernameForID(ObjectId id) {
        DBCollection collection1 = instance.collection;
        DBObject id1 = collection1.findOne(new BasicDBObject("_id", id));
        if (id1 == null) return null;
        Object username = id1.get("username");
        if (!(username instanceof String)) return null;
        return (String) username;
    }

    public TPlayerManager(AuthenticationDetails details) {
        TPlayerManager.instance = this;
        Plugin gearz = Bukkit.getPluginManager().getPlugin("Gearz");
        Logger logger;
        if (gearz != null) {
            logger = gearz.getLogger();
            if (!gearz.getConfig().getBoolean("database.enable")) {
                return;
            }
        } else {
            logger = Bukkit.getLogger();
        }
        MongoClient databaseClient;
        try {
            databaseClient = details.getClient();
            logger.info("Attempting a connection to the MongoDB!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.severe("Failed to connect!");
            return;
        }
        if (details.getDatabase() == null || details.getPlayerCollection() == null) {
            logger.severe("Cannot continue, data is null for connection!");
            return;
        }
        this.database = databaseClient.getDB(details.getDatabase());
        if (this.database == null) {
            logger.severe("Failed to connect!");
            return;
        }
        this.collection = this.database.getCollection(details.getPlayerCollection());
        logger.info("Connected to MongoDB!");
        TCooldownManager.database = database;

    }

    public static TPlayerManager getInstance() {
        return instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    public void onLogin(PlayerJoinEvent event) {
        TPlayerJoinEvent tPlayerJoinEvent = new TPlayerJoinEvent(this.getPlayer(event.getPlayer()));
        Bukkit.getPluginManager().callEvent(tPlayerJoinEvent);
        event.setJoinMessage(tPlayerJoinEvent.getJoinMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDisconnect(PlayerQuitEvent event) {
        if (!this.players.containsKey(event.getPlayer().getName())) {
            return;
        }
        TPlayerDisconnectEvent tPlayerDisconnectEvent = new TPlayerDisconnectEvent(players.get(event.getPlayer().getName()));
        Bukkit.getPluginManager().callEvent(tPlayerDisconnectEvent);
        event.setQuitMessage(tPlayerDisconnectEvent.getQuitMessage());
        players.get(event.getPlayer().getName()).disconnected();
        players.remove(event.getPlayer().getName());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        this.addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onKick(PlayerKickEvent event) {
        TPlayerDisconnectEvent tPlayerDisconnectEvent = new TPlayerDisconnectEvent(players.get(event.getPlayer().getName()));
        Bukkit.getPluginManager().callEvent(tPlayerDisconnectEvent);
        event.setLeaveMessage(tPlayerDisconnectEvent.getQuitMessage());
    }

    public TPlayer getPlayer(@NonNull Player player) {
        if (!this.players.containsKey(player.getName())) addPlayer(player);
        return players.get(player.getName());
    }

    public TPlayer addPlayer(Player player) {
        if (this.players.containsKey(player.getName())) {
            this.players.remove(player.getName());
        }
        TPlayer tPlayer = new TPlayer(player);
        this.players.put(player.getName(), tPlayer);
        return tPlayer;
    }

    public Collection<TPlayer> getPlayers() {
        return this.players.values();
    }

    public DBCollection getCollection() {
        return collection;
    }

    public DB getDatabase() {
        return database;
    }

    public static class AuthenticationDetails {
        private final String host;
        private final int port;
        private final String database;
        private final String playerCollection;

        public AuthenticationDetails(String host, int port, String database, String playerCollection) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.playerCollection = playerCollection;
        }

        public String getPlayerCollection() {
            return playerCollection;
        }

        public String getDatabase() {
            return database;
        }

        public MongoClient getClient() throws UnknownHostException {
            return new MongoClient(host, port);
        }
    }
}
