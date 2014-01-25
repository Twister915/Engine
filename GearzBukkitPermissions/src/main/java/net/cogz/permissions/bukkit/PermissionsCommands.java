package net.cogz.permissions.bukkit;

import net.cogz.permissions.PermGroup;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsCommands implements TCommandHandler {

    /**
     * group admin create <default>
     */
    @TCommand(
            name = "group",
            usage = "/group <args...>",
            permission = "gearz.permissions.group",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus group(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.INVALID_ARGS;
        }
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        PermGroup group = permsManager.getGroup(args[0]);
        switch (args[1]) {
            case "create":
                if (args.length < 2 || args.length > 3) return TCommandStatus.INVALID_ARGS;
                boolean defau = false;
                if (args.length == 3) {
                    defau = Boolean.parseBoolean(args[2]);
                }
                permsManager.createGroup(args[0], defau);

                break;
            case "delete":
                break;
            case "set":
                break;
            case "remove":
                break;
            case "check":
                break;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "permissions",
            usage = "/permissions <args...>",
            permission = "gearz.permissions",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus command(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 2) {
            return TCommandStatus.INVALID_ARGS;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) return TCommandStatus.INVALID_ARGS;
        String cmd = args[1];
        if (args[0].equalsIgnoreCase("reload")) {
            GearzBukkitPermissions.getInstance().getPermsManager().reload();
            return TCommandStatus.SUCCESSFUL;
        }
        if (args[0].equalsIgnoreCase("group")) {
            if (args.length < 3) {
                return TCommandStatus.INVALID_ARGS;
            }
            switch (args[1]) {
                case "create":
                    GearzBukkitPermissions.getInstance().getPermsManager().createGroup(args[2]);
                    break;
                case "delete":
                    GearzBukkitPermissions.getInstance().getPermsManager().deleteGroup(args[2]);
                    break;
                case "add":
                    GearzBukkitPermissions.getInstance().getPermsManager().getGroup(args[2]).addPermission(args[3], true);
                    break;
                case "remove":
                    GearzBukkitPermissions.getInstance().getPermsManager().getGroup(args[2]).removePermission(args[3]);
                    break;
            }
        }
        switch (cmd) {
            case "add":
                if (args.length < 3) {
                    return TCommandStatus.INVALID_ARGS;
                }
                GearzBukkitPermissions.getInstance().getPermsManager().getPlayer(target.getName()).addPermission(args[2], true);
                break;
            case "remove":
                if (args.length < 3) {
                    return TCommandStatus.INVALID_ARGS;
                }
                GearzBukkitPermissions.getInstance().getPermsManager().givePermsToPlayer(target.getName(), args[2], false);
                break;
            case "check":
                if (args.length < 3) {
                    return TCommandStatus.INVALID_ARGS;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getPermsManager().getPlayer(target.getName()).hasPermission(args[2]) + "");
                break;
            case "group":
                if (args.length < 4) {
                    return TCommandStatus.INVALID_ARGS;
                }
                switch (args[2]) {
                    case "add":
                        GearzBukkitPermissions.getInstance().getPermsManager().getPlayer(target.getName()).addPlayerToGroup(GearzBukkitPermissions.getInstance().getPermsManager().getGroup(args[3]));
                        break;
                    case "remove":
                        GearzBukkitPermissions.getInstance().getPermsManager().getPlayer(target.getName()).removePlayerFromGroup(GearzBukkitPermissions.getInstance().getPermsManager().getGroup(args[3]));
                        break;
                    case "check":
                        sender.sendMessage(GearzBukkitPermissions.getInstance().getPermsManager().getPlayer(target.getName()).isPlayerInGroup(GearzBukkitPermissions.getInstance().getPermsManager().getGroup(args[3])) + "");
                }
                break;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }


}
