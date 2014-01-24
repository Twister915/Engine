package net.cogz.permissions;

import com.mongodb.DB;
import com.mongodb.DBObject;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 1/24/14.
 */
public class PermGroup extends GModel {
    @BasicField public String name;
    @BasicField public String prefix;
    @BasicField public String suffix;
    @BasicField public String name_color;
    @BasicField public String tab_color;
    @BasicField private Map<String, Boolean> permissions;
    @BasicField public boolean isDefault;

    public PermGroup() {
        super();
    }

    public PermGroup(DB database) {
        super(database);
    }

    public PermGroup(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    public void addPermission(String perm, boolean value) {
        this.permissions.put(perm, value);
    }

    public void removePermission(String perm) {
        this.permissions.remove(perm);
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

}
