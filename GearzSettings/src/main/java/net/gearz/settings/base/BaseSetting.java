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
