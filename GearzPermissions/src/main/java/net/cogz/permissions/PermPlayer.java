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
    @Getter public String name;
    @Getter @BasicField public String uuid;
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String nameColor;
    @Getter @BasicField public String tabColor;
    @Getter @BasicField public String group;
    @Getter @BasicField public List<String> permissions;

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
    public PermPlayer(DB database, String name) {
        this(database);
        this.name = name;
    }

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
        this.permissions.remove(perm);
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
