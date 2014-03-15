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

package net.tbnr.util;

import lombok.Data;
import net.tbnr.util.player.TPlayer;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/7/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
public class PlayerResetParams {
    @SuppressWarnings("unused")
    private TPlayer player;
    @SuppressWarnings("unused")
    private List<ItemStack> doNotclear;
    @SuppressWarnings("unused")
    private boolean clearXP = true;
    @SuppressWarnings("unused")
    private boolean clearPotions = true;
    @SuppressWarnings("unused")
    private boolean restoreHealth = true;
    @SuppressWarnings("unused")
    private boolean restoreFood = true;
    @SuppressWarnings("unused")
    private boolean resetFlight = true;
    @SuppressWarnings("unused")
    private boolean movePlayerDown = true;
    @SuppressWarnings("unused")
    private boolean restoreSpeeds = true;
    @SuppressWarnings("unused")
    private boolean resetInventory = true;
    @SuppressWarnings("unused")
    private GameMode resetGamemode = GameMode.SURVIVAL;
}