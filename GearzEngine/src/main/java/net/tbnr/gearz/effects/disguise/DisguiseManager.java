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

package net.tbnr.gearz.effects.disguise;

import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.entity.EntityType;

/**
 * An interface for a disguise manager
 *
 * <p/>
 * Latest Change: Created it
 * <p/>
 *
 * @author George
 * @since 25/04/2014
 */
public interface DisguiseManager {





	/**
	 * Get the disguise of the player
	 * @param player The player to get the disguise of
	 * @return the disguise of the player
	 */
	public EntityType getDisguise(GearzPlayer player);

	/**
	 * Get if player is disguised
	 * @param player the player to check if disguised
	 */
	public Boolean isDisguised(GearzPlayer player);

}
