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

package net.cogz.friends.bukkit;

import net.cogz.friends.FriendRequestException;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.netcommand.BouncyUtils;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.SimplePaginator;
import net.tbnr.util.command.TCommand;
import net.tbnr.util.command.TCommandHandler;
import net.tbnr.util.command.TCommandSender;
import net.tbnr.util.command.TCommandStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Commands that allow players to manage
 * their friends, including adding, removing,
 * and checking the online status of them.
 *
 * <p>
 * Latest Change: UUIDs
 * <p>
 *
 * @author Jake
 * @since 3/8/2014
 */
public class FriendsCommands extends SimplePaginator implements TCommandHandler {
    FriendsManager manager;

    public FriendsCommands(FriendsManager manager) {
        super(6);
        this.manager = manager;
    }

    @Override
    public String formatEntry(Object entry, int num, int optionNum) {
        if (optionNum == 1) {
            String friend = (String) entry;
            return formatPlayer(friend);
        } else if (optionNum == 2) {
            String friend = (String) entry;
            return formatRequest(friend);
        }
        return null;
    }

    /**
     * 1 == Friend Requests Header
     * 2 == Friends List Header
     */
    @Override
    public String formatHeader(int optionNum) {
        if (optionNum == 1) {
            return GearzBukkitFriends.getInstance().getFormat("formats.friend-requests-header", false);
        } else if (optionNum == 2) {
            return GearzBukkitFriends.getInstance().getFormat("formats.friend-header", false);
        }
        return null;
    }

    @TCommand(
            name = "friend",
            usage = "/friend <args...>",
            permission = "gearz.friend",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused, unchecked")
    public TCommandStatus friend(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-usehelp", false));
            return TCommandStatus.SUCCESSFUL;
        }
        Player target = null;
        if (args.length == 2) {
            target = Bukkit.getPlayerExact(args[1]);
        }
        switch (args[0]) {
            case "add":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                if (target == null) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (target.getName().equals(sender.getName())) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                try {
                    manager.addFriend(sender.getName(), target.getName(), true);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-already", false));
                    break;
                } catch (FriendRequestException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-need-request", false));
                    try {
                        manager.addFriendRequest(target.getName(), sender.getName());
                        target.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-request-received", false, new String[]{"<player>", sender.getName()}));
                    } catch (IllegalStateException ex) {
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-already-request", false));
                        return TCommandStatus.SUCCESSFUL;
                    }
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-add", false, new String[]{"<player>", target.getName()}));
                target.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-add", false, new String[]{"<player>", sender.getName()}));
                break;
            case "remove":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                if (args[1].equals(sender.getName())) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-self", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                try {
                    manager.removeFriend(sender.getName(), args[1], true);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-not"));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-remove", false, new String[]{"<player>", args[1]}));
                if (target != null) {
                    target.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-remove-victim", false, new String[]{"<player>", sender.getName()}));
                }
                break;
            case "deny":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                try {
                    manager.denyFriendRequest(sender.getName(), args[1]);
                } catch (IllegalStateException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-no-request", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-deny", false, new String[]{"<player>", args[1]}));
                break;
            case "requests":
                if (args.length > 2) return TCommandStatus.INVALID_ARGS;
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.not-a-number", false));
                        return TCommandStatus.SUCCESSFUL;
                    }
                }
                try {
                    paginate(sender, manager.getPendingRequests(sender.getName()), page, 1, 2);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.page-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                return TCommandStatus.SUCCESSFUL;
            case "help":
                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friends-help-header", false));
                sender.sendMessage(formatHelp("/friend add <player>", "adds a player as a friend."));
                sender.sendMessage(formatHelp("/friend remove <player>", "removes a player as a friend"));
                sender.sendMessage(formatHelp("/friend join <player>", "joins the server that the specified friend is on"));
                sender.sendMessage(formatHelp("/friend deny <player>", "denys a player's friend request"));
                sender.sendMessage(formatHelp("/friend requests", "Lists all current friend requests"));
                sender.sendMessage(formatHelp("/friends", "Lists all your friends"));
                return TCommandStatus.SUCCESSFUL;
            case "join":
                if (args.length != 2) return TCommandStatus.INVALID_ARGS;
                Server server = ServerManager.getServerByPlayer(args[1]);
                if (server == null) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                    return TCommandStatus.SUCCESSFUL;
                }
                if (server.equals(ServerManager.getThisServer())) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-same-server", false));
                } else if (server.isCanJoin()) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-join", false, new String[]{"<server>", server.getGame()}, new String[]{"<player>", args[1]}));
                    BouncyUtils.sendPlayerToServer((Player) sender, server);
                } else {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-join-bad", false));
                }
                return TCommandStatus.SUCCESSFUL;
            default:
                return TCommandStatus.INVALID_ARGS;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "friends",
            senders = {TCommandSender.Player},
            permission = "gearz.friends",
            usage = "/friends")
    @SuppressWarnings("unused")
    public TCommandStatus friends(CommandSender sender, TCommandSender type, TCommand meta, Command command, String[] args) {
        int page = 1;
        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.not-a-number", false));
                return TCommandStatus.SUCCESSFUL;
            }
        }
        List<String> friends = manager.getPlayerFriends(sender.getName());
        try {
            sendFriendsList((Player) sender, friends, page);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.page-null", false));
            return TCommandStatus.SUCCESSFUL;
        }
        return TCommandStatus.SUCCESSFUL;
    }

    @SuppressWarnings("unchecked")
    private void sendFriendsList(Player player, List<String> friends, int page) throws IllegalArgumentException {
        paginate(player, friends, page, 2, 1);
    }

    private String formatPlayer(String friend) {
        Server server = ServerManager.getServerByPlayer(friend);
        if (server != null) {
            return GearzBukkitFriends.getInstance().getFormat("formats.friend-online", false, new String[]{"<player>", friend}, new String[]{"<server>", server.getGame()});
        } else {
            return GearzBukkitFriends.getInstance().getFormat("formats.friend-offline", false, new String[]{"<player>", friend});
        }
    }

    private String formatRequest(String friend) {
        return GearzBukkitFriends.getInstance().getFormat("formats.friend-request", false, new String[]{"<player>", friend});
    }

    @Override
    public void handleCommandStatus(TCommandStatus status, CommandSender sender, TCommandSender senderType) {
        Gearz.handleCommandStatus(status, sender);
    }

    public String formatHelp(String key, String value) {
        return GearzBukkitFriends.getInstance().getFormat("formats.friends-help", false, new String[]{"<key>", key}, new String[]{"<value>", value});
    }
}
