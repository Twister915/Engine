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
    @Getter @BasicField public List<String> permissions = new ArrayList<>();
    @Getter @BasicField public List<String> inheritances = new ArrayList<>();
    @Getter @BasicField public boolean isDefault;
    @Getter @BasicField public Integer priority;
    @Getter @BasicField public String ladder = "default";

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

    public void addPermission(String perm, boolean value) {
        String permission = perm + "," + value;
        this.permissions.add(permission);
        save();
    }

    public void removePermission(String perm) {
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
