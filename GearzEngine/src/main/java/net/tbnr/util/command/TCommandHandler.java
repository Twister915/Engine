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

import org.bukkit.command.CommandSender;

/**
 * Implement this to handle commands using the TCommand annotation.
 */
public interface TCommandHandler {
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType);
}
