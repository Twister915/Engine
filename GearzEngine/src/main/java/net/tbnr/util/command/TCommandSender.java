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

package net.tbnr.util.command;

/**
 * All command sender types
 */
public enum TCommandSender {
    /**
     * The block command sender. This is when a Commandblock sends a command. Unsure on targets.
     */
    Block,
    /**
     * This is when a player sends a command.
     */
    Player,
    /**
     * This is when the console sends a command.
     */
    Console
}
