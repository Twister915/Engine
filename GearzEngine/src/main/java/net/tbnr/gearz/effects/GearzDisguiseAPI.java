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

package net.tbnr.gearz.effects;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;
import net.tbnr.gearz.player.GearzPlayer;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 23/04/2014
 */
public interface GearzDisguiseAPI {

	public static void DisguisePlayerAsMob(GearzPlayer player, EntityType entityType);
}
