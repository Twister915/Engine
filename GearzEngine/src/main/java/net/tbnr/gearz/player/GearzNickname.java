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

package net.tbnr.gearz.player;

import net.tbnr.gearz.Gearz;
import net.tbnr.util.ColoredTablist;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayer;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/15/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public final class GearzNickname implements Listener, TCommandHandler {

	@EventHandler(priority = EventPriority.MONITOR)
	@SuppressWarnings("unused")
	public void onPlayerJoin(final TPlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                updateNickname(event.getPlayer());
            }
        }, 120);
	}

	@TCommand(
			name = "nick",
			usage = "Nickname command!",
			permission = "gearz.nick",
			senders = {TCommandSender.Player, TCommandSender.Console})
	@SuppressWarnings("unused")
	public TCommandStatus nick(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
		if (args.length < 1) {
			return TCommandStatus.FEW_ARGS;
		}
		if (type == TCommandSender.Console && args.length < 2) {
			return TCommandStatus.FEW_ARGS;
		}
		TPlayer player = null;
		if (type == TCommandSender.Player) {
			player = Gearz.getInstance().getPlayerManager().getPlayer((Player) sender);
		}
		String nick = args[0];
		if (args.length > 1) {
			if (!sender.hasPermission("gearz.nick.others")) {
				return TCommandStatus.PERMISSIONS;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) return TCommandStatus.INVALID_ARGS;
			player = Gearz.getInstance().getPlayerManager().getPlayer(target);
			nick = args[1];
			//if (nick.equalsIgnoreCase("off")) nick = player.getPlayer().getName();
			sender.sendMessage(Gearz.getInstance().getFormat("formats.update-nickname-other", false, new String[]{"<player>", target.getName()}, new String[]{"<name>", nick}));
		}
		assert player != null;
		if (nick.equalsIgnoreCase("off")) {
			nick = player.getPlayer().getName();
		}
		if ((TPlayer.getAnyPlayerWithUsername(nick) != null || TPlayer.anyMatchesToStorable(Gearz.getInstance(), "nickname", nick)) && !nick.equals(player.getPlayer().getName())) {
			player.sendMessage(Gearz.getInstance().getFormat("formats.nickname-taken", false, new String[]{"<name>", nick}));
			return TCommandStatus.SUCCESSFUL;
		}
		nick = nick.replaceAll("[^A-Za-z0-9_§&]", "");
		if (nick.length() > 16) {
			sender.sendMessage(Gearz.getInstance().getFormat("formats.nickname-too-long"));
			return TCommandStatus.INVALID_ARGS;
		}
		player.store(Gearz.getInstance(), new GearzPlayerNickname(nick));
		String s = this.updateNickname(player);
		player.sendMessage(Gearz.getInstance().getFormat("formats.update-nickname", false, new String[]{"<name>", s}));
		return TCommandStatus.SUCCESSFUL;
	}

	@TCommand(
			name = "realname",
			usage = "/realname <player>",
			permission = "gearz.whois",
			senders = {TCommandSender.Player})
	@SuppressWarnings("unused")
	public TCommandStatus realname(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
		if (args.length < 1) {
			return TCommandStatus.FEW_ARGS;
		}
		GearzPlayerNickname gearzNickname = new GearzPlayerNickname(null);
		TPlayer player = null;
		String nick = null;

		Object stored;
		for (TPlayer tplayer : Gearz.getInstance().getPlayerManager().getPlayers()) {
			stored = tplayer.getStorable(Gearz.getInstance(), gearzNickname);
			if (stored == null || !(stored instanceof String)) continue;
			if (((String) stored).startsWith(args[0])) {
				player = tplayer;
				nick = (String) stored;
				break;
			}
		}
		if (player == null) return TCommandStatus.INVALID_ARGS;
		sender.sendMessage(Gearz.getInstance().getFormat("formats.realname", true, new String[]{"<player>", player.getPlayer().getName()}, new String[]{"<nick>", nick}));
		return TCommandStatus.SUCCESSFUL;
	}


	private String updateNickname(TPlayer player) {
		Object storable = player.getStorable(Gearz.getInstance(), new GearzPlayerNickname(null));
		if (storable == null || !(storable instanceof String)) {
			player.store(Gearz.getInstance(), new GearzPlayerNickname(player.getPlayer().getName()));
			storable = player.getPlayer().getName();
		}

		String nick = ChatColor.translateAlternateColorCodes('&', (String) storable);
		if (!player.getPlayer().hasPermission("gearz.nick.color")) nick = ChatColor.stripColor(nick);

		player.getPlayer().setDisplayName(nick);
		player.getPlayer().setCustomName(ChatColor.stripColor(nick));
		ColoredTablist.updateNick(player.getPlayer());
		return nick;
	}

	@Override
	public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
		Gearz.handleCommandStatus(status, sender);
	}
}
