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

import lombok.Getter;
import net.cogzmc.engine.util.TPlugin;


/**
 * Bukkit Friends Plugin
 * An implementation of the friends API
 *
 * <p>
 * Latest Change: Created
 * <p>
 *
 * @author Jake
 * @since 3/8/2014
 */
public class GearzBukkitFriends extends TPlugin {
    @Getter public static GearzBukkitFriends instance;
    @Getter public FriendsManager friendsManager;

    @Override
    public void enable() {
        GearzBukkitFriends.instance = this;
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
