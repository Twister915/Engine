package net.tbnr.gearz.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Created by jake on 1/4/14.
 */
public class IPBanHandler {
    DBCollection dbCollection;

    public IPBanHandler(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void add(String ip, String reason, String issuer) {
        BasicDBObject insert = new BasicDBObject("ip", ip).append("reason", reason).append("issuer", issuer);
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
