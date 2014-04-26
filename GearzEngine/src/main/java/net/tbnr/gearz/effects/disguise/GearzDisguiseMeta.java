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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta for implementations of the Gearz Disguise API Class
 * <p/>
 * Latest Change: Created It
 * <p/>
 * @see net.tbnr.gearz.effects.disguise.GearzDisguiseAPI
 * @author George
 * @since 25/04/2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GearzDisguiseMeta {

	/**
	 * Priority of the disguise plugin to be used
	 * @return The GearzDisguisePriority
	 */
	public GearzDisguisePriority priority() default GearzDisguisePriority.NORMAL;

	/**
	 * The actual plugin it uses (exact name). If said plugin is not loaded it is deleted
	 * @return The Plugin name
	 */
	public String pluginName() default "";

	/**
	 * If it is enabled
	 * @return true if it is enabled
	 */
	public boolean enabled() default true;

}
