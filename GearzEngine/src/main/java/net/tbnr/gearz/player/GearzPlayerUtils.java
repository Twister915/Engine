package net.tbnr.gearz.player;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.event.player.PlayerLevelChangeEvent;
import net.tbnr.gearz.event.player.PlayerPointChangeEvent;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import net.tbnr.util.player.TPlayerDisconnectEvent;
import net.tbnr.util.player.TPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/24/13
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GearzPlayerUtils implements Listener, TCommandHandler {
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onLevelChange(PlayerLevelChangeEvent event) {
        event.getPlayer().getTPlayer().playSound(Sound.LEVEL_UP);
        event.getPlayer().getTPlayer().sendMessage(Gearz.getInstance().getFormat("formats.level-up", true, new String[]{"<level>", String.valueOf(event.getNewLevel())}, new String[]{"<old-level>", String.valueOf(event.getOldLevel())}));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerJoin(final TPlayerJoinEvent event) {
        if(event.getPlayer() == null) return;
        event.getPlayer().setScoreboardSideTitle(Gearz.getInstance().getFormat("formats.sidebar-title-loading"));
        GearzPlayer.playerFromTPlayer(event.getPlayer()).setupScoreboard();
        Bukkit.getScheduler().runTaskLater(Gearz.getInstance(), new Runnable() {
            @Override
            public void run() {
                GearzPlayer.playerFromTPlayer(event.getPlayer()).updateStats();
            }
        }, 5);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPointChange(PlayerPointChangeEvent event) {
        event.getPlayer().getTPlayer().playSound(Sound.ORB_PICKUP);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerDisconnect(TPlayerDisconnectEvent event) {
        GearzPlayer.removePlayer(event.getPlayer());
    }

    @TCommand(
            name = "xp",
            senders = {TCommandSender.Console, TCommandSender.Player},
            permission = "gearz.xp",
            usage = "Set XP.")
    @SuppressWarnings("unused")
    public TCommandStatus xp(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 2 || (type == TCommandSender.Console && args.length < 3)) return TCommandStatus.FEW_ARGS;
        Player player = (type == TCommandSender.Console || args.length == 3) ? Bukkit.getPlayer(args[0]) : (Player) sender;
        if (player == null) return TCommandStatus.INVALID_ARGS;
        GearzPlayer gplayer = GearzPlayer.playerFromTPlayer(Gearz.getInstance().getPlayerManager().getPlayer(player));
        int xp;
        try {
            xp = Integer.valueOf((type == TCommandSender.Console || args.length == 3) ? args[2] : args[1]);
        } catch (NumberFormatException ex) {
            if (args.length == 2) {
                return TCommandStatus.FEW_ARGS;
            } else {
                return TCommandStatus.INVALID_ARGS;
            }
        }
        switch (((type == TCommandSender.Console || args.length == 3) ? args[1] : args[0])) {
            case "add":
            case "+":
                gplayer.addXp(xp);
                break;
            case "subtract":
            case "remove":
            case "-":
                gplayer.addXp(-1 * xp);
                break;
            case "clear":
                gplayer.addXp(-1 * Integer.MAX_VALUE);
                xp = -1;
                break;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        sender.sendMessage(Gearz.getInstance().getFormat("formats.xp-change", true, new String[]{"<action>", (xp >= 0 ? "added" : "removed")}, new String[]{"<target>", (sender.equals(gplayer.getTPlayer().getPlayer()) ? "yourself" : gplayer.getTPlayer().getPlayer().getName())}, new String[]{"<xp>", String.valueOf(Math.abs(xp))}));
        if (!sender.equals(gplayer.getTPlayer().getPlayer())) gplayer.getTPlayer().sendMessage(Gearz.getInstance().getFormat("formats.xp-changed", true, new String[]{"<action>", (xp >= 0 ? "added" : "removed")}, new String[]{"<xp>", String.valueOf(xp)}));
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }
}
