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

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseTypes.DisguiseType;
import me.libraryaddict.disguise.DisguiseTypes.MobDisguise;
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
	key = "LibDisguises",
	priority = GearzDisguisePriority.HIGHEST,
	enabled = false,
	pluginName = "LibDisguises"
)
public class LibDisguiseAPI implements GearzDisguiseAPI {
	private static final Boolean debug = true;
	private static final Logger log = Gearz.getInstance().getLogger();

	@Override
	public void onEnable(Gearz gearzPlugin) {
	}

	@Override
	public void disguisePlayerAsMob(GearzPlayer player, EntityType entityType) {
		DisguiseAPI.disguiseToAll(player.getPlayer(), new MobDisguise(DisguiseType.getType(entityType)));
	}

	@Override
	public void undisguisePlayer(GearzPlayer player) {
		DisguiseAPI.undisguiseToAll(player.getPlayer());
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
