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
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 1/24/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class PermPlayer extends GModel {
    @Getter public String name; //Name of the player for local data purposes
    @Getter @BasicField public String uuid; //UUID of the player
    @Getter @BasicField public String prefix; //Player's chat prefix
    @Getter @BasicField public String suffix; //Player's chat suffix
    @Getter @BasicField public String nameColor; //Player's name color in chat
    @Getter @BasicField public String tabColor; //Player's tab color
    @Getter @BasicField public String group; //Player's group
    @Getter @BasicField public List<String> permissions; //List of permissions that the player has

    @SuppressWarnings("unused")
    public PermPlayer() {
        super();
    }

    public PermPlayer(DB database) {
        super(database);
    }

    @SuppressWarnings("unused")
    public PermPlayer(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    @Deprecated
    /**
     * Deprecated in place of searching for players by name.
     */
    public PermPlayer(DB database, String name) {
        this(database);
        this.name = name;
    }

    /**
     * Instance of a PermPlayer created from the UUID
     *
     * @param database called in the super
     * @param uuid UUID of the player to created
     * @param name name of the player to create
     */
    public PermPlayer(DB database, String uuid, String name) {
        this(database);
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Sets a players group
     *
     * @param group group to set to
     */
    public void setGroup(PermGroup group) {
        this.group = group.getName();
        save();
    }

    /**
     * Sets the players group to null
     */
    public void removeGroup() {
        this.group = null;
        save();
    }

    /**
     * Returns whether or not a player is in a group
     * @param group group to check for membership
     * @return whether or not a player is in a group
     */
    public boolean isPlayerInGroup(PermGroup group) {
        return this.group.equals(group.getName());
    }

    /**
     * Adds a permission to a player
     * @param perm permission to add
     * @param value value to set permission to
     */
    protected void addPermission(String perm, boolean value) {
        String permission = perm + "," + value;
        if (this.permissions == null) permissions = new ArrayList<>();
        if (this.permissions.contains(permission) || this.permissions.contains(perm + "," + !value)) return;
        this.permissions.add(permission);
        save();
    }

    /**
     * Removes a player permission
     * @param perm permission to remove
     */
    public void removePermission(String perm) {
        String trueValue = perm + "," + true;
        String falseValue = perm + "," + false;
        if (this.permissions.contains(trueValue)) {
            this.permissions.remove(trueValue);
        } else if (this.permissions.contains(falseValue)) {
            this.permissions.remove(falseValue);
        }
        save();
    }

    /**
     * Whether or not a player has a permission
     *
     * @param perm permission to check
     * @return whether or not a player has a permission
     */
    public boolean hasPermission(String perm) {
        for (String string : this.permissions) {
            String[] s = string.split(",");
            String permission = s[0];
            if (permission.equals(perm)) {
                return true;
            }
        }
        return false;
    }
}
