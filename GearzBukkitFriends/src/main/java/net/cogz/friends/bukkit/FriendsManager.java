package net.cogz.friends.bukkit;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.cogz.friends.GearzFriends;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by jake on 3/8/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class FriendsManager extends GearzFriends {
    @Override
    public DBCollection getCollection() {
        return Gearz.getInstance().getMongoDB().getCollection("users");
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
            return TPlayer.getPlayerObject(player);
        }
    }
}
