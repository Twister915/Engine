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

package net.tbnr.util.player.cooldowns;

import com.mongodb.*;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/18/13
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCooldownManager {
    private static final HashMap<String, TCooldown> cooldowns = new HashMap<>();
    public static DB database = null;

    public static boolean canContinue(String key, TCooldown cooldown) {
        if (database == null) {
            return true;
        }
        DBCollection collection = TCooldownManager.getCollection();
        BasicDBObject key2 = new BasicDBObject("key", key);
        DBCursor key1 = collection.find(key2);
        DBObject object = null;
        while (key1.hasNext()) {
            object = key1.next();
        }
        if (object == null) {
            collection.save(key2.append("cooldown-time_stored", cooldown.getTime_stored()).append("cooldown-length", cooldown.getLength()));
            return true;
        } else {
            TCooldown cooldown1 = new TCooldown((Long) object.get("cooldown-time_stored"), (Long) object.get("cooldown-length"));
            if (cooldown1.canContinue()) {
                collection.remove(object);
                return canContinue(key, new TCooldown(cooldown.getLength()));
            } else {
                return false;
            }

        }
    }

    public static boolean canContinueLocal(String key, TCooldown cooldown) {
        if (cooldowns.containsKey(key)) {
            if (cooldowns.get(key).canContinue()) {
                cooldowns.remove(key);
                return canContinueLocal(key, new TCooldown(cooldown.getLength()));
            } else {
                return false;
            }
        } else {
            cooldowns.put(key, cooldown);
            return true;
        }

    }

    private static DBCollection getCollection() {
        return database.getCollection("cooldowns");
    }
}
