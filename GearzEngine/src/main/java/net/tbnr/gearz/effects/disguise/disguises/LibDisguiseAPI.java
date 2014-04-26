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
import net.tbnr.gearz.player.GearzPlayer;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.EntityType;

import java.util.logging.Logger;

/**
 * An implementation of the disguise api for lib disguise plugin
 *
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 25/04/2014
 */
@GearzDisguiseMeta(
	priority = GearzDisguisePriority.HIGHEST,
	enabled = false,
	pluginName = "LibDisguises"
)
public class LibDisguiseAPI implements GearzDisguiseAPI {
	private static final Boolean debug = true;
	private static final Logger log = Gearz.getInstance().getLogger();
//	private static final Map<DisguiseType, EntityType> conversionMatrix = new HashMap<>() {
//		{
//			put(null, EntityType.MINECART);
//		}
//	};

	@Override
	public void onEnable(Gearz gearzPlugin) {

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
