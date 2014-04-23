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

package net.cogz.permissions;

import com.mongodb.DB;
import lombok.Getter;
import net.tbnr.gearz.activerecord.GModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gearz Permissions API
 * Supports many types of
 * MetaData and permissions
 */
@SuppressWarnings("FieldCanBeLocal")
public abstract class GearzPermissions {
    /**
     * Players who are online with their names.
     */
    public Map<String, PermPlayer> players = new HashMap<>();
    /**
     * All groups
     */
    @Getter private Map<String, PermGroup> groups = new HashMap<>();
    /**
     * Default group
     */
    @Getter private PermGroup defaultGroup;

    /**
     * Get currently online players
     *
     * @return all currently online players
     */
    public abstract List<String> onlinePlayers();

    /**
     * Stores the database
     */
    private DB database;

    /**
     * Give a player a permission
     *
     * @param value  True or false, whether or not to add it
     * @param player Name of player
     * @param perm   Permission to give player
     */
    public abstract void givePermsToPlayer(String player, String perm, boolean value);

    /**
     * Used to control the database.
     *
     * @return The DB.
     */
    public abstract DB getDatabase();

    public abstract String getUUID(String player);

    /**
     * Reloads all the data from the database
     */
    public void reload() {
        this.database = getDatabase();
        if (this.database == null) throw new UnsupportedOperationException("No data supplied! Needs a database!");
        try {
	        this.groups = new HashMap<>();
	        defaultGroup = null;
	        PermGroup permGroup = new PermGroup(this.database);
	        List<GModel> many = permGroup.findAll();
	        for (GModel m : many) {
		        if (!(m instanceof PermGroup)) continue;
		        PermGroup group = (PermGroup) m;
		        if (group.isDefault()) defaultGroup = group;
		        this.groups.put(group.getName(), group);
	        }
	        if (getDefaultGroup() == null) {
		        PermGroup group = createGroup("default");
		        group.isDefault = true;
		        defaultGroup = group;
		        group.save();
		        throw new UnsupportedOperationException("Invalid default group! New one created..");
	        }
	        for (String s : onlinePlayers()) {
		        onJoin(s);
	        }
        } catch(Exception exception) {
	        throw new UnsupportedOperationException("Cannot Connect to Database! Maybe Database is lagging?!");
        }
    }

    /**
     * Called when a player joins
     *
     * @param player Player who joined
     */
    public PermPlayer onJoin(String player) {
        GModel one = new PermPlayer(this.database, getUUID(player), player).findOne();
        if (one == null) {
            String uuid = getUUID(player);
            if (uuid == null) throw new IllegalArgumentException("Not a valid player");
            one = new PermPlayer(this.database, uuid, player);
            ((PermPlayer) one).name = player;
            ((PermPlayer) one).setGroup(getDefaultGroup());
            one.save();
        }
        if (!(one instanceof PermPlayer)) return null;
        ((PermPlayer) one).name = player;
        this.players.put(player, (PermPlayer) one);
        reloadPlayer(player);
        return (PermPlayer) one;
    }

    /**
     * Called when a player quits
     * Handles permissions removal
     *
     * @param player the player who quit.
     */
    public void onQuit(String player) {
        this.players.remove(player);
    }

    /**
     * Get a player by name
     *
     * @param player The name of the player
     */
    public PermPlayer getPlayer(String player) {
        return this.players.get(player);
    }

    /**
     * Retrieves an offline player by name.
     * First checks if the player is online,
     * and will return that. Otherwise a new
     * instance of PermPlayer is created and
     * returned.
     *
     * @param player name of the player to retrieve
     * @return a PermPlayer created from the player
     */
    public PermPlayer getOfflinePlayer(String player) {
        if (this.players.containsKey(player)) {
            return this.players.get(player);
        }
        return onJoin(player);
    }

    /**
     * Get a group by name
     *
     * @param group The name of the player
     */
    public PermGroup getGroup(String group) {
        return this.groups.get(group);
    }

