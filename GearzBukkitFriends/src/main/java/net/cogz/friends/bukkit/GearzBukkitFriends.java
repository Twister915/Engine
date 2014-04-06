package net.cogz.friends.bukkit;

import lombok.Getter;
import net.tbnr.util.TPlugin;

/**
 * Created by jake on 3/8/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class GearzBukkitFriends extends TPlugin {
    @Getter public static GearzBukkitFriends instance;
    @Getter public FriendsManager friendsManager;

    @Override
    public void enable() {
        GearzBukkitFriends.instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        friendsManager = new FriendsManager();
        registerCommands(new FriendsCommands(friendsManager));
    }

    @Override
    public void disable() {
        saveConfig();
    }

    @Override
    public String getStorablePrefix() {
        return "gfriends";
    }
}
