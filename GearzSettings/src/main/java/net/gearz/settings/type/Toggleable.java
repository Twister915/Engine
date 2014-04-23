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

package net.gearz.settings.type;

/**
 * Implementable when a setting can be toggled through
 */
public interface Toggleable {
    Object getNextState(Object previous) throws IllegalArgumentException;
}
