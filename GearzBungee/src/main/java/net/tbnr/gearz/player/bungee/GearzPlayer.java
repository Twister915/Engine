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

package net.tbnr.gearz.player.bungee;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores information about a GearzPlayer
 * which is the object used to access the
 * a player's player document and store
 * other vital information about them.
 *
 * <p>
 * Latest Change: Update to use UUIDs
 * <p>
 *
 * @author Joey
 * @since Unknown
 */
public final class GearzPlayer {
    /**
     * The player's username
     */
    @Getter private String username;
    @Getter private String uuid;
    /**
     * The player document
     */
    private DBObject playerDocument;

    /**
     * This will return a GearzPlayer with the player's UUID
     * and it's last known username in the Gearz database
     *
     * @param object DBObject to search for
     * @throws PlayerNotFoundException thrown if a username or uuid is not found
     */
    private GearzPlayer(@NonNull DBObject object) throws PlayerNotFoundException {
        try {
            this.username = (String) object.get("current_username");
            this.uuid = (String) object.get("uuid");
        } catch (ClassCastException ex) {
            throw new PlayerNotFoundException("Invalid DBObject");
        }
        this.playerDocument = object;
    }

    /**
     * Deprecated in palce of GearzPlayer(ProxiedPlayer)
     *
     * @param player player's name
     * @throws PlayerNotFoundException thrown when the player is not found
     */
    public GearzPlayer(String player) throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("current_username", player);
        try {
            this.username = (String) object.get("current_username");
        } catch (ClassCastException ex) {
            throw new PlayerNotFoundException("Invalid username");
        }
        loadUsernameDocument();
    }

    public GearzPlayer(String uuid, boolean findUUID) throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("uuid", uuid);
        try {
            this.uuid = (String) object.get("uuid");
        } catch (ClassCastException ex) {
            throw new PlayerNotFoundException("Invalid UUID String");
        }
        loadUUIDDocument();
    }

    /**
     * This will create a GearzPlayer with the UUID passed and the
     * last known username for the player in the Gearz database
     *
     * @param uuid uuid to search for
     * @throws PlayerNotFoundException thrown if a username or uuid is not found
     */
    public GearzPlayer(UUID uuid) throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("uuid", uuid.toString());
        try {
            this.uuid = (String) object.get("uuid");
        } catch (ClassCastException ex) {
            throw new PlayerNotFoundException("Invalid UUID");
        }
        loadUUIDDocument();
    }

    /**
     * Creates a GearzPlayer from a ProxiedPlayer
     * This will store 100% correct UUIDs and names
     *
     * @param player player to get the GearzPlayer for
     * @throws PlayerNotFoundException thrown when a player is not found
     */
    public GearzPlayer(ProxiedPlayer player) throws PlayerNotFoundException {
        this.username = player.getName();
        this.uuid = player.getUniqueId().toString();
        loadUUIDDocument();
    }

    public static GearzPlayer getById(ObjectId id) throws PlayerNotFoundException {
        DBObject query = new BasicDBObject("_id", id);
        DBObject one = getCollection().findOne(query);
        if (one == null) throw new PlayerNotFoundException("Invalid ID");
        return new GearzPlayer(one);
    }

    /**
     * Loads the document from the database representing the player :D
     *
     * @throws PlayerNotFoundException This occurs when there is no database object for that player, can be used as a hook
     *                                 for retrying the find.
     */
    public void loadUUIDDocument() throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("uuid", this.uuid);
        DBObject cursor = getCollection().findOne(object);
        if (cursor != null) {
            this.playerDocument = cursor;
        } else {
            throw new PlayerNotFoundException("Player not found yet!");
        }
        this.username = (String) this.playerDocument.get("current_username");
    }

    public void loadUsernameDocument() throws PlayerNotFoundException {
        DBObject object = new BasicDBObject("current_username", this.username);
        DBObject cursor = getCollection().findOne(object);
        if (cursor != null) {
            this.playerDocument = cursor;
        } else {
            throw new PlayerNotFoundException("Player not found yet!");
        }
        this.uuid = (String) this.playerDocument.get("uuid");
    }

    /**
     * Gets the base collection.
     *
     * @return Collection
     */
    public static DBCollection getCollection() {
        return GearzBungee.getInstance().getMongoDB().getCollection(GearzBungee.getInstance().getFormat("db-collection", false, false));
    }

    /**
     * Player Not Found Exception
     */
    public static class PlayerNotFoundException extends Exception {
        public PlayerNotFoundException(String s) {
            super(s);
        }
    }

    public DBObject getPlayerDocument() {
        if (this.playerDocument != null) return this.playerDocument;
        try {
            loadUUIDDocument();
        } catch (PlayerNotFoundException e) {
            return null;
        }
        return this.playerDocument;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return ProxyServer.getInstance().getPlayer(this.username);
    }


    public List<String> getUsernameHistory() {
        List<String> usernames = new ArrayList<>();
        BasicDBList usernamesObject = (BasicDBList) playerDocument.get("usernames");
        if (usernamesObject == null) {
            return usernames;
        }
        for (Object object : usernamesObject) {
            if (!(object instanceof String)) continue;
            String name = (String) object;
            usernames.add(name);
        }
        return usernames;
    }

    public List<String> getIPHistory() {
        List<String> usernames = new ArrayList<>();
        BasicDBList usernamesObject = (BasicDBList) playerDocument.get("ips");
        if (usernamesObject == null) {
            return usernames;
        }
        for (Object object : usernamesObject) {
            if (!(object instanceof String)) continue;
            String name = (String) object;
            usernames.add(name);
        }
        return usernames;
    }

    public void save() {
        getCollection().save(this.playerDocument);
    }
}
