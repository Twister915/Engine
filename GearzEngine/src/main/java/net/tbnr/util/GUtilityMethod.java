/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created by George on 07/02/14.
 * <p/>
 * Purpose Of File: To Represent a utility method ( Not in a GUtility files, for when you make a utility method so later we can search for them to put them in a util file)
 * <p/>
 * Latest Change: Added it
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value={CONSTRUCTOR, METHOD})
public @interface GUtilityMethod {
}
