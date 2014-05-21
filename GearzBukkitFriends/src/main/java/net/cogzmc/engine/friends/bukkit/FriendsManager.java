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

package net.cogzmc.engine.friends.bukkit;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.cogzmc.engine.friends.GearzFriends;
import net.cogzmc.engine.gearz.Gearz;
import net.cogzmc.engine.util.player.TPlayer;
import net.cogzmc.engine.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manages Bukkit end of the friends API
 *
 * <p>
 * Latest Change: UUIDs
 * <p>
 *
 * @author Jake
 * @since 3/8/2014
 */
public class FriendsManager extends GearzFriends {
    DBCollection collection;
    @Override
    public DBCollection getCollection() {
        if (collection != null) return collection;
        this.collection = Gearz.getInstance().getMongoDB().getCollection("users");
        return this.collection;
    }

    public boolean isPlayerOnline(String player) {
        return Bukkit.getPlayerExact(player) != null;
    }

    @Override
    public DBObject getPlayerDocument(String player) {
        if (isPlayerOnline(player)) {
            Player bukkitPlayer = Bukkit.getPlayerExact(player);
            TPlayer tPlayer = TPlayerManager.getInstance().getPlayer(bukkitPlayer);
            return tPlayer.getPlayerDocument();
        } else {
            return TPlayer.getPlayerObjectByLastKnownName(player);
        }
    }
}
