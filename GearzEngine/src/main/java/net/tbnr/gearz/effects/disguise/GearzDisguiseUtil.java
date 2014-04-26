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

import net.tbnr.gearz.effects.disguise.exceptions.NoGearzDisguiseMeta;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 26/04/2014
 */
public class GearzDisguiseUtil {

	public static GearzDisguiseMeta getMeta(GearzDisguiseAPI disguiseAPI) throws NoGearzDisguiseMeta {
		GearzDisguiseMeta disguiseMeta = disguiseAPI.getClass().getAnnotation(GearzDisguiseMeta.class);
		if(disguiseMeta == null) throw new NoGearzDisguiseMeta(disguiseAPI.getClass().getName()+" has no Gearz Disguise Meta.");
		return disguiseMeta;
	}

}
