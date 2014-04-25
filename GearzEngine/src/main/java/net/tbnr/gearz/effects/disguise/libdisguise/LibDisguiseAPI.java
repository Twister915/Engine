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

package net.tbnr.gearz.effects.disguise.libdisguise;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.effects.disguise.GearzDisguiseAPI;
import net.tbnr.gearz.player.GearzPlayer;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 25/04/2014
 */
public class LibDisguiseAPI implements GearzDisguiseAPI {
	private static final Boolean debug = true;
	private static final Logger log = Gearz.getInstance().getLogger();

	@Override
	public void onEnable(JavaPlugin plugin) {
		if (debug) {
			log.info("onEnable() was called in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It Normally Returns void!");
		}
		// import org.apache.commons.lang.NotImplementedException;
		throw new NotImplementedException("onEnable() has not been created yet in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It would Normally Return void!");
	}

	@Override
	public void disguisePlayerAsMob(GearzPlayer player, EntityType entityType) {
		if (debug) {
			log.info("disguisePlayerAsMob() was called in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It Normally Returns void!");
		}
		// import org.apache.commons.lang.NotImplementedException;
		throw new NotImplementedException("disguisePlayerAsMob() has not been created yet in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It would Normally Return void!");
	}

	@Override
	public EntityType customDisguiseToEntityType(Object o) {
		if (debug) {
			log.info("customDisguiseToEntityType() was called in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It Normally Returns EntityType!");
		}
		// import org.apache.commons.lang.NotImplementedException;
		throw new NotImplementedException("customDisguiseToEntityType() has not been created yet in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It would Normally Return EntityType!");
	}

	@Override
	public Object entityTypeToCustomDisguise(EntityType entityType) {
		if (debug) {
			log.info("entityTypeToCustomDisguise() was called in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It Normally Returns Object!");
		}
		// import org.apache.commons.lang.NotImplementedException;
		throw new NotImplementedException("entityTypeToCustomDisguise() has not been created yet in class net.tbnr.gearz.effects.disguise.libdisguise.LibDisguiseAPI! It would Normally Return Object!");
	}
}