    /**
     * Sets a permission
     *
     * @param player Player to update
     * @param perm   permission to add
     * @param value  whether or not the permission is active
     */
    @SuppressWarnings("unused")
    public void givePermToPlayer(String player, String perm, boolean value) {
        if (getPlayer(player) == null) {
            onJoin(player);
        }
        this.players.get(player).addPermission(perm, value);
        this.givePermsToPlayer(player, perm, value);
    }

    /**
     * Sets a permission for a group
     *
     * @param group Group to update
     * @param perm  Permission to give to group
     * @param value whether or not the permission is active
     */
    @SuppressWarnings("unused")
    public void givePermToGroup(String group, String perm, boolean value) {
        if (getGroup(group) == null) {
            throw new IllegalStateException("Group does not exist!");
        }
        getGroup(group).addPermission(perm, value);
        getGroup(group).save();
    }

    /**
     * Removes a player permission
     *
     * @param player player to update
     * @param perm   permission to remove
     */
    @SuppressWarnings("unused")
    public void removePlayerPerm(String player, String perm) {
        PermPlayer permPlayer = this.players.get(player);
        permPlayer.removePermission(perm);
        permPlayer.save();
        reload();
    }

    /**
     * Remove a group permission
     *
     * @param group group to update
     * @param perm  permission to remove
     */
    @SuppressWarnings("unused")
    public void removeGroupPerm(PermGroup group, String perm) {
        group.removePermission(perm);
        group.save();
        reload();
    }

    /**
     * Sets the player to the group
     *
     * @param player player to update
     * @param group  group to add the player to
     */
    @SuppressWarnings("unused")
    public void setGroup(String player, String group) {
        PermPlayer permPlayer = (PermPlayer) new PermPlayer(this.database, getUUID(player), player).findOne();
        if (permPlayer == null) {
            permPlayer = onJoin(player);
        }
        PermGroup permGroup = getGroup(group);
        permPlayer.setGroup(permGroup);
        permPlayer.save();
    }

