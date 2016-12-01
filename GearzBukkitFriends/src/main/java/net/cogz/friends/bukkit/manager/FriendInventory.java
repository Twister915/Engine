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

package net.cogz.friends.bukkit.manager;

import net.tbnr.util.inventory.base.BaseGUI;
import net.tbnr.util.inventory.base.GUICallback;
import net.tbnr.util.inventory.base.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author Jake
 * @since 5/11/2014
 */
public class FriendInventory extends BaseGUI {
    public FriendInventory(ArrayList<GUIItem> items, String title, GUICallback callback, boolean effects) {
        super(items, title, callback, effects);
    }

    public void closeGUI(Player player) {
        HandlerList.unregisterAll(this);
    }

}
