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
