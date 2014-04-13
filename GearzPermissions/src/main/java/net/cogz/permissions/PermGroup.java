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
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = {"name", "prefix", "nameColor", "tabColor", "suffix"}, callSuper = false)
public class PermGroup extends GModel {
    @Getter @BasicField public String name; //Name of the group
    @Getter @BasicField public String prefix; //Group prefix, overriden by player specific prefixes
    @Getter @BasicField public String suffix; //Group suffix, overriden by player specific suffixes
    @Getter @BasicField public String nameColor; //Group name color in chat, overriden by player specific name colors
    @Getter @BasicField public String tabColor; //Group tab color, overriden by player specific name colors
    @Getter @BasicField public List<String> permissions; //List of permissions that a group has
    @Getter @BasicField public List<String> inheritances; //Groups that this PermGroup inherits
    @Getter @BasicField public boolean isDefault; //Whether or not this is the group that players are automatically added too upon joining

    @SuppressWarnings("unused")
    public PermGroup() {
        super();
    }

    public PermGroup(DB database) {
        super(database);
    }

    @SuppressWarnings("unused")
    public PermGroup(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    /**
     * Adds a permission to a group
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
     * Removes a group permission
     * @param perm permission to remove
     */
    protected void removePermission(String perm) {
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
     * Adds a group inheritance
     *
     * @param permGroup group to add as an inheritance
     */
    public void addInheritance(PermGroup permGroup) {
        this.inheritances.add(permGroup.getName());
        save();
    }

    /**
     * Removes a group inheritance
     *
     * @param permGroup group to remove as a inheritance
     */
    public void removeInheritance(PermGroup permGroup) {
        if (this.inheritances.contains(permGroup.getName())) {
            this.inheritances.remove(permGroup.getName());
        }
        save();
    }

    /**
     * Whether or not a group has a permission
     *
     * @param perm permission to check
     * @return whether or not a group has a permission
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
