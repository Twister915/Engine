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

package net.tbnr.gearz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.tbnr.gearz.GearzBungee;

/**
 * Created by jake on 1/4/14.
 *
 * Purpose Of File: Handles IP Bans in the database
 *
 * Latest Change:
 */
public class IPBanHandler {
    final DBCollection dbCollection;

    public IPBanHandler(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void add(String ip, String reason, String issuer) {
        BasicDBObject insert = new BasicDBObject("ip", ip).append("reason", reason).append("issuer", issuer);
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getAddress().getHostName().equals(ip)) {
                player.disconnect(GearzBungee.getInstance().getFormat("ban-reason", false, false, new String[]{"<reason>", reason}));
            }
        }
        dbCollection.insert(insert);
    }

    public void remove(String ip) {
        BasicDBObject query = new BasicDBObject("ip", ip);
        DBCursor dbCursor = dbCollection.find(query);
        if (dbCursor.hasNext()) dbCollection.remove(query);
    }

    public boolean isBanned(String ip) {
        BasicDBObject query = new BasicDBObject("ip", ip);
        DBCursor dbCursor = dbCollection.find(query);
        return dbCursor.hasNext();
    }

    public DBObject getBanObject(String ip) {
        BasicDBObject query = new BasicDBObject("ip", ip);
        DBCursor dbCursor = dbCollection.find(query);
        if (dbCursor.hasNext()) return dbCursor.next();
        return null;
    }
}
