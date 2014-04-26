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

package net.tbnr.gearz.effects.disguise.disguises;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.effects.disguise.GearzDisguiseAPI;
import net.tbnr.gearz.effects.disguise.GearzDisguiseMeta;
import net.tbnr.gearz.effects.disguise.GearzDisguisePriority;
import net.tbnr.gearz.effects.disguise.exceptions.GearzDisguiseAPIException;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/04/2014
 */
@GearzDisguiseMeta(
		priority = GearzDisguisePriority.FALLBACK
)
public class VanillaDisguiseAPI implements GearzDisguiseAPI {
	private static final boolean debug = false; //TODO get debug mode
	private static final Logger log = null; //TODO get logger

	private static final Map<EntityType, SkullType> conversionMatrix = new HashMap<EntityType, SkullType>() {
		{
			put(EntityType.ZOMBIE, SkullType.ZOMBIE);

			put(EntityType.CREEPER, SkullType.CREEPER);

			put(EntityType.PLAYER, SkullType.PLAYER);

			put(EntityType.WITHER, SkullType.WITHER);

			put(EntityType.SKELETON, SkullType.SKELETON);
		}
	};

	@Override
	public void onEnable(Gearz gearzPlugin) {

	}

	@Override
	public void disguisePlayerAsMob(GearzPlayer player, EntityType entityType) {
		ItemStack head = getHelmet(entityType);
		if(head == null) head = new ItemStack(Material.SKULL_ITEM);
		player.getPlayer().getInventory().setHelmet(head);
	}

	@Override
	public void undisguisePlayer(GearzPlayer player) {
		player.getPlayer().getInventory().setHelmet(null);
	}


	@Override
	public EntityType customDisguiseToEntityType(Object o) {
		try {throw new GearzDisguiseAPIException(VanillaDisguiseAPI.class.getName()+" doesn't have a custom Disguise Type Enum!");}catch(GearzDisguiseAPIException ex) {ex.printStackTrace();}
		return null;
	}

	@Override
	public Object entityTypeToCustomDisguise(EntityType entityType) {
		try {throw new GearzDisguiseAPIException(VanillaDisguiseAPI.class.getName()+" doesn't have a custom Disguise Type Enum!");}catch(GearzDisguiseAPIException ex) {ex.printStackTrace();}
		return null;
	}

	public ItemStack getHelmet(EntityType entityType) {
		ItemStack helmet = new ItemStack(Material.SKULL_ITEM);
		helmet.setDurability((short)conversionMatrix.get(entityType).ordinal());
		return helmet;
	}
}
