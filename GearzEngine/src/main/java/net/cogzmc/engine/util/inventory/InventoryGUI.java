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

package net.cogzmc.engine.util.inventory;

import net.cogzmc.engine.util.inventory.base.BaseGUI;
import net.cogzmc.engine.util.inventory.base.GUICallback;
import net.cogzmc.engine.util.inventory.base.GUIItem;

import java.util.ArrayList;

/**
 * <p>
 * Latest Change:
 * <p>
 *
 * @author Jake
 * @since 4/10/2014
 */
public class InventoryGUI extends BaseGUI {

    public InventoryGUI(ArrayList<GUIItem> items, String title, GUICallback callback) {
        this(items, title, callback, true);
    }

    /**
     * An InventoryGUI with callbacks
     *
     * @param items    And array list of the items to be put in the GUI
     * @param title    The title of the GUI
     * @param callback The callback that handles the clicks.
     */
    public InventoryGUI(ArrayList<GUIItem> items, String title, GUICallback callback, boolean effects) {
        super(items, title, callback, effects);
    }
}
