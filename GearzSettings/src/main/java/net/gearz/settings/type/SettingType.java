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

package net.gearz.settings.type;

/**
 * Represents different setting types
 */
public interface SettingType {
    /**
     * Name of the setting type
     *
     * @return name of the setting type
     */
    String getName();

    /**
     * Checks if an object is an instance of this SettingType
     *
     * @param obj object to check
     * @return whether or not the object is an instance of this SettingType
     */
    boolean isInstance(Object obj);

    /**
     * Parses a raw string as this SettingType
     *
     * @param raw raw string to parse
     * @return object parsed from the raw string
     */
    Object parse(String raw);
}
