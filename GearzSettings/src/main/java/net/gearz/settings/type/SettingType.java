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
