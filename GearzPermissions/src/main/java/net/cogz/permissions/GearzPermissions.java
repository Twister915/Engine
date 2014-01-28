package net.cogz.permissions;

import com.mongodb.DB;
import lombok.Getter;
import net.tbnr.gearz.activerecord.GModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 1/23/14.
 */
public abstract class GearzPermissions {
    public static GearzPermissions instance;

    public static GearzPermissions getInstance() {
        return instance;
    }
    /**
     * Players who are online with their names.
     */
    private Map<String, PermPlayer> players;
    /**
     * All groups
     */
    private Map<String, PermGroup> groups;
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
    public DB getDatabase() {
        return GModel.getDefaultDatabase();
    }

    /**
     * Sets the player's displays
     *
     * @param player     Player to update
     * @param prefix     Their prefix
     * @param nameColor Their name color
     * @param tabColor  Their tab color
     */
    public abstract void updatePlayerDisplays(String player, String prefix, String nameColor, String tabColor);

    /**
     * Sets a player's chat name color
     *
     * @param player     Player to set the name for
     * @param nameColor Color to set the name too
     */
    public abstract void updatePlayerNameColor(String player, String nameColor);

    /**
     * Sets a players suffix
     *
     * @param player player to update
     * @param suffix new suffix
     */
    public abstract void updatePlayerSuffix(String player, String suffix);

    /**
     * Set a player's prefix
     *
     * @param player player to update
     * @param prefix new prefix
     */
    public abstract void updatePlayerPrefix(String player, String prefix);

    /**
     * Sets a players tab color
     *
     * @param player    player to update
     * @param tabColor new tab color
     */
    public abstract void updatePlayerTabColor(String player, String tabColor);

    /**
     * Reloads all the data from the database
     */
    public void reload() {
        this.database = getDatabase();
        int checks = 0;
        while (this.database == null) {
            this.database = getDatabase();
            checks++;
            if (checks >= 15000) break;
        }
        if (this.database == null) throw new UnsupportedOperationException("No data supplied! Needs a database!");
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
        this.players = new HashMap<>();
        for (String s : onlinePlayers()) {
            onJoin(s);
        }
    }

    /**
     * Player join
     *
     * @param s Player who joined
     */
    public void onJoin(String s) {
        GModel one = new PermPlayer(this.database, s).findOne();
        if (one == null) {
            one = new PermPlayer(this.database, s);
            ((PermPlayer) one).addPlayerToGroup(getDefaultGroup());
            one.save();
        }
        if (!(one instanceof PermPlayer)) return;
        this.players.put(((PermPlayer) one).getName(), (PermPlayer) one);
        reloadPlayer(s);
    }

    /**
     * Quit
     *
     * @param s the player who quit.
     */
    public void onQuit(String s) {
        this.players.remove(s);
    }

