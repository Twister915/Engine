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

package net.tbnr.util.inventory;

import lombok.Getter;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.inventory.base.BaseGUI;
import net.tbnr.util.inventory.base.GUICallback;
import org.bukkit.entity.Player;

/**
 * Created by jake on 12/27/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ServerSelector extends BaseGUI {
    @Getter
    final String gameType;

    public ServerSelector(String gameType, GUICallback inventoryGUICallback) {
        super(InventoryRefresher.getServerItems(InventoryRefresher.getServersForSelector(gameType)), gameType + " Servers", inventoryGUICallback);
        this.gameType = gameType;
    }

    @Override
    public void openGUI(Player player) {
        Gearz.getInstance().getInventoryRefresher().add(this);
    }

    @Override
    public void closeGUI(Player player) {
        Gearz.getInstance().getInventoryRefresher().remove(this);
    }
}
