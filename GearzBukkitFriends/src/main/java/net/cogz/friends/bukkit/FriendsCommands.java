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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Commands that allow players to manage
 * their friends, including adding, removing,
 * and checking the online status of them.
 * <p/>
 * <p/>
 * Latest Change: UUIDs
 * <p/>
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
            description = "Friend management command",
            usage = "/friend <args...>",
            permission = "gearz.friend",
            senders = {TCommandSender.Player})
    @SuppressWarnings("unused, unchecked")
    public TCommandStatus friend(final CommandSender sender, TCommandSender type, TCommand meta, Command command, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-usehelp", false));
            return TCommandStatus.SUCCESSFUL;
        }
        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.attempting-process", true));
        new BukkitRunnable() {
            @Override
            public void run() {
                Player target = null;
                if (args.length == 2) {
                    target = Bukkit.getPlayerExact(args[1]);
                }
                switch (args[0]) {
                    case "add":
                        if (args.length != 2) {
                            Gearz.handleCommandStatus(TCommandStatus.INVALID_ARGS, sender);
                            return;
                        }
                        if (target == null) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                            return;
                        }
                        if (target.getName().equals(sender.getName())) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-self", false));
                            return;
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
                                return;
                            }
                            return;
                        }
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-add", false, new String[]{"<player>", target.getName()}));
                        target.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-add", false, new String[]{"<player>", sender.getName()}));
                        break;
                    case "remove":
                        if (args.length != 2) return;
                        if (args[1].equals(sender.getName())) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-self", false));
                            Gearz.handleCommandStatus(TCommandStatus.INVALID_ARGS, sender);
                            return;
                        }
                        try {
                            manager.removeFriend(sender.getName(), args[1], true);
                        } catch (IllegalStateException e) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-not"));
                            return;
                        }
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-remove", false, new String[]{"<player>", args[1]}));
                        if (target != null) {
                            target.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-remove-victim", false, new String[]{"<player>", sender.getName()}));
                        }
                        break;
                    case "deny":
                        if (args.length != 2) {
                            Gearz.handleCommandStatus(TCommandStatus.INVALID_ARGS, sender);
                            return;
                        }
                        try {
                            manager.denyFriendRequest(sender.getName(), args[1]);
                        } catch (IllegalStateException e) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-no-request", false));
                            return;
                        }
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-deny", false, new String[]{"<player>", args[1]}));
                        break;
                    case "requests":
                        if (args.length > 2) {
                            Gearz.handleCommandStatus(TCommandStatus.INVALID_ARGS, sender);
                            return;
                        }
                        int page = 1;
                        if (args.length == 2) {
                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.not-a-number", false));
                                return;
                            }
                        }
                        try {
                            paginate(sender, manager.getPendingRequests(sender.getName()), page, 1, 2);
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.page-null", false));
                            return;
                        }
                        return;
                    case "help":
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friends-help-header", false));
                        sender.sendMessage(formatHelp("/friend add <player>", "adds a player as a friend."));
                        sender.sendMessage(formatHelp("/friend remove <player>", "removes a player as a friend"));
                        sender.sendMessage(formatHelp("/friend join <player>", "joins the server that the specified friend is on"));
                        sender.sendMessage(formatHelp("/friend deny <player>", "denys a player's friend request"));
                        sender.sendMessage(formatHelp("/friend requests", "Lists all current friend requests"));
                        sender.sendMessage(formatHelp("/friends", "Lists all your friends"));
                        return;
                    case "join":

                        if (!manager.isFriend(sender.getName(), args[1])) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                            return;
                        }
                        Server server = ServerManager.getServerByPlayer(args[1]);
                        if (server == null) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                            return;
                        }
                        if (server.equals(ServerManager.getThisServer())) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-same-server", false));
                        } else if (server.isCanJoin()) {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-join", false, new String[]{"<server>", server.getGame()}, new String[]{"<player>", args[1]}));
                            BouncyUtils.sendPlayerToServer((Player) sender, server);
                        } else {
                            sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-join-bad", false));
                        }
                        return;
                    default:
                        Gearz.handleCommandStatus(TCommandStatus.INVALID_ARGS, sender);
                }
            }
        }.runTaskLaterAsynchronously(GearzBukkitFriends.getInstance(), 0L);

        return TCommandStatus.SUCCESSFUL;
    }

    @TCommand(
            name = "friends",
            description = "Friends list command",
            senders = {TCommandSender.Player},
            permission = "gearz.friends",
            usage = "/friends")
    @SuppressWarnings("unused")
    public TCommandStatus friends(final CommandSender sender, TCommandSender type, TCommand meta, Command command, final String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                int page = 1;
                if (args.length == 1) {
                    try {
                        page = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.not-a-number", false));
                        return;
                    }
                }

                List<String> orderedFriends = sortFriends(manager.getPlayerFriends(sender.getName()));
                try {
                    sendFriendsList((Player) sender, orderedFriends, page);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.page-null", false));
                }
            }
        }.runTaskLaterAsynchronously(GearzBukkitFriends.getInstance(), 0L);
        return TCommandStatus.SUCCESSFUL;
    }

    private List<String> sortFriends(List<String> unsorted) {
        List<String> sortedFriends = new ArrayList<>();
        Iterator<String> iterator =  unsorted.iterator();
        int totalLooped = 0;
        while (iterator.hasNext()) {
            String friend = iterator.next();
            if (totalLooped == unsorted.size()) {
                sortedFriends.add(friend);
                iterator.remove();
            }
            if (Bukkit.getPlayerExact(friend) != null) {
                sortedFriends.add(friend);
                iterator.remove();
            }
            totalLooped++;
        }
        return sortedFriends;
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
