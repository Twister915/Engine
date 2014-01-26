package net.gearz.settings.base;

import net.gearz.settings.type.SettingType;

import java.util.List;

/**
 * Base implementation of a Setting
 */
public interface BaseSetting {
    /**
     * Returns the name of the setting
     * @return name of the setting
     */
    String getName();

    /**
     * Returns the aliases of a setting
     * @return setting aliases
     */
    List<String> getAliases();

    /**
     * Returns the description of a setting
     * @return the setting description
     */
    String getDescription();

    /**
     * Returns the SettingType of a setting
     * @return SettingType of the setting
     */
    SettingType getType();

    /**
     * Returns the default value of a setting
     * @return default setting value
     */
    Object getDefaultValue();
}
