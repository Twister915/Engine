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

package net.gearz.settings.base;

import java.util.List;

/**
 * Based implementation of the SettingsRegistry
 * Documentation is found there.
 */
public interface BaseSettingsRegistry {
    public BaseSetting getSetting(String query);

    public List<BaseSetting> getSettings();

    public boolean isRegistered(BaseSetting setting);

    public void register(BaseSetting setting);

    public void unregister(BaseSetting setting);
}
