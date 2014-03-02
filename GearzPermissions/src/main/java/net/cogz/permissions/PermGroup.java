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
    @Getter @BasicField public String name;
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String nameColor;
    @Getter @BasicField public String tabColor;
    @Getter @BasicField public List<String> permissions;
    @Getter @BasicField public List<String> inheritances;
    @Getter @BasicField public boolean isDefault;

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

    protected void addPermission(String perm, boolean value) {
        String permission = perm + "," + value;
        if (this.permissions == null) permissions = new ArrayList<>();
        if (this.permissions.contains(permission) || this.permissions.contains(perm + "," + !value)) return;
        this.permissions.add(permission);
        save();
    }

    protected void removePermission(String perm) {
        this.permissions.remove(perm);
        save();
    }

    public void addInheritance(PermGroup permGroup) {
        this.inheritances.add(permGroup.getName());
        save();
    }

    public void removeInheritance(PermGroup permGroup) {
        if (this.inheritances.contains(permGroup.getName())) {
            this.inheritances.remove(permGroup.getName());
        }
        save();
    }

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
