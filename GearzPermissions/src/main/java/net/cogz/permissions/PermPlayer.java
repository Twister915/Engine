package net.cogz.permissions;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 1/24/14.
 */
public class PermPlayer extends GModel {
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String nameColor;
    @Getter @BasicField public String tabColor;
    @Getter @BasicField private String name;
    @Getter @BasicField private List<PermGroup> groups;
    @Getter @BasicField private List<String> permissions;

    public PermPlayer() {
        super();
    }

    public PermPlayer(DB database) {
        super(database);
    }

    public PermPlayer(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    public PermPlayer(DB database, String name) {
        this(database);
        this.name = name;
    }

    public void addPlayerToGroup(PermGroup group) {
        this.groups.add(group);
    }

    public void removePlayerFromGroup(PermGroup group) {
        this.groups.remove(group);
    }

    public boolean isPlayerInGroup(PermGroup group) {
        return this.groups.contains(group);
    }

    public void addPermission(String perm, boolean value) {
        String permission = perm + "," + value;
        this.permissions.add(permission);
    }

    public void removePermission(String perm) {
        this.permissions.remove(perm);
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