    /**
     * Get a player by name
     *
     * @param n The name of the player
     */
    public PermPlayer getPlayer(String n) {
        return this.players.get(n);
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
    public void givePermToPlayer(String player, String perm, boolean value) {
        this.players.get(player).addPermission(perm, value);
    }

    /**
     * Sets a permission for a group
     *
     * @param group Group to update
     * @param perm  Permission to give to group
     * @param value whether or not the permission is active
     */
    public void givePermToGroup(String group, String perm, boolean value) {
        this.groups.get(group).addPermission(perm, value);
    }

    /**
     * Removes a player permission
     *
     * @param player player to update
     * @param perm   permission to remove
     */
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
    public void removeGroupPerm(String group, String perm) {
        PermGroup group1 = this.groups.get(group);
        group1.removePermission(perm);
        group1.save();
        reload();
    }

    /**
     * Sets the player to the group
     *
     * @param player player to update
     * @param group  group to add the player to
     */
    public void setGroup(String player, String group) {
        PermPlayer permPlayer = (PermPlayer) new PermPlayer(this.database, player).findOne();
        if (permPlayer == null) {
            onJoin(player);
        }
        for (String g : permPlayer.getGroups()) {
            PermGroup permGroup = getGroup(g);
            permPlayer.removePlayerFromGroup(permGroup);
        }
        PermGroup group1 = getGroup(group);
        permPlayer.addPlayerToGroup(group1);
        permPlayer.save();
    }

    /**
     * Reloads a player (loads perms into them)
     *
     * @param player Name of player to reload
     */
    private void reloadPlayer(String player) {
        PermPlayer thisPlayer = this.players.get(player);
        if (thisPlayer == null) return;
        Map<String, Boolean> perms = new HashMap<>();
        for (String entry : thisPlayer.getPermissions()) {
            String[] s = entry.split(",");
            String permission = s[0];
            boolean value = Boolean.valueOf(s[1]);
            perms.put(permission, value);
        }
        for (String g : thisPlayer.getGroups()) {
            PermGroup group = getGroup(g);
            for (String entry : group.getPermissions()) {
                String[] s = entry.split(",");
                String permission = s[0];
                boolean value = Boolean.valueOf(s[1]);
                perms.put(permission, value);
            }
        }
        for (Map.Entry<String, Boolean> stringBooleanEntry : perms.entrySet()) {
            givePermsToPlayer(thisPlayer.getName(), stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
        PermGroup permGroup;
        try {
            permGroup = getGroup(thisPlayer.getGroups().get(0));
        } catch (Exception e) {
            return;
        }
        if (permGroup == null) return;
        String prefix = thisPlayer.getPrefix() == null ? permGroup.getPrefix() : thisPlayer.getPrefix();
        String tab_color = thisPlayer.getTabColor() == null ? permGroup.getTabColor() : thisPlayer.getTabColor();
        String name_color = thisPlayer.getNameColor() == null ? permGroup.getNameColor() : thisPlayer.getNameColor();
        if (tab_color == null) tab_color = name_color;
        this.updatePlayerDisplays(player, prefix, name_color, tab_color);
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
        PermGroup group = new PermGroup(this.database);
        group.name = name;
        group.isDefault = defau;
        group.save();
        return group;
    }

    /**
     * Deletes a PermGroup
     *
     * @param name name of the group to delete
     */
    public void deleteGroup(String name) {
        PermGroup group = getGroup(name);
        if (group == null) return;
        PermPlayer player = new PermPlayer(this.database);
        player.addPlayerToGroup(group);
        for (GModel gModel : player.findMany()) {
            if (!(gModel instanceof PermPlayer)) continue;
            PermPlayer player1 = (PermPlayer) gModel;
            player1.removePlayerFromGroup(group);
            player1.save();
        }
        group.remove();
    }

    /**
     * Gets the prefix of a player
     *
     * @param player player to get the prefix of
     * @return the prefix of the player
     */
    public String getPrefix(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = getGroup(player.getGroups().get(0));
        String prefix = group.getPrefix();
        if (player.getPrefix() != null) prefix = player.getPrefix();
        return prefix;
    }

    /**
     * Gets the suffix of a player
     *
     * @param player player to get the suffix of
     * @return the suffix of the player
     */
    public String getSuffix(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = getGroup(player.getGroups().get(0));
        String prefix = group.getSuffix();
        if (player.getSuffix() != null) prefix = player.getSuffix();
        return prefix;
    }

    /**
     * Gets the name color of a player
     *
     * @param player player to get the name color of
     * @return the name color of the player
     */
    public String getNameColor(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = getGroup(player.getGroups().get(0));
        String nameColor = group.getNameColor();
        if (player.getNameColor() != null) nameColor = player.getNameColor();
        return nameColor;
    }

    /**
     * Gets the tab color of a player
     *
     * @param player player to get the tab color of
     * @return the tab color of the player
     */
    public String getTabColor(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = getGroup(player.getGroups().get(0));
        String tabColor = group.getTabColor();
        if (player.getTabColor() != null) tabColor = player.getTabColor();
        return tabColor;
    }
}
