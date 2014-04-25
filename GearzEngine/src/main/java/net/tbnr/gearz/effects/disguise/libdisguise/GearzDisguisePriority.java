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

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 25/04/2014
 */
public enum GearzDisguisePriority {

	/**
	 * Highest priority (1)
	 */
	HIGHEST,
	/**
	 * High priority (2)
	 */
	HIGH,
	/**
	 * Normal priority (3)
	 */
	NORMAL,
	/**
	 * Low priority (4)
	 */
	LOW,
	/**
	 * Lowest priority (5)
	 */
	LOWEST,
	/**
	 * Fall back priority ~ for only disguise api's that use no external plugins!
	 */
	FALLBACK

}
