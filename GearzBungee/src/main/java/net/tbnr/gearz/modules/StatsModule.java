package net.tbnr.gearz.modules;

import net.md_5.bungee.api.CommandSender;
import net.tbnr.gearz.GearzBungee;
import net.tbnr.util.bungee.command.TCommand;
import net.tbnr.util.bungee.command.TCommandHandler;
import net.tbnr.util.bungee.command.TCommandSender;
import net.tbnr.util.bungee.command.TCommandStatus;

/**
 * Created by rigor789 on 2014.01.25..
 * <p/>
 * Purpose Of File: Provides the /stats command for player stats.
 * <p/>
 * Latest Change: Create
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
