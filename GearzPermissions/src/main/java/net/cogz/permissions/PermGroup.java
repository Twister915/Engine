package net.cogz.permissions;

import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;

import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 1/24/14.
 */
public class PermGroup extends GModel {
    @BasicField public String prefix;
    @BasicField public String suffix;
    @BasicField public String name_color;
    @BasicField public String tab_color;
    @BasicField private String name;
    @SuppressWarnings("unused") @BasicField private List<PermGroup> groups;
    @SuppressWarnings("unused") @BasicField private Map<String, Boolean> permissions;

}
