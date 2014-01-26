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
