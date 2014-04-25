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
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 23/04/2014
 */
public interface GearzDisguiseAPI {

	/**
	 * Grab the API for the disguise the plugin manager
	 * @param plugin ~ The plugin
	 */
	public void onEnable(JavaPlugin plugin);


	/**
	 * Disguise a player as a mob
	 * @param player player to disguise
	 * @param entityType mob type
	 * TODO Add supports for extra arguments
	 */
	public void disguisePlayerAsMob(GearzPlayer player, EntityType entityType);

	/**
	 * Turn a custom disguise into an entity type
	 * @param o ~ the custom disguise type
	 */
	public EntityType customDisguiseToEntityType(Object o);

	/**
	 * Turn an entity type into a custom disguise type
	 * @param entityType ~ the entity type
	 */
	public Object entityTypeToCustomDisguise(EntityType entityType);
}
