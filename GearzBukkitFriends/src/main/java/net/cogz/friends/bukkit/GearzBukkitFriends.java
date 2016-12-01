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

package net.cogz.friends.bukkit;

import lombok.Getter;
import net.cogz.friends.bukkit.manager.PlayerListener;
import net.tbnr.util.input.SignGUI;
import net.tbnr.util.TPlugin;


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
    @Getter public SignGUI signGUI;

    @Override
    public void enable() {
        GearzBukkitFriends.instance = this;
        friendsManager = new FriendsManager();
        this.signGUI = new SignGUI(this);
        registerCommands(new FriendsCommands(friendsManager));
        registerEvents(new PlayerListener(friendsManager));
    }

    @Override
    public void disable() {
        saveConfig();
        signGUI.destroy();
    }

    @Override
    public String getStorablePrefix() {
        return "gfriends";
    }
}