    /**
     * Reloads a player (loads perms into them)
     *
     * @param player Name of player to reload
     */
    private void reloadPlayer(String player) {
        PermPlayer permPlayer = this.players.get(player);
        if (permPlayer == null) {
            return;
        }
        Map<String, Boolean> perms = new HashMap<>();
        for (PermGroup group : getAllGroups(permPlayer)) {
            for (String entry : group.getPermissions()) {
                try {
                    String[] s = entry.split(",");
                    String permission = s[0];
                    boolean value = Boolean.valueOf(s[1]);
                    perms.put(permission, value);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //ignore -- continue
                }
            }
        }
        if (permPlayer.getPermissions() != null) {
            for (String entry : permPlayer.getPermissions()) {
                try {
                    String[] s = entry.split(",");
                    String permission = s[0];
                    boolean value = Boolean.valueOf(s[1]);
                    perms.put(permission, value);
                } catch (ArrayIndexOutOfBoundsException e) {
                    //ignore -- continue
                }
            }
        }

        for (Map.Entry<String, Boolean> stringBooleanEntry : perms.entrySet()) {
            givePermsToPlayer(permPlayer.getName(), stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
    }

    /**
     * Creates a PermGroup
     *
     * @param name name of group
     * @return group that was created
     */
    public PermGroup createGroup(String name) {
        return createGroup(name, false);
    }

    /**
     * Creates a PermGroup
     *
     * @param name name of group
     * @return group that was created
     */
    public PermGroup createGroup(String name, boolean defau) {
        if (this.groups.containsKey(name)) throw new IllegalStateException("Group already exists");
        PermGroup group = new PermGroup(this.database);
        group.name = name;
        group.isDefault = defau;
        this.groups.put(name, group);
        group.save();
        return group;
    }

    /**
     * Deletes a PermGroup
     *
     * @param name name of the group to delete
     */
    public void deleteGroup(String name) {
        PermGroup permGroup = getGroup(name);
        if (permGroup == null) return;
        PermPlayer player = new PermPlayer(this.database);
        player.setGroup(permGroup);
        for (GModel gModel : player.findMany()) {
            if (!(gModel instanceof PermPlayer)) continue;
            PermPlayer permPlayer = (PermPlayer) gModel;
            permPlayer.removeGroup();
            permPlayer.save();
        }
        permGroup.remove();
    }

    /**
     * Gets the prefix of a player
     *
     * @param player player to getSetting the prefix of
     * @return the prefix of the player
     */
    public String getPrefix(PermPlayer player) {
        String prefix = null;
        if (player.getGroup() != null) {
            PermGroup permGroup = getGroup(player.getGroup());
            prefix = permGroup.getPrefix();
        }
        if (player.getPrefix() != null) prefix = player.getPrefix();
        return prefix;
    }

    /**
     * Gets the suffix of a player
     *
     * @param player player to getSetting the suffix of
     * @return the suffix of the player
     */
    public String getSuffix(PermPlayer player) {
        String prefix = null;
        if (player.getGroup() != null) {
            PermGroup permGroup = getGroup(player.getGroup());
            prefix = permGroup.getSuffix();
        }
        if (player.getSuffix() != null) prefix = player.getSuffix();
        return prefix;
    }

    /**
     * Gets the name color of a player
     *
     * @param player player to getSetting the name color of
     * @return the name color of the player
     */
    @SuppressWarnings("unused")
    public String getNameColor(PermPlayer player) {
        String nameColor = null;
        if (player.getGroup() != null) {
            PermGroup permGroup = getGroup(player.getGroup());
            nameColor = permGroup.getNameColor();
        }
        if (player.getNameColor() != null) nameColor = player.getNameColor();
        return nameColor;
    }

    /**
     * Gets the tab color of a player
     *
     * @param player player to getSetting the tab color of
     * @return the tab color of the player
     */
    @SuppressWarnings("unused")
    public String getTabColor(PermPlayer player) {
        String tabColor = null;
        if (player.getGroup() != null) {
            PermGroup permGroup = getGroup(player.getGroup());
            tabColor = permGroup.getTabColor();
        }
        if (player.getTabColor() != null) tabColor = player.getTabColor();
        return tabColor;
    }

    /**
     * Adds an inheritance to a group
     *
     * @param toUpdate Group to update
     * @param toAdd    Group to add to toUpdate
     */
    @SuppressWarnings("unused")
    public void addInheritance(PermGroup toUpdate, PermGroup toAdd) {
        toUpdate.addInheritance(toAdd);
    }

    /**
     * Removes an inheritance to a group
     *
     * @param toUpdate Group to update
     * @param toAdd    Group to add to toUpdate
     */
    @SuppressWarnings("unused")
    public void removeInheritance(PermGroup toUpdate, PermGroup toAdd) {
        toUpdate.removeInheritance(toAdd);
    }

    /**
     * Gets a full list of groups of a player
     * including inherited groups
     *
     * @param permPlayer player to search
     * @return list of all a player's groups
     */
    public List<PermGroup> getAllGroups(PermPlayer permPlayer) {
        List<PermGroup> allGroups = new ArrayList<>();
        String groupString = permPlayer.getGroup();
        if (groupString == null) return allGroups;
        PermGroup permGroup = getGroup(groupString);
        if (permGroup == null) return allGroups;
        List<PermGroup> totalGroups = getInheritedGroups(permGroup);
        totalGroups.add(permGroup);
        return totalGroups;
    }

    /**
     * Gets all inherited groups in a recursive fashion.
     *
     * @param group Group to resolve inheritances for.
     * @return List of inherited groups.
     */
    private List<PermGroup> getInheritedGroups(PermGroup group) {
        List<PermGroup> inheritedGroups = new ArrayList<>();
        for (String inheritance : group.inheritances) {
            PermGroup group1 = getGroup(inheritance);
            if (group1 == null) continue;
            if (group1.equals(group)) throw new IllegalStateException("Circular resolution error!");
            if (!(group1.inheritances == null || group1.inheritances.size() < 0)) {
                for (PermGroup permGroup : getInheritedGroups(group1)) {
                    if (permGroup.equals(group)) throw new IllegalStateException("Circular resolution error!");
                    if (!(inheritedGroups.contains(permGroup))) inheritedGroups.add(permGroup);
                }
            }
            if (!(inheritedGroups.contains(group1))) inheritedGroups.add(group1);
        }
        if (inheritedGroups.contains(group)) inheritedGroups.remove(group);
        return inheritedGroups;
    }
}
