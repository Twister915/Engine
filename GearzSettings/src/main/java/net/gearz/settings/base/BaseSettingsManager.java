package net.gearz.settings.base;

/**
 * Base implementation of the SettingsManager
 * Documentation is found there.
 */
public interface BaseSettingsManager {
    public boolean hasValue(BaseSetting setting);

    public Object getValue(BaseSetting setting);

    public <T> T getRawValue(BaseSetting setting, Class<T> typeClass) throws IllegalArgumentException;

    <T> T getValue(BaseSetting setting, Class<T> typeClass) throws IllegalArgumentException;

    <T> T getValue(BaseSetting setting, Class<T> typeClass, T defaultValue) throws IllegalArgumentException;

    public void setValue(BaseSetting setting, Object value);

    public void deleteValue(BaseSetting setting);
}
