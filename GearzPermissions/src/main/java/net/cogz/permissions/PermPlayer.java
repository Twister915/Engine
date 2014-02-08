package net.cogz.permissions;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;
import net.tbnr.gearz.activerecord.LinkedObject;

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
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String nameColor;
    @Getter @BasicField public String tabColor;
    @Getter @BasicField public String name;
    @Getter @BasicField @LinkedObject public PermGroup group;
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

    public PermPlayer(DB database, String name) {
        this(database);
        this.name = name.toLowerCase();
    }

    public void setGroup(PermGroup group) {
        this.group = group;
        save();
    }

    public void removeGroup() {
        this.group = null;
        save();
    }

    @SuppressWarnings("unused")
    public boolean isPlayerInGroup(PermGroup group) {
        return this.group.getName().equals(group.getName());
    }

    protected void addPermission(String perm, boolean value) {
        String permission = perm + "," + value;
        if (this.permissions == null) permissions = new ArrayList<>();
        if (this.permissions.contains(permission)) return;
        this.permissions.add(permission);
        save();
    }

    public void removePermission(String perm) {
        this.permissions.remove(perm);
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
