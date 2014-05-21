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

package net.cogzmc.engine.util.command;

/**
 * The command statuses!
 */
public enum TCommandStatus {
    /**
     * Success!
     */
    SUCCESSFUL,
    /**
     * We don't have permission for this command!
     */
    PERMISSIONS,
    /**
     * Invalid arguments
     */
    INVALID_ARGS,
    /**
     * Too few arguments
     */
    FEW_ARGS,
    /**
     * Too many arguments
     */
    MANY_ARGS,
    /**
     * Return the help data
     */
    HELP,
    /**
     * The wrong target executed the command
     */
    WRONG_TARGET
}
