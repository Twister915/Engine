package net.gearz.settings;

import lombok.ToString;
import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.type.SettingType;

import java.util.List;

/**
 * The representation of a Setting
 * Stores data including the name,
 * SettingType, and other information
 * that are required for registering settings
 */
@ToString
public class Setting implements BaseSetting {
    private String name;
    private List<String> aliases;
    private String description;
    private Object defaultValue;
    private SettingType type;

    public Setting(String name, List<String> aliases, String description, SettingType type, Object defaultValue) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public SettingType getType() {
        return this.type;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
