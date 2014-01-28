package net.tbnr.gearz.player;

import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by rigor789 on 2013.12.23..
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class ClearChat implements TCommandHandler {
    @TCommand(
            name = "clearchat",
            usage = "/clearchat",
            permission = "gearz.clearchat.all",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus clearchat(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        for (int i = 0; i <= 200; i++) {
            silentBroadcast("", true);
        }
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", false);
        silentBroadcast(ChatColor.DARK_AQUA + "\u25BA" + ChatColor.RESET + "" + ChatColor.BOLD + " The chat has been cleared by a staff member", false);
        silentBroadcast(ChatColor.DARK_AQUA + "+" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", 60) + "+", false);
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "clearmychat",
            usage = "/clearmychat",
            permission = "gearz.clearchat.own",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus clearmychat(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        for (int i = 0; i <= 200; i++) {
            sender.sendMessage("");
        }
        sender.sendMessage(ChatColor.GREEN + "Chat cleared!");
        return TCommandStatus.SUCCESSFUL;
    }

    private void silentBroadcast(String message, boolean bypassOPs) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if ((p.hasPermission("gearz.clearchat.bypass") || p.isOp()) && bypassOPs) continue;
            p.sendMessage(message);
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }
}
