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

package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * A simple module to send a link for
 * TBNR's leaderboards to players
 *
 * <p>
 * Latest Change: Create module
 * <p>
 *
 * @author Rigor
 * @since 1/25/2014
 */
public class StatsModule implements TCommandHandler {

	@TCommand(name = "stats", aliases = { "leaderboards", "stat" }, senders = { TCommandSender.Player }, permission = "gearz.stats", usage = "/stats")
	public TCommandStatus stats(CommandSender sender, TCommandSender type, TCommand command, String[] args){
		sender.sendMessage(GearzBungee.getInstance().getFormat("stats", true, true, new String[] { "<url>", GearzBungee.getInstance().getFormat("stats-url", false, false, new String[] { "<player>", sender.getName() } )  }));
		return TCommandStatus.SUCCESSFUL;
	}

	@Override
	public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
		GearzBungee.handleCommandStatus(status, sender);
	}
}
