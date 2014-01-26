package net.gearz.settings;

import net.gearz.settings.base.BaseSetting;
import net.gearz.settings.base.BaseSettingsManager;
import net.gearz.settings.type.SettingTypeUtil;

/**
 * Extendable settings manager for
 * management of settings. This class
 * can be exteneded and used in almost
 * any case, or implementation.
 */
public abstract class SettingsManager implements BaseSettingsManager {
    /**
     * Gets the raw value of a setting as an Object
     *
     * @param setting setting to get the valud of
     * @return the value of the setting as an object
     */
    public abstract Object getRaw(BaseSetting setting);

    /**
     * Returns whether or not a setting has a set value
     *
     * @param setting setting to check for a value
     * @return whether or not a setting has a value
     */
    @Override
    public boolean hasValue(BaseSetting setting) {
        return this.getValue(setting) != null;
    }

    /**
     * Gets the value of a setting
     *
     * @param setting setting to retrieve the value of
     * @return the value of a setting
     */
    @Override
    public Object getValue(BaseSetting setting) {
        return getValue(setting, setting.getDefaultValue());
    }

    /**
     * Gets the raw value of a setting as a class type
     *
     * @param setting   setting to retrieve the value of
     * @param typeClass class that the value should be retrieved as
     * @return the raw value of the setting as the type of the param typeClass
     * @throws IllegalArgumentException thrown on illegal cast of the value to the class type
     */
    @Override
    public <T> T getRawValue(BaseSetting setting, Class<T> typeClass) throws IllegalArgumentException {
        Object rawValue = this.getRaw(setting);
        if (rawValue != null) {
            return SettingTypeUtil.getValue(rawValue, typeClass);
        } else {
            return null;
        }
    }

    /**
     * Gets the value of a setting
     *
     * @param setting   setting to retrieve the value of
     * @param typeClass class that the value should be retrieved as
     * @param <T>       class type
     * @return the value fo a setting
     * @throws IllegalArgumentException thrown on illegal cast of the value to the class
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(BaseSetting setting, Class<T> typeClass) throws IllegalArgumentException {
        return this.getValue(setting, typeClass, (T) setting.getDefaultValue());
    }

    /**
     * Gets the value of a setting with a default value
     *
     * @param setting      setting to retrieve the value of
     * @param typeClass    class that the value should be retrieved as
     * @param defaultValue generic that specifies the default value
     * @return the value of a setting
     * @throws IllegalArgumentException thrown on illegal cast of value to class
     */
    @Override
    public <T> T getValue(BaseSetting setting, Class<T> typeClass, T defaultValue) throws IllegalArgumentException {
        T value = this.getRawValue(setting, typeClass);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets a value of a setting as an object
     *
     * @param setting      setting to retrieve the value of
     * @param defaultValue default value if the value is not found
     * @return the value of the setting as an object
     */
    private Object getValue(BaseSetting setting, Object defaultValue) {
        Object value = this.getRaw(setting);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
}
