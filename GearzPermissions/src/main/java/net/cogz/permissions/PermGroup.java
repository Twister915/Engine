package net.cogz.permissions;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 1/24/14.
 */
public class PermGroup extends GModel {
    @Getter @BasicField public String name;
    @Getter @BasicField public String prefix;
    @Getter @BasicField public String suffix;
    @Getter @BasicField public String name_color;
    @Getter @BasicField public String tab_color;
    @Getter @BasicField private Map<String, Boolean> permissions = new HashMap<>();
    @Getter @BasicField public boolean isDefault;

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
}
