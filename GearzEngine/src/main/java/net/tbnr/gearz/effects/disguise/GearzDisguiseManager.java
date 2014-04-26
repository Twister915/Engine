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

import net.tbnr.gearz.effects.disguise.disguises.LibDisguiseAPI;
import net.tbnr.gearz.effects.disguise.exceptions.NoGearzDisguiseMeta;
import net.tbnr.gearz.player.GearzPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Gearz Disguise Manager
 * <p/>
 * Latest Change: Created it
 * <p/>
 * 
 * @author George
 * @since 25/04/2014
 */
public class GearzDisguiseManager implements DisguiseManager {

	private List<GearzDisguiseAPI> disguises = new ArrayList<>();

	public GearzDisguiseManager() {
		
		init();
	}

	private void init() {

		registerDisguises(
			new LibDisguiseAPI()
		);


	}

	/**
	 * Registers all the disguises
	 * @param disguisesAPI The disguise api's
	 */
	private void registerDisguises(GearzDisguiseAPI... disguisesAPI) {
		disguises.addAll(Arrays.asList(disguisesAPI));
	}

	/**
	 * Get the disguise of the player
	 * @param player The player to get the disguise of
	 * @return the disguise of the player
	 */
	public EntityType getDisguise(GearzPlayer player) {
		return null;
	} //todo

	/**
	 * Get if player is disguised
	 * @param player the player to check if disguised
	 */
	public Boolean isDisguised(GearzPlayer player) {
		return false;
	} //todo

	public GearzDisguiseAPI getDisguiseAPI() {
		GearzDisguiseAPI disguiseAPI = disguises.get(0);
		GearzDisguiseMeta disguiseMeta;
		for(GearzDisguiseAPI disguise : disguises) {
			try {
				disguiseMeta = GearzDisguiseUtil.getMeta(disguise);
				if(disguiseMeta.enabled() &&
						disguiseMeta.priority().isHigher(GearzDisguiseUtil.getMeta(disguiseAPI).priority()) &&
						Bukkit.getPluginManager().isPluginEnabled(disguiseMeta.pluginName())) disguiseAPI = disguise;
			} catch (NoGearzDisguiseMeta noGearzDisguiseMeta) {
				noGearzDisguiseMeta.printStackTrace();
			}
		}
		return disguiseAPI;
	}

	public GearzDisguiseAPI getDisguiseAPI(String s) {
		for(GearzDisguiseAPI disguise : disguises) {
			try {
				if(GearzDisguiseUtil.getMeta(disguise).pluginName().equalsIgnoreCase(s)) return disguise;
			} catch (NoGearzDisguiseMeta noGearzDisguiseMeta) {
				noGearzDisguiseMeta.printStackTrace();
			}
		}
		return null;
	}
}
