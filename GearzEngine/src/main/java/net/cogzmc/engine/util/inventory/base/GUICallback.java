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

package net.cogzmc.engine.util.inventory.base;

import org.bukkit.entity.Player;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/10/2014
 */
public interface GUICallback {
    /**
     * Called when something has been pressed in the inventory GUI
     *
     * @param item   is the item that was pressed
     * @param player is the player who pressed it
     */
    public void onItemSelect(BaseGUI gui, GUIItem item, Player player);

    /**
     * Called when the inventory is opened
     *
     * @param gui    is the gui that was opened
     * @param player is the player for whi the GUI was opened
     */
    public void onGUIOpen(BaseGUI gui, Player player);

    /**
     * Called when the inventory is closed
     *
     * @param gui    is the gui that was closed
     * @param player is the player for who the GUI was closed
     */
    public void onGUIClose(BaseGUI gui, Player player);
}
