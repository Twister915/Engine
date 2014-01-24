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
    @Getter
    private PermGroup defaultGroup;

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
     * @param value  Adds a permission to a player
     * @param player Name of player
     * @param perm   Permission to give player
     * @param value  Value of that permission
     */
    public abstract void givePermsToPlayer(String player, String perm, boolean value);

    /**
     * Used to control the database.
     *
     * @return The DB.
     */
    public abstract DB getDatabase();

    /**
     * Sets the player's displays
     *
     * @param player     Player to update
     * @param prefix     Their prefix
     * @param name_color Their name color
     * @param tab_color  Their tab color
     */
    public abstract void updatePlayerDisplays(String player, String prefix, String name_color, String tab_color);

    /**
     * Reloads all the data from the database
     */
    public void reload() {
        this.database = getDatabase();
        if (this.database == null) throw new UnsupportedOperationException("No data supplied! Needs a database!");
        this.groups = new HashMap<>();
        defaultGroup = null;
        PermGroup PermGroup = new PermGroup(this.database);
        List<GModel> many = PermGroup.findAll();
        for (GModel m : many) {
            if (!(m instanceof PermGroup)) continue;
            System.out.println(((PermGroup) m).name); //TODO remove lel
            PermGroup group = (PermGroup) m;
            if (group.isDefault) defaultGroup = group;
            this.groups.put(group.name, group);
        }
        if (defaultGroup == null) {
            throw new UnsupportedOperationException("Invalid default group! The dhsad you think you're doing bro?");
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
            ((PermPlayer) one).addPlayerToGroup(defaultGroup);
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
     * @param player The name of the player
     */
    public PermGroup getGroup(String player) {
        return this.groups.get(player);
    }

    /**
     * Sets a permission
     *
     * @param player
     * @param perm
     * @param perm
     */
    @SuppressWarnings("unused")
    public void givePermToPlayer(String player, String perm, boolean value) {
        this.players.get(player).addPermission(perm, value);
    }

    /**
     * Sets a permission
     *
     * @param player
     * @param perm
     */
    public void givePermToGroup(String player, String perm, boolean value) {
        this.groups.get(player).addPermission(perm, value);
    }

    /**
     * Unsets a permission
     *
     * @param player
     * @param perm
     */
    @SuppressWarnings("unused")
    public void unsetPlayerPerm(String player, String perm) {
        PermPlayer PermPlayer = this.players.get(player);
        PermPlayer.removePermission(perm);
        PermPlayer.save();
        reload();
    }

    /**
     * Unsets a permission
     *
     * @param group
     * @param perm
     */
    public void unsetGroupPerm(String group, String perm) {
        PermGroup group1 = this.groups.get(group);
        group1.removePermission(perm);
        group1.save();
        reload();
    }

    /**
     * Sets a group
     *
     * @param player
     * @param group
     */
    @SuppressWarnings("unused")
    public void setGroup(String player, String group) {
        PermPlayer PermPlayer = (PermPlayer) new PermPlayer(this.database, player).findOne();
        if (PermPlayer == null) {
            PermPlayer = new PermPlayer(this.database, player);
        }
        for (PermGroup PermGroup : PermPlayer.getGroups()) {
            PermPlayer.removePlayerFromGroup(PermGroup);
        }
        PermGroup group1 = getGroup(group);
        PermPlayer.addPlayerToGroup(group1);
        PermPlayer.save();
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
        for (Map.Entry<String, Boolean> stringBooleanEntry : thisPlayer.getPermissions().entrySet()) {
            perms.put(stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
        for (PermGroup group : thisPlayer.getGroups()) {
            for (Map.Entry<String, Boolean> stringBooleanEntry : group.getPermissions().entrySet()) {
                perms.put(stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
            }
        }
        for (Map.Entry<String, Boolean> stringBooleanEntry : perms.entrySet()) {
            givePermsToPlayer(thisPlayer.getName(), stringBooleanEntry.getKey(), stringBooleanEntry.getValue());
        }
        PermGroup PermGroup = thisPlayer.getGroups().get(0);
        if (PermGroup == null) return;
        String prefix = thisPlayer.prefix == null ? PermGroup.prefix : thisPlayer.prefix;
        String tab_color = thisPlayer.tab_color == null ? PermGroup.tab_color : thisPlayer.tab_color;
        String name_color = thisPlayer.name_color == null ? PermGroup.name_color : thisPlayer.name_color;
        if (tab_color == null) tab_color = name_color;
        this.updatePlayerDisplays(player, prefix, name_color, tab_color);
    }

    public PermGroup createGroup(String name) {
        PermGroup group = new PermGroup(this.database);
        group.name = name;
        group.save();
        return group;
    }

    public void deleteGroup(String name) {
        PermGroup group = getGroup(name);
        if (group == null) return;
        PermPlayer player = new PermPlayer(database);
        player.addPlayerToGroup(group);
        for (GModel gModel : player.findMany()) {
            if (!(gModel instanceof PermPlayer)) continue;
            PermPlayer player1 = (PermPlayer) gModel;
            player1.removePlayerFromGroup(group);
            player1.save();
        }
        group.remove();
    }

    public String getPrefix(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = player.getGroups().get(0);
        String prefix = group.prefix;
        if (player.prefix != null) prefix = player.prefix;
        return prefix;
    }

    public String getSuffix(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = player.getGroups().get(0);
        String prefix = group.suffix;
        if (player.suffix != null) prefix = player.suffix;
        return prefix;
    }

    public String getNameColor(PermPlayer player) {
        if (player.getGroups().size() < 1) return null;
        PermGroup group = player.getGroups().get(0);
        String name_color = group.name_color;
        if (player.name_color != null) name_color = player.name_color;
        return name_color;
    }
}
