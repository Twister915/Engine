/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.gearz.settings;

import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.base.BaseSettingsRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles registration of Settings
 */
public class SettingsRegistry implements BaseSettingsRegistry {
    /**
     * List of registered settings
     */
    private List<BaseSetting> settings = new ArrayList<>();

    /**
     * Retrieves a setting based on a query.
     * Searches registered settings based on
     * aliases and it's name.
     *
     * @param query name and aliases to search for
     * @return the setting found based on the query
     */
    @Override
    public BaseSetting getSetting(String query) {
        for (BaseSetting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(query)) return setting;
            for (String alias : setting.getAliases()) {
                if (alias.equalsIgnoreCase(query)) return setting;
            }
        }
        return null;
    }

    /**
     * Returns a list of settings
     *
     * @return a list of settings
     */
    @Override
    public List<BaseSetting> getSettings() {
        return this.settings;
    }

    /**
     * Returns whether or not a setting is registered
     *
     * @param setting setting to check
     * @return whether or not a setting is registered
     */
    @Override
    public boolean isRegistered(BaseSetting setting) {
        return this.settings.contains(setting);
    }

    /**
     * Register a setting
     *
     * @param setting setting to register
     */
    @Override
    public void register(BaseSetting setting) {
        this.settings.add(setting);
    }

    /**
     * UnRegister a setting
     *
     * @param setting setting to unregister
     */
    @Override
    public void unregister(BaseSetting setting) {
        this.settings.add(setting);
    }
}
