/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogz.friends.bukkit.manager;

import com.google.common.collect.Lists;
import net.cogz.friends.bukkit.FriendsManager;
import net.cogz.friends.bukkit.GearzBukkitFriends;
import net.tbnr.util.input.SignGUI;
import net.tbnr.util.inventory.base.BaseGUI;
import net.tbnr.util.inventory.base.GUICallback;
import net.tbnr.util.inventory.base.GUIItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author Jake
 * @since 5/11/2014
 */
public class PlayerListener implements Listener {
    private FriendsManager manager;

    public PlayerListener(FriendsManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().addItem(getItemStack());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getPlayer().getItemInHand() == null) return;
        if (!event.getPlayer().getItemInHand().hasItemMeta()) return;
        if (!event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(getItemStack().getItemMeta().getDisplayName()))
            return;
        manageMainGUI(event.getPlayer());
    }

    private void manageMainGUI(Player player) {
        FriendInventory inventory = new FriendInventory(getMainItems(), ChatColor.GOLD + "Friends Management", new GUICallback() {
            @Override
            public void onItemSelect(BaseGUI gui, GUIItem item, Player player) {
                gui.close(player);
                if (item.getName().equals("Add Friend")) {
                    GearzBukkitFriends.getInstance().getSignGUI().open(player, new String[]{"Name Here", "", "", ""}, new SignGUI.SignGUIListener() {
                        @Override
                        public void onSignDone(Player player, String[] lines) {
                            if (lines.length == 0) return;
                            if (lines[0].equals("")) return;
                            Bukkit.dispatchCommand(player, "friend add " + lines[0].trim().replaceAll(" ", ""));
                        }
                    });
                } else if (item.getName().equals("List Friends")) {
                    manageListGUI(player, ListKey.Friends);
                } else if (item.getName().equals("List Friend Requests")) {
                    manageListGUI(player, ListKey.Requests);
                }
            }

            @Override
            public void onGUIOpen(BaseGUI gui, Player player) {
            }

            @Override
            public void onGUIClose(BaseGUI gui, Player player) {
            }

        }, false);
        inventory.open(player);
    }

    public static enum ListKey {
        Friends, Requests
    }

    private void manageListGUI(Player player, ListKey key) {
        if (key == ListKey.Friends) {
            FriendInventory inv = new FriendInventory(getListItems(player, ListKey.Friends), ChatColor.GOLD + "Your Friends", new GUICallback() {
                @Override
                public void onItemSelect(BaseGUI gui, GUIItem item, Player player) {
                    gui.close(player);
                    manageManagementGUI(player, item.getName(), ListKey.Friends);
                }

                @Override
                public void onGUIOpen(BaseGUI gui, Player player) {

                }

                @Override
                public void onGUIClose(BaseGUI gui, Player player) {

                }
            }, false);
            inv.open(player);
        } else if (key == ListKey.Requests) {
            FriendInventory inv = new FriendInventory(getListItems(player, ListKey.Requests), ChatColor.GOLD + "Your Friend Requests", new GUICallback() {
                @Override
                public void onItemSelect(BaseGUI gui, GUIItem item, Player player) {
                    gui.close(player);
                    manageManagementGUI(player, item.getName(), ListKey.Requests);
                }

                @Override
                public void onGUIOpen(BaseGUI gui, Player player) {

                }

                @Override
                public void onGUIClose(BaseGUI gui, Player player) {

                }
            }, false);
            inv.open(player);
        }
    }

    private void manageManagementGUI(Player player, final String friend, ListKey key) {
        FriendInventory inv = new FriendInventory(getManagementItems(key), ChatColor.GOLD + "Manage: " + ChatColor.DARK_AQUA + friend, new GUICallback() {
            @Override
            public void onItemSelect(BaseGUI gui, GUIItem item, Player player) {
                gui.close(player);
                if (item.getName().equals("Join Friend")) {
                    if (Bukkit.getPlayerExact(friend) == null) {
                        player.sendMessage(GearzBukkitFriends.getInstance().getFormat("formats.friend-null", false));
                        return;
                    }
                    Bukkit.dispatchCommand(player, "friend join " + friend);
                } else if (item.getName().equals("Remove Friend")) {
                    Bukkit.dispatchCommand(player, "friend remove " + friend);
                } else if (item.getName().equals("Deny Request")) {
                    Bukkit.dispatchCommand(player, "friend deny " + friend);
                } else if (item.getName().equals("Accept Request")) {
                    Bukkit.dispatchCommand(player, "friend add " + friend);
                }
            }

            @Override
            public void onGUIOpen(BaseGUI gui, Player player) {

            }

            @Override
            public void onGUIClose(BaseGUI gui, Player player) {

            }
        }, false);
        inv.open(player);
    }

    private ItemStack getItemStack() {
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Friend Manager");
        meta.setLore(Lists.newArrayList(ChatColor.GOLD + "Right click to open!"));
        stack.setItemMeta(meta);
        return stack;
    }

    private ArrayList<GUIItem> getMainItems() {
        ArrayList<GUIItem> items = new ArrayList<>();
        items.add(new GUIItem(new ItemStack(Material.DIAMOND), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Add Friend", Lists.newArrayList(ChatColor.GOLD + "Click to add a friend")));
        items.add(new GUIItem(new ItemStack(Material.ANVIL), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "List Friends", Lists.newArrayList(ChatColor.GOLD + "Click to list all your friends")));
        items.add(new GUIItem(new ItemStack(Material.SULPHUR), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "List Friend Requests", Lists.newArrayList(ChatColor.GOLD + "Click here to list all", ChatColor.GOLD + "your received friend requests")));
        return items;
    }

    private ArrayList<GUIItem> getManagementItems(ListKey key) {
        ArrayList<GUIItem> items = new ArrayList<>();
        if (key == ListKey.Friends) {
            items.add(new GUIItem(new ItemStack(Material.ARROW), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Join Friend", Lists.newArrayList(ChatColor.GOLD + "Click to join you friend")));
            items.add(new GUIItem(new ItemStack(Material.REDSTONE_BLOCK), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Remove Friend", Lists.newArrayList(ChatColor.GOLD + "Click to remove this friend")));
        } else if (key == ListKey.Requests) {
            items.add(new GUIItem(new ItemStack(Material.REDSTONE), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Deny Request", Lists.newArrayList(ChatColor.GOLD + "Click to deny this player")));
            items.add(new GUIItem(new ItemStack(Material.GOLD_INGOT), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Accept Request", Lists.newArrayList(ChatColor.GOLD + "Click to accept this player")));
        }
        return items;
    }

    private ArrayList<GUIItem> getListItems(Player player, ListKey key) {
        ArrayList<GUIItem> items = Lists.newArrayList();
        List<String> toSortFrom = new ArrayList<>();
        if (key == ListKey.Friends) {
            toSortFrom = manager.getPlayerFriends(player.getName());
        } else if (key == ListKey.Requests) {
            toSortFrom = manager.getPendingRequests(player.getName());
        }
        for (String string : toSortFrom) {
            GUIItem item = new GUIItem(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()), ChatColor.GOLD + string);
            items.add(item);
        }
        return items;
    }
}
