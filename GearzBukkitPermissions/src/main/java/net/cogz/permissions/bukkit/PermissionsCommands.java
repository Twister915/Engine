package net.cogz.permissions.bukkit;

import net.cogz.permissions.PermGroup;
import net.cogz.permissions.PermPlayer;
import net.tbnr.gearz.Gearz;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Jake on 1/24/14.
 */
public class PermissionsCommands implements TCommandHandler {

    @TCommand(
            name = "player",
            usage = "/player",
            permission = "gearz.permissions.player",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus player(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 0) return TCommandStatus.FEW_ARGS;
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        PermPlayer player = permsManager.getPlayer(args[0]);
        switch (args[1]) {
            case "reset":
            case "delete":
                if (!sender.hasPermission("gearz.permissions.player.delete")) return TCommandStatus.PERMISSIONS;
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                String name = player.getName();
                player.remove();
                permsManager.onJoin(name);
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.player-deleted"));
                return TCommandStatus.SUCCESSFUL;
            case "set":
                if (!sender.hasPermission("gearz.permissions.player.set")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3 || args.length > 4) return TCommandStatus.INVALID_ARGS;
                if (player == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                boolean value = true;
                if (args.length == 4) {
                    value = Boolean.parseBoolean(args[3]);
                }
                String permission = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-player-perm", false, new String[]{"<permission>", permission}, new String[]{"<player>", args[0]}, new String[]{"<value>", value + ""}));
                player.addPermission(permission, value);
                break;
            case "remove":
            case "unset":
                if (!sender.hasPermission("gearz.permissions.player.remove")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (player == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<player>", args[0]}));
                player.removePermission(args[2]);
                return TCommandStatus.SUCCESSFUL;
            case "check":
                if (!sender.hasPermission("gearz.permissions.player.check")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (player == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-player", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (player.hasPermission(args[2])) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-value", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
                } else {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-player-perm-invalid", false, new String[]{"<player>", args[0]}, new String[]{"<permission>", args[2]}));
                }
                return TCommandStatus.SUCCESSFUL;
            case "perms":
            case "show":
            case "permissions":
                if (!sender.hasPermission("gearz.permissions.player.show")) return TCommandStatus.PERMISSIONS;
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                for (String perm : player.getPermissions()) {
                    String[] s = perm.split(",");
                    sender.sendMessage(s[0] + " : " + s[1]);
                }
                return TCommandStatus.SUCCESSFUL;
            case "addgroup":
            case "setgroup":
                if (!sender.hasPermission("gearz.permissions.player.addgroup")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup group = permsManager.getGroup(args[2]);
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.added-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                player.addPlayerToGroup(group);
                return TCommandStatus.SUCCESSFUL;
            case "removegroup":
                if (!sender.hasPermission("gearz.permissions.player.removegroup")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup grp = permsManager.getGroup(args[2]);
                if (grp == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-player-group", false, new String[]{"<player>", args[0]}, new String[]{"<group>", args[2]}));
                player.removePlayerFromGroup(grp);
                return TCommandStatus.SUCCESSFUL;
            case "prefix":
                if (!sender.hasPermission("gearz.permissions.player.prefix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", player.getName()}));
                    player.prefix = null;
                    player.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String prefix = Gearz.getInstance().compile(args, 2, args.length);
                player.prefix = prefix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                player.save();
                return TCommandStatus.SUCCESSFUL;
            case "suffix":
                if (!sender.hasPermission("gearz.permissions.player.suffix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", player.getName()}));
                    player.suffix = null;
                    player.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String suffix = Gearz.getInstance().compile(args, 2, args.length);
                player.suffix = suffix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                player.save();
                return TCommandStatus.SUCCESSFUL;
            case "tabcolor":
                if (!sender.hasPermission("gearz.permissions.player.tabcolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                player.tabColor = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                player.save();
                return TCommandStatus.SUCCESSFUL;
            case "namecolor":
                if (!sender.hasPermission("gearz.permissions.player.namecolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                player.nameColor = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                player.save();
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "group",
            usage = "/group <args...>",
            permission = "gearz.permissions.group",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus group(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length == 0) return TCommandStatus.FEW_ARGS;
        PermissionsManager permsManager = GearzBukkitPermissions.getInstance().getPermsManager();
        PermGroup group = permsManager.getGroup(args[0]);
        switch (args[1]) {
            case "create":
                if (!sender.hasPermission("gearz.permissions.group.create")) return TCommandStatus.PERMISSIONS;
                if (args.length < 2 || args.length > 3) return TCommandStatus.INVALID_ARGS;
                boolean defau = false;
                if (args.length == 3) {
                    defau = Boolean.parseBoolean(args[2]);
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.created-group", false, new String[]{"<group>", args[0]}, new String[]{"<default>", defau + ""}));
                permsManager.createGroup(args[0], defau);
                return TCommandStatus.SUCCESSFUL;
            case "delete":
                if (!sender.hasPermission("gearz.permissions.group.delete")) return TCommandStatus.PERMISSIONS;
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.deleted-group", false, new String[]{"<group>", args[0]}));
                permsManager.deleteGroup(args[0]);
                return TCommandStatus.SUCCESSFUL;
            case "set":
                if (!sender.hasPermission("gearz.permissions.group.set")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3 || args.length > 4) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                boolean value = true;
                if (args.length == 4) {
                    value = Boolean.parseBoolean(args[3]);
                }
                String permission = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-group-perm", false, new String[]{"<permission>", permission}, new String[]{"<group>", args[0]}, new String[]{"<value>", value + ""}));
                group.addPermission(permission, value);
                break;
            case "remove":
            case "unset":
                if (!sender.hasPermission("gearz.permissions.group.remove")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.remove-group-perm", false, new String[]{"<oermission>", args[2]}, new String[]{"<group>", args[0]}));
                group.removePermission(args[2]);
                return TCommandStatus.SUCCESSFUL;
            case "check":
                if (!sender.hasPermission("gearz.permissions.group.check")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                if (group == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (group.hasPermission(args[2])) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-value", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}, new String[]{"<value>", true + ""}));
                } else {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.check-group-perm-invalid", false, new String[]{"<group>", args[0]}, new String[]{"<permission>", args[2]}));
                }
                return TCommandStatus.SUCCESSFUL;
            case "perms":
            case "show":
            case "permissions":
                if (!sender.hasPermission("gearz.permissions.group.show")) return TCommandStatus.PERMISSIONS;
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                for (String perm : group.getPermissions()) {
                    String[] s = perm.split(",");
                    sender.sendMessage(s[0] + " : " + s[1]);
                }
                return TCommandStatus.SUCCESSFUL;
            case "prefix":
                if (!sender.hasPermission("gearz.permissions.group.prefix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix-null", false, new String[]{"<target>", group.getName()}));
                    group.prefix = null;
                    group.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String prefix = Gearz.getInstance().compile(args, 2, args.length);
                group.prefix = prefix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-prefix", true, new String[]{"<prefix>", prefix}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "suffix":
                if (!sender.hasPermission("gearz.permissions.group.suffix")) return TCommandStatus.PERMISSIONS;
                if (args.length < 3) return TCommandStatus.INVALID_ARGS;
                if (args[2].trim().equals("null")) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix-null", false, new String[]{"<target>", group.getName()}));
                    group.suffix = null;
                    group.save();
                    return TCommandStatus.SUCCESSFUL;
                }
                String suffix = Gearz.getInstance().compile(args, 2, args.length);
                group.suffix = suffix;
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-suffix", true, new String[]{"<suffix>", suffix}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "tabcolor":
                if (!sender.hasPermission("gearz.permissions.group.tabcolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                group.tabColor = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-tab-color", true, new String[]{"<color>", args[2]}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "namecolor":
                if (!sender.hasPermission("gearz.permissions.group.namecolor")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                group.nameColor = args[2];
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.set-name-color", true, new String[]{"<color>", args[2]}));
                group.save();
                return TCommandStatus.SUCCESSFUL;
            case "addinheritance":
                if (!sender.hasPermission("gearz.permissions.group.inheritance")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup toAdd = permsManager.getGroup(args[2]);
                if (toAdd == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                    return TCommandStatus.SUCCESSFUL;
                }
                permsManager.addInheritance(group, toAdd);
                return TCommandStatus.SUCCESSFUL;
            case "removeinheritance":
                if (!sender.hasPermission("gearz.permissions.group.inheritance")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                PermGroup toRemove = permsManager.getGroup(args[2]);
                if (toRemove == null) {
                    sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.null-group"));
                    return TCommandStatus.SUCCESSFUL;
                }
                permsManager.addInheritance(group, toRemove);
                return TCommandStatus.SUCCESSFUL;
            case "setladder":
                if (!sender.hasPermission("gearz.permissions.group.ladder")) return TCommandStatus.PERMISSIONS;
                if (args.length != 3) return TCommandStatus.INVALID_ARGS;
                permsManager.setLadder(group, args[2]);
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "permissions",
            usage = "/permissions <args...>",
            permission = "gearz.permissions",
            senders = {TCommandSender.Player, TCommandSender.Console})
    @SuppressWarnings("unused")
    public TCommandStatus permissions(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            return TCommandStatus.INVALID_ARGS;
        }
        switch (args[0]) {
            case "reload":
                sender.sendMessage(GearzBukkitPermissions.getInstance().getFormat("formats.reload"));
                GearzBukkitPermissions.getInstance().getPermsManager().reload();
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.getInstance().handleCommandStatus(status, sender, senderType);
    }
}
