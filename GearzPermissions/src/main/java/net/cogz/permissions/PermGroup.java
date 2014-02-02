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
public class PermGroup extends GModel {
    @Getter @BasicField public String name;
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String nameColor;
    @Getter @BasicField public String tabColor;
    @Getter @BasicField final public List<String> permissions = new ArrayList<>();
    @Getter @BasicField final public List<String> inheritances = new ArrayList<>();
    @Getter @BasicField public boolean isDefault;
    @Getter @BasicField public Integer priority;

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
        if (this.permissions.contains(permission)) return;
        this.permissions.add(permission);
        save();
    }

    protected void removePermission(String perm) {
        this.permissions.remove(perm);
        save();
    }

    public void addInheritance(PermGroup permGroup) {
        this.inheritances.add(permGroup.getName());
    }

    public void removeInheritance(PermGroup permGroup) {
        if (this.inheritances.contains(permGroup.getName())) {
            this.inheritances.remove(permGroup.getName());
        }
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
